package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonElement;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.api.book.hierarchy.category.Category;
import kotlin.Unit;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.getRendererFor;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH;

public class ComponentCategoryButton extends GuiComponent {

	public ComponentCategoryButton(int posX, int posY, int width, int height, GuiBook book, @Nonnull Category category) {
		super(posX, posY, width, height);

		JsonElement icon = category.icon;

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
			book.placeInFocus(category);
		});

		// ------- BUTTON RENDERING AND ANIMATION ------- //
		Runnable iconMask = getRendererFor(icon, new Vec2d(24, 24), true);
		{
			BUS.hook(GuiComponentEvents.PostDrawEvent.class, (GuiComponentEvents.PostDrawEvent event) -> {
				GlStateManager.color(0, 0, 0);
				iconMask.run();
			});

			render.getTooltip().func((Function<GuiComponent, List<String>>) guiComponent -> {
				List<String> list = new ArrayList<>();
				TooltipHelper.addToTooltip(list, category.titleKey);

				String desc = category.descKey;
				String used = LibrarianLib.PROXY.canTranslate(desc) ? desc : desc + "0";
				if (LibrarianLib.PROXY.canTranslate(used)) {
					TooltipHelper.addToTooltip(list, used);
					int i = 0;
					while (LibrarianLib.PROXY.canTranslate(desc + (++i)))
						TooltipHelper.addToTooltip(list, desc + i);
				}

				for (int i = 1; i < list.size(); i++)
					list.set(i, TextFormatting.GRAY + list.get(i));
				return list;
			});

			ComponentAnimatableVoid circleWipe = new ComponentAnimatableVoid(0, 0, width, height);
			add(circleWipe);
			circleWipe.getTransform().setTranslateZ(100);

			circleWipe.clipping.setClipToBounds(true);
			circleWipe.clipping.setCustomClipping(() -> {

				GlStateManager.disableTexture2D();
				GlStateManager.disableCull();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				for (int i = 0; i <= 10; i++) {
					float angle = (float) (i * Math.PI * 2 / 10);
					float x1 = (float) (12 + MathHelper.cos(angle) * circleWipe.animX);
					float y1 = (float) (12 + MathHelper.sin(angle) * circleWipe.animX);
					buffer.pos(x1, y1, 100).color(0f, 1f, 1f, 1f).endVertex();
				}
				tessellator.draw();

				return Unit.INSTANCE;
			});

			final double radius = 16;

			circleWipe.BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {

				BasicAnimation mouseInAnim = new BasicAnimation<>(circleWipe, "animX");
				mouseInAnim.setDuration(10);
				mouseInAnim.setEasing(Easing.easeOutQuint);
				mouseInAnim.setTo(radius);
				event.component.add(mouseInAnim);
			});

			circleWipe.BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {

				BasicAnimation mouseOutAnim = new BasicAnimation<>(circleWipe, "animX");
				mouseOutAnim.setDuration(10);
				mouseOutAnim.setEasing(Easing.easeOutQuint);
				mouseOutAnim.setTo(0);
				event.component.add(mouseOutAnim);
			});

			Color wipeColor = category.color;

			circleWipe.BUS.hook(GuiComponentEvents.PostDrawEvent.class, (GuiComponentEvents.PostDrawEvent event) -> {
				GlStateManager.color(wipeColor.getRed(), wipeColor.getGreen(), wipeColor.getBlue());
				GlStateManager.enableAlpha();
				GlStateManager.disableCull();
				GL11.glEnable(GL_POLYGON_SMOOTH);
				iconMask.run();
				GL11.glDisable(GL_POLYGON_SMOOTH);
				GlStateManager.enableCull();
			});
		}
		// ------- BUTTON RENDERING AND ANIMATION ------- //
	}
}
