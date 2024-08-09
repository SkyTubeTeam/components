package com.github.skytube.components.utils;

import java.io.Closeable;

import android.util.Log;

/**
 * Class to help measure time. Usable in a try block:
 * <code><pre>
 *      try (Stopwatch s = new Stopwatch("some function")) {
 *         ...
 *      }
 * </pre></code>
 */
public final class Stopwatch implements Closeable {
    private final long start;
    private final String name;

    public Stopwatch(String name) {
        this.start = System.currentTimeMillis();
        this.name = name;
    }

    @Override
    public void close() {
        final long elapsed = System.currentTimeMillis() - start;
        Log.i("Stopwatch", "Elapsed " + elapsed + " ms for " + name);
    }
}
