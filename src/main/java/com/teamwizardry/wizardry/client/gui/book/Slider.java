package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

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
			//if (component.hasTag("kill")) {
			//	if (component.getPos().getXi() < 0)
			//		component.setPos(component.getPos().add(5, 0));
			//	else component.invalidate();
//
			//	return;
			//}
			//if (component.getPos().getXi() > -130) {
			//	double t = Math.abs(component.getPos().getXi());
			//	double x = (-130) * (MathHelper.sin((float) (t * Math.PI / 2)));
			//	component.setPos(new Vec2d(x, 0));
			//}
		});
	}
}
