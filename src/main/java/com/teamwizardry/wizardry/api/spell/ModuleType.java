package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.wizardry.api.module.Module;

/**
 * Created by Saad on 6/21/2016.
 */
public enum ModuleType {
    BOOLEAN, EFFECT, EVENT, MODIFIER, SHAPE;
    
	public final Sprite backgroundSprite;
	private ModuleType() {
		backgroundSprite = Module.STATIC_ICON_SHEET.getSprite(this.toString().toLowerCase(), 24, 24);
	}
}
