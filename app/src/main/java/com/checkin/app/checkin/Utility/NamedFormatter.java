package com.checkin.app.checkin.Utility;

import java.util.Map;
import java.util.regex.Pattern;

import static com.checkin.app.checkin.Utility.Utils.replaceAll;

public class NamedFormatter {
    private static final Pattern RE = Pattern.compile(
            "\\\\(.)" +         // Treat any character after a backslash literally
            "|" +
            "(%\\(([^)]+)\\))"  // Look for %(keys) to replace
    );
    NamedFormatter() {}

    public static String format(String fmt, Map<String, String> values) {
        return replaceAll(fmt, RE.matcher(fmt), matchResult -> matchResult.group(1) != null ?
                matchResult.group(1) : Utils.getOrDefault(values, matchResult.group(3), matchResult.group(2)));
    }
}
