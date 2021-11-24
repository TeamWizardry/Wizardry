package com.teamwizardry.wizardry.common.utils;


import java.awt.*;
import java.util.List;

public class ColorUtils {

    public static Color generateRandomColor() {
        return new Color(RandUtil.nextFloat(), RandUtil.nextFloat(), RandUtil.nextFloat());
    }

    public static Color mergeColors(Color color1, Color color2) {
        return new Color((int) (color1.getRed() * 0.5 + color2.getRed() * 0.5),
                (int) (color1.getGreen() * 0.5 + color2.getGreen() * 0.5),
                (int) (color1.getBlue() * 0.5 + color2.getBlue() * 0.5));
    }

    public static Color[] mergeColorSets(List<Color[]> colors) {
        Color[] resultSet = null;

        for (Color[] colorSet : colors) {
            if (resultSet == null) {
                resultSet = colorSet;
                continue;
            }
            for (int i = 0, resultSetLength = resultSet.length; i < resultSetLength; i++) {
                Color resultColor = resultSet[i];
                resultSet[i] = mergeColors(resultColor, colorSet[i]);
            }
        }

        return resultSet;
    }
}
