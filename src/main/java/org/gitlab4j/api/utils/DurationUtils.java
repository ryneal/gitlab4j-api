package org.gitlab4j.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {

    private static final String[] TIME_UNITS = { "mo", "w", "d", "h", "m", "s" };
    private static final int[] TIME_UNIT_MULTIPLIERS = { 60 * 60 * 24 * 30, 60 * 60 * 24 * 7, 60 * 60 * 24, 60 * 60, 60, 1 };
    private static Pattern durationPattern = Pattern.compile("(\\s*(\\d+)(mo|[wdhms]))");

    /**
     * Create a human readable duration string from seconds.
     *
     * @param durationSeconds the total number of seconds in the duration
     * @return a human readable string representing the duration
     */
    public static final String toString(int durationSeconds) {

        int months = durationSeconds / TIME_UNIT_MULTIPLIERS[0];
        int weeks = (durationSeconds - months * TIME_UNIT_MULTIPLIERS[0]) / TIME_UNIT_MULTIPLIERS[1];
        int days = (durationSeconds - months * TIME_UNIT_MULTIPLIERS[0] - weeks * TIME_UNIT_MULTIPLIERS[1]) / TIME_UNIT_MULTIPLIERS[2];
        int seconds = durationSeconds - (months * TIME_UNIT_MULTIPLIERS[0]) - (weeks * TIME_UNIT_MULTIPLIERS[1]) - (days * TIME_UNIT_MULTIPLIERS[2]);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        StringBuilder buf = new StringBuilder();
        if (months > 0) {

            buf.append(months).append("mo");
            if (weeks > 0) {
                buf.append(weeks).append('w');
            }

            if (seconds > 0) {
                buf.append(days).append('d').append(hours).append('h').append(minutes).append('m').append(seconds).append('s');
            } else if (minutes > 0) {
                buf.append(days).append('d').append(hours).append('h').append(minutes).append('m');
            } else if (hours > 0) {
                buf.append(days).append('d').append(hours).append('h');
            } else if (days > 0) {
                buf.append(days).append('d');
            }

        } else if (weeks > 0) {

            buf.append(weeks).append('w');
            if (seconds > 0) {
                buf.append(days).append('d').append(hours).append('h').append(minutes).append('m').append(seconds).append('s');
            } else if (minutes > 0) {
                buf.append(days).append('d').append(hours).append('h').append(minutes).append('m');
            } else if (hours > 0) {
                buf.append(days).append('d').append(hours).append('h');
            }

        } else  if (days > 0) {

            buf.append(days).append('d');
            if (seconds > 0) {
                buf.append(hours).append('h').append(minutes).append('m').append(seconds).append('s');
            } else if (minutes > 0) {
                buf.append(hours).append('h').append(minutes).append('m');
            } else if (hours > 0) {
                buf.append(hours).append('h');
            }

        } else if (hours > 0) {

            buf.append(hours).append('h');
            if (seconds > 0) {
                buf.append(minutes).append('m').append(seconds).append('s');
            } else if (minutes > 0) {
                buf.append(minutes).append('m');
            }

        } else if (minutes > 0) {

            buf.append(minutes).append('m');
            if (seconds > 0) {
                buf.append(seconds).append('s');
            }

        } else {
            buf.append(' ').append(seconds).append('s');
        }

        return (buf.toString());
    }

    /**
     * Parses a human readable duration string and calculates the number of seconds it represents.
     *
     * @param durationString the human readable duration
     * @return the total number of seconds in the duration
     */
    public static final int parse(String durationString) {

        durationString = durationString.toLowerCase();
        Matcher matcher = durationPattern.matcher(durationString);

        int currentUnitIndex = -1;
        int seconds = 0;
        Boolean validDuration = null;

        while (matcher.find() && validDuration != Boolean.FALSE) {
            
            validDuration = true;

            int numGroups = matcher.groupCount();
            if (numGroups == 3) {

                String unit = matcher.group(3);
                int nextUnitIndex = getUnitIndex(unit);
                if (nextUnitIndex > currentUnitIndex) {

                    currentUnitIndex = nextUnitIndex;
                    try {
                        seconds += Long.parseLong(matcher.group(2)) * TIME_UNIT_MULTIPLIERS[nextUnitIndex];
                    } catch (NumberFormatException nfe) {
                        validDuration = false;
                    }
                } else {
                    validDuration = false;
                }

            } else {
                validDuration = false;
            }
        }

        if (validDuration != Boolean.TRUE) {
            throw new IllegalArgumentException(String.format("'%s' is not a valid duration", durationString));
        }

        return (seconds);
    }

    private static final int getUnitIndex(String unit) {

        for (int i = 0; i < TIME_UNITS.length; i++) {
            if (unit.equals(TIME_UNITS[i]))
                return (i);
        }

        return (-1);
    }
}
