package com.teamwizardry.wizardry.api.module.attribute;

public class Attribute {

    public static final Attribute

            POWER = new Attribute("Power"),
            DURATION = new Attribute("Duration"),
            RADIUS = new Attribute("Radius"),
            PIERCE = new Attribute("Pierce"),
            SILENT = new Attribute("Silent"),
            SPEED = new Attribute("Speed"),
            KNOCKBACK = new Attribute("Knockback"),
            PROJ_COUNT = new Attribute("Projectile Count"),
            SCATTER = new Attribute("Scatter"),
            CRIT_CHANCE = new Attribute("Crit Chance"),
            CRIT_DAMAGE = new Attribute("Crit Damage"),
            DISTANCE = new Attribute("Distance"),
            DAMAGE = new Attribute("Damage"),

    MANA = new Attribute("Mana"),
            BURNOUT = new Attribute("Burnout");

    public final String name;

    public Attribute(String name) {
        this.name = name;
    }

}
