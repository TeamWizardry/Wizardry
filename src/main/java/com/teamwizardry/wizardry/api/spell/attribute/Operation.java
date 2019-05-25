package com.teamwizardry.wizardry.api.spell.attribute;

/**
 * All supported modifier operations. Operations are performed from top to bottom of this file.
 */
public enum Operation {
	/**
	 * Operation: output = value + modifier
	 */
	ADD {
		@Override
		public float apply(float value, float modifier) {
			return value + modifier;
		}
	},
	/**
	 * Operation: output = value * modifier
	 */
	MULTIPLY {
		@Override
		public float apply(float value, float modifier) {
			return value * modifier;
		}
	},
	/**
	 * Operation: output = value * (1 + modifier)
	 */
	BONUS_MULTIPLY {
		@Override
		public float apply(float value, float modifier) {
			return value * (1 + modifier);
		}
	};

	public abstract float apply(float value, float modifier);
}
