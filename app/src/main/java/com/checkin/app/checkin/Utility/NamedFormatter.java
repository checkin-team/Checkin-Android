package com.checkin.app.checkin.Utility;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static com.checkin.app.checkin.Utility.Util.replaceAll;

public class NamedFormatter {
    private static final Pattern RE = Pattern.compile(
            "\\\\(.)" +         // Treat any character after a backslash literally
            "|" +
            "(%\\(([^)]+)\\))"  // Look for %(keys) to replace
    );
    NamedFormatter() {}

    public static String format(String fmt, Map<String, String> values) {
        return replaceAll(fmt, RE.matcher(fmt), new Util.MatchResultFunction() {
            @Override
            public String apply(MatchResult matchResult) {
                return matchResult.group(1) != null ?
                        matchResult.group(1) : Util.getOrDefault(values, matchResult.group(3), matchResult.group(2));
            }
        });
    }
}
