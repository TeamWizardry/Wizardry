package com.teamwizardry.wizardry.api.modules.attribute;

public class Attribute {

    public static final Attribute

            POWER = new Attribute("power"),
            DURATION = new Attribute("duration"),
            RADIUS = new Attribute("radius"),
            PIERCE = new Attribute("pierce"),
            SILENT = new Attribute("silent"),
            SPEED = new Attribute("speed"),
            KNOCKBACK = new Attribute("knockback"),
            PROJ_COUNT = new Attribute("projectile count"),
            SCATTER = new Attribute("scatter"),
            CRIT_CHANCE = new Attribute("crit chance"),
            CRIT_DAMAGE = new Attribute("crit damage"),
            DISTANCE = new Attribute("distance"),
            DAMAGE = new Attribute("damage"),

            COST = new Attribute("cost"),
            BURNOUT = new Attribute("burnout");

    public final String name;

    public Attribute(String name) {
        this.name = name;
    }

}
