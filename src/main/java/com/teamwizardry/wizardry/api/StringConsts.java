package com.teamwizardry.wizardry.api;

public class StringConsts {

	// api.capability.mana
	public static final String MANA = "mana";
	public static final String MAX_MANA = "max_mana";
	public static final String BURNOUT = "burnout";
	public static final String MAX_BURNOUT = "max_burnout";

	// api.task
	public static final String QUEUE = "queue";
	public static final String RESOURCE_LOCATION = "resource_location";
	public static final String DATA = "data";
	public static final String CHAINED_TO = "chained_to";
	public static final String STORAGE_NBT = "storage_nbt";
    public static final String TASK_STORAGE = "task_storage";
    public static final String TASK_CONTROLLER = "task_controller";

    // api.entity.fairy
    public static final String HAPPY = "happy";
    public static final String TRAPPED = "trapped";
    public static final String DEPRESSED = "depressed";
    public static final String SLAVED = "slaved";

    // api.entity.fairy
    public static final String FAIRY_STATE = "fairy_state";
    public static final String SECONDARY_COLOR = "secondary_color";
    public static final String PRIMARY_COLOR = "primary_color";

    // api.task & common.core.task
    public static final String DIRECTION = "direction";
    public static final String BLOCK_POS = "block_pos";
    public static final String INITIAL_MILLIS = "initial_millis";

    // api.spell
    public static final String PATTERN = "pattern";
    public static final String PATTERN_TYPE = "pattern_type";
    public static final String TARGET_TYPE = "target_type";
    public static final String ATTRIBUTE_VALUES = "attribute_values";
    public static final String MANA_COST = "mana_cost";
    public static final String BURNOUT_COST = "burnout_cost";
    public static final String NEXT_SHAPE = "next_shape";
    public static final String EFFECTS = "effects";
    public static final String CASTER = "caster";
    public static final String EXTRA_DATA = "extra_data";

    public static final String SPELL_DATA = "spell_data";
    public static final String COLOR = "color";
    public static final String RAND = "rand";
    public static final String LAST_CAST = "last_cast";
    public static final String LAST_COOLDOWN = "last_cooldown";
    public static final String PURITY = "purity";
    public static final String PURITY_OVERRIDE = "purity_override";
    public static final String COMPLETE = "complete";
    public static final int NACRE_PURITY_CONVERSION = 30 * 20; // 30 seconds for max purity, 0/60 for no purity
}