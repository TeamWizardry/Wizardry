package com.teamwizardry.wizardry.api.util.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PathUtils {

    private PathUtils() {
    }

    public static String resolve(String path) {

        List<String> parts = new ArrayList<>();
        parts.addAll(Arrays.asList(path.split("/")));

        for (int i = 0; i < parts.size(); i++) {
            if ("".equals(parts.get(i)) || ".".equals(parts.get(i))) {
                parts.remove(i);
                i--;
                continue;
            }
            if ("..".equals(parts.get(i))) {
                parts.remove(i);
                i--;
                if (i >= 0) {
                    parts.remove(i);
                    i--;
                }
                continue;
            }
        }

        return "/" + String.join("/", parts);
    }

    public static String resolve(String parent, String relative) {
        if (relative.startsWith("/"))
            return resolve(relative);
        return resolve(parent + "/" + relative);
    }

    public static String parent(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        int index = path.lastIndexOf("/");
        return path.substring(0, index);
    }

}
