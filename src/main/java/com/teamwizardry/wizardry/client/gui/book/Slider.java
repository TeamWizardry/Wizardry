package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Created by LordSaad.
 */
public class Slider {

	private static Texture sliders = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/sliders.png"));
	private static Sprite bg = sliders.getSprite("blue", 133, 37);

	public ComponentVoid component;

	public Slider(String text) {
		component = new ComponentVoid(0, 0, bg.getWidth(), bg.getHeight());
		component.setEnabled(false);
		component.addTag("slider");

		ComponentSprite compBG = new ComponentSprite(bg, 0, 0, bg.getWidth(), bg.getHeight());
		compBG.setEnabled(false);
		component.add(compBG);

		ComponentText compText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		compText.setEnabled(false);
		compText.getText().setValue(text);
		compText.getUnicode().setValue(true);
		compText.getWrap().setValue(120);
		component.add(compText);

		component.BUS.hook(GuiComponent.ComponentTickEvent.class, componentTickEvent -> {
			double t = -1, tmax = 5;
			float finalLoc = -130;
			float x;
			if (!component.hasTag("kill")) {
				for (Object tag : component.getTags()) {
					Minecraft.getMinecraft().player.sendChatMessage(tag + "");
					if (tag instanceof String && ((String) tag).startsWith("t:")) {
						t = Double.parseDouble(((String) tag).split(":")[1]);
						if (t < tmax) {
							component.removeTag(tag);
							component.addTag("t:" + (t + 0.5));
						}
						break;
					}
				}
				if (t == -1) {
					component.addTag("t:" + 0);
					t = 0;
				}
				if (t >= tmax) return;

				x = finalLoc * MathHelper.sin((float) (Math.PI / 2 * ((t + ClientTickHandler.getPartialTicks()) / tmax)));

			} else {
				for (Object tag : component.getTags()) {
					Minecraft.getMinecraft().player.sendChatMessage(tag + "");
					if (tag instanceof String && ((String) tag).startsWith("t:")) {
						t = Float.parseFloat(((String) tag).split(":")[1]);
						if (t < tmax) {
							component.removeTag(tag);
							component.addTag("t:" + (t - 0.5));
						}
						break;
					}
				}
				if (t == -1) component.addTag("t:" + tmax);
				if (t <= 0) return;

				x = finalLoc * MathHelper.sin((float) (Math.PI / 2 * ((t + ClientTickHandler.getPartialTicks()) / tmax)));
			}

			component.setPos(new Vec2d(x, component.getPos().getY()));
		});
	}
}
