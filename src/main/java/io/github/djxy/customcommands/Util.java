package io.github.djxy.customcommands;

import java.util.ArrayList;

/**
 * Created by Samuel on 2016-07-28.
 */
public class Util {

    public static String[] split(String str) {
        ArrayList<String> words = new ArrayList<>();
        StringBuilder builder = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) <= 32 && builder.length() != 0) {
                words.add(builder.toString());
                builder = new StringBuilder(str.length() - i);
            }
            else if (str.charAt(i) > 32)
                builder.append(str.charAt(i));
        }

        if (builder.length() != 0)
            words.add(builder.toString());

        if (str.trim().length() != 0 && str.charAt(str.length() - 1) <= 32 && words.size() == 0)
            words.add("");

        return words.toArray(new String[words.size()]);
    }

}
