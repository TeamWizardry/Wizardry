package com.teamwizardry.wizardry.common.utils

import java.awt.Color

object ColorUtils {
    fun generateRandomColor(): Color {
        return Color(RandUtil.nextFloat(), RandUtil.nextFloat(), RandUtil.nextFloat())
    }

    private fun mergeColors(color1: Color, color2: Color): Color {
        return Color(
            (color1.red * 0.5 + color2.red * 0.5).toInt(),
            (color1.green * 0.5 + color2.green * 0.5).toInt(),
            (color1.blue * 0.5 + color2.blue * 0.5).toInt()
        )
    }

    fun mergeColorSets(colors: List<Array<Color>>): Array<Color>? {
        var resultSet: Array<Color>? = null
        for (colorSet in colors) {
            if (resultSet == null) {
                resultSet = colorSet
                continue
            }
            var i = 0
            val resultSetLength = resultSet.size
            while (i < resultSetLength) {
                val resultColor = resultSet[i]
                resultSet[i] = mergeColors(resultColor, colorSet[i])
                i++
            }
        }
        return resultSet
    }
}