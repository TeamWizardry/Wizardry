package com.teamwizardry.wizardry.api.utils;

import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.util.math.MathHelper;

public class MathUtils {

    public static Vec2d genRandomDotInCircle(float radius) {
        float theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
        float r = radius * RandUtil.nextFloat();
        float x = r * MathHelper.cos(theta);
        float y = r * MathHelper.sin(theta);

        return new Vec2d(x, y);
    }

    public static Vec2d genCirclePerimeterDot(float radius, float angle) {
        float theta = 2.0f * (float) Math.PI * angle;
        float x = radius * MathHelper.cos(theta);
        float y = radius * MathHelper.sin(theta);

        return new Vec2d(x, y);
    }
}
