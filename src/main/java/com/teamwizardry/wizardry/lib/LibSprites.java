package com.teamwizardry.wizardry.lib;

import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LordSaad44
 */
public class LibSprites {

	public static class Worktable {
		/**
		 * http://i.imgur.com/glVH4d7.png
		 */
		public static final Texture SPRITE_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"));

		/**
		 * http://i.imgur.com/CCDlGu1.png
		 */
		public static final Sprite TAB_SIDE = SPRITE_SHEET.getSprite("tab_side", 24, 24);
		public static final Sprite TAB_TOP = SPRITE_SHEET.getSprite("tab_top", 24, 24);

		/**
		 * http://i.imgur.com/HZiKr3v.png
		 */
		public static final Sprite MODULE_SLOT_SINGLE = SPRITE_SHEET.getSprite("module_slot_single", 32, 32);
		public static final Sprite MODULE_SLOT_LEFT = SPRITE_SHEET.getSprite("module_slot_l", 32, 32);
		public static final Sprite MODULE_SLOT_RIGHT = SPRITE_SHEET.getSprite("module_slot_r", 32, 32);
		public static final Sprite MODULE_SLOT_LEFT_RIGHT = SPRITE_SHEET.getSprite("module_slot_lr", 32, 32);

		/**
		 * http://i.imgur.com/0q9uLmn.png
		 */
		public static final Sprite MODULE_DEFAULT = SPRITE_SHEET.getSprite("module_default", 24, 24);
		public static final Sprite MODULE_DEFAULT_GLOW = SPRITE_SHEET.getSprite("module_default_glow", 24, 24);
		public static final Sprite MODULE_ICON_MISSING = SPRITE_SHEET.getSprite("module_icon_missing", 16, 16);

		/**
		 * http://i.imgur.com/FG1q61z.png
		 */
		public static final Sprite SCROLL_SLIDER_VERTICAL = SPRITE_SHEET.getSprite("scroll_slider_v", 8, 16);
		public static final Sprite SCROLL_SLIDER_HORIZONTAL = SPRITE_SHEET.getSprite("scroll_slider_h", 16, 8);

		/**
		 * http://i.imgur.com/0OJKnVk.png
		 */
		public static final Sprite SCROLL_GROOVE_VERTICAL_MIDDLE = SPRITE_SHEET.getSprite("scroll_groove_v", 12, 12);
		public static final Sprite SCROLL_GROOVE_VERTICAL_TOP = SPRITE_SHEET.getSprite("scroll_groove_v_top", 12, 12);
		public static final Sprite SCROLL_GROOVE_VERTICAL_BOTTOM = SPRITE_SHEET.getSprite("scroll_groove_v_bottom", 12, 12);

		/**
		 * http://i.imgur.com/pS0X2Jz.png
		 */
		public static final Sprite SCROLL_GROOVE_HORIZONTAL_MIDDLE = SPRITE_SHEET.getSprite("scroll_groove_h", 12, 12);
		public static final Sprite SCROLL_GROOVE_HORIZONTAL_LEFT = SPRITE_SHEET.getSprite("scroll_groove_h_left", 12, 12);
		public static final Sprite SCROLL_GROOVE_HORIZONTAL_RIGHT = SPRITE_SHEET.getSprite("scroll_groove_h_right", 12, 12);

		/**
		 * http://i.imgur.com/yoqqA9T.png
		 */
		public static final Sprite BOX = SPRITE_SHEET.getSprite("_whatisthis_box_thing", 16, 16);
		public static final Sprite BOX_HORIZONTAL_MIDDLE = SPRITE_SHEET.getSprite("_whatisthis_box_h_thing", 16, 13);
		public static final Sprite BOX_HORIZONTAL_LEFT = SPRITE_SHEET.getSprite("_whatisthis_box_h_left_thing", 16, 13);
		public static final Sprite BOX_HORIZONTAL_RIGHT = SPRITE_SHEET.getSprite("_whatisthis_box_h_right_thing", 16, 13);

		/**
		 * http://i.imgur.com/hMnirIG.png
		 */
		public static final Sprite GRID = SPRITE_SHEET.getSprite("_whatisthis_grid_thing", 24, 24);
	}
}
