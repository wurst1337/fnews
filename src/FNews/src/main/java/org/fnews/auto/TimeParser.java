package org.fnews.auto;

public class TimeParser {

    public static long toTicks(String input) {
        input = input.toLowerCase();
        long value = Long.parseLong(input.replaceAll("[^0-9]", ""));

        if (input.endsWith("s")) return value * 20;
        if (input.endsWith("m")) return value * 20 * 60;
        if (input.endsWith("h")) return value * 20 * 60 * 60;

        return value * 20;
    }
}
