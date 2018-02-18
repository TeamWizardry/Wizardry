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
		public double apply(double value, double modifier) {
			return value + modifier;
		}
	},
	/**
	 * Operation: output = value * modifier
	 */
	MULTIPLY {
		@Override
		public double apply(double value, double modifier) {
			return value * modifier;
		}
	},
	/**
	 * Operation: output = value * (1 + modifier)
	 */
	BONUS_MULTIPLY {
		@Override
		public double apply(double value, double modifier) {
			return value * (1 + modifier);
		}
	};

	public abstract double apply(double value, double modifier);
}
