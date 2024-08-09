package com.github.skytube.components.utils;


import android.database.Cursor;

public final class Column {

    private final String name;
    private final String type;
    private final String modifier;

    public Column(final String name, final String type) {
        this.name = name;
        this.type = type;
        this.modifier = null;
    }

    public Column(final String name, final String type, String modifier) {
        this.name = name;
        this.type = type;
        this.modifier = modifier;
    }

    public String format() {
        return name + ' ' + type + (modifier != null ? " " + modifier : "");
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public String modifier() {
        return modifier;
    }

    public int getColumn(Cursor cursor) {
        return cursor.getColumnIndex(name);
    }

}
