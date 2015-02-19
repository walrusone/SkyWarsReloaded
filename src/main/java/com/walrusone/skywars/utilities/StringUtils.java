package com.walrusone.skywars.utilities;

import java.util.List;

public class StringUtils {

    public static String toString(String[] args, char color1, char color2) {
        StringBuilder stringBuilder = new StringBuilder();

        if (args.length > 1) {
            for (int iii = 0; iii < args.length - 1; iii++) {
                stringBuilder.append("\247");
                stringBuilder.append(color1);
                stringBuilder.append(args[iii]);
                stringBuilder.append("\247");
                stringBuilder.append(color2);
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append("\247");
        stringBuilder.append(color1);
        stringBuilder.append(args[args.length - 1]);

        return stringBuilder.toString();
    }

    public static String toString(List<String> args, char color1, char color2) {
        return toString(args.toArray(new String[args.size()]), color1, color2);
    }

    public static String formatScore(int score) {
        return formatScore(score, "");
    }

    public static String formatScore(int score, String note) {
        char color = '7';

        if (score > 0) {
            color = 'a';
        } else if (score < 0) {
            color = 'c';
        }

        return "\247" + color + "(" + (score > 0 ? "+" : "") + score + " Score" + ")";
    }
}
