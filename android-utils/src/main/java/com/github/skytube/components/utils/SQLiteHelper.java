package com.github.skytube.components.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public final class SQLiteHelper {
    private SQLiteHelper() {
    }

    /**
     * Execute a <b>constant</b> query, and return the number in the first row, first column.
     *
     * @param db the database to execute on.
     * @param query the query to execute
     * @param selectionArgs the arguments for the query.
     * @return a number.
     */
    public static Integer executeQueryForInteger(SQLiteDatabase db, String query, String[] selectionArgs, Integer defaultValue) {
        try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return defaultValue;
    }

    /**
     * Execute a <b>constant</b> query, and return the number in the first row, first column.
     *
     * @param query the query to execute
     * @return a number.
     */
    public static Integer executeQueryForInteger(SQLiteDatabase db, String query, Integer defaultValue) {
        return executeQueryForInteger(db, query, null, defaultValue);
    }

    public static Long getOptionalLong(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndexOrThrow(columnName);
        return cursor.isNull(columnIndex) ? null : cursor.getLong(columnIndex);
    }

    public static Long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    /**
     * Execute the given sql updates, one-by-one. Throws an exception if any of them fails.
     */
    public static void execSQLUpdates(SQLiteDatabase db, String[] sqlUpdates) {
        for (String sqlUpdate : sqlUpdates) {
            db.execSQL(sqlUpdate);
        }
    }

    public static void continueOnError(SQLiteDatabase db, String update) {
        try {
            db.execSQL(update);
        } catch (SQLiteException e) {
            error(e, "Unable to execute %s , because: %s", update, e.getMessage());
        }
    }

    public static void addColumn(SQLiteDatabase db, String tableName, Column column) {
        try {
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + column.format());
        } catch (SQLiteException e) {
            error(e, "Unable to add '%s'  '%s' to table: '%s', because: %s", column.name(), column.type(), tableName, e.getMessage());
        }
    }

    public static void createIndex(SQLiteDatabase db, String indexName, String tableName, Column... columns) {
        db.execSQL("CREATE INDEX " + indexName + " ON " + tableName + "(" + listColumns(true, columns) + ")");
    }

    public static String getCreateTableCommand(String tableName, Column... columns) {
        return "CREATE TABLE " + tableName + " (" + listColumns(false, columns) + ")";
    }

    public static void createTable(SQLiteDatabase db, String tableName, Column... columns) {
        try {
            db.execSQL(getCreateTableCommand(tableName, columns));
        } catch (SQLiteException e) {
            error(e, "Unable to create table: '%s', because: %s", tableName, e.getMessage());
            throw e;
        }
    }

    public static void updateTableSchema(SQLiteDatabase db, String tableName, String newTableCreateStatement, String migration) {
        db.execSQL("ALTER TABLE " + tableName + " RENAME TO old_" + tableName);
        db.execSQL(newTableCreateStatement);
        db.execSQL(migration + " from old_" + tableName);
        db.execSQL("DROP TABLE old_" + tableName);
    }

    private static String listColumns(boolean justNames, final Column[] columns) {
        boolean first = true;
        final StringBuilder sql = new StringBuilder();
        for (Column col : columns) {
            if (col == null) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sql.append(", ");
            }
            if (justNames) {
                sql.append(col.name());
            } else {
                sql.append(col.format());
            }
        }
        return sql.toString();
    }

    private static void error(Throwable error, String format, Object... args) {
        Log.e(SQLiteHelper.class.getSimpleName(), format(format, args), error);
    }

    private static String format(String format, Object... args) {
        return args.length > 0 ? String.format(format, args) : format;
    }

}
