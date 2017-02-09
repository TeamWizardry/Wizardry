package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by LordSaad.
 */
public class BookGui extends GuiBase {

	private static Texture SPRITE_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/book.png"));
	private static Sprite background = SPRITE_SHEET.getSprite("background", 145, 179);

	public BookGui() {
		super(145, 179);

		ComponentSprite componentBackground = new ComponentSprite(background, 0, 0);
		getMainComponents().add(componentBackground);

		ComponentVoid mainIndex = new ComponentVoid(0, 0, background.getWidth(), background.getHeight());
		componentBackground.add(mainIndex);

		String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		InputStream stream;
		String path;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/" + langname + "/index.json");
			path = "documentation/" + langname;
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_US/index.json");
			path = "documentation/en_US";
		}

		if (stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);
			JsonElement json = new JsonParser().parse(reader);

			if (json.isJsonObject() && json.getAsJsonObject().has("index")) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("index");
				int i = 0;
				for (JsonElement element : array) {
					if (element.isJsonObject()) {
						JsonObject chunk = element.getAsJsonObject();
						if (chunk.has("icon") && chunk.has("text") && chunk.has("link") && chunk.get("icon").isJsonPrimitive() && chunk.get("text").isJsonPrimitive() && chunk.get("link").isJsonPrimitive()) {
							ComponentVoid category = new ComponentVoid(15 + ((32 + 5) * i), 15, 32, 32);
							Sprite icon = new Sprite(new ResourceLocation(chunk.get("icon").getAsString()));
							category.BUS.hook(GuiComponent.PostDrawEvent.class, postDrawEvent -> {
								GlStateManager.pushMatrix();
								GlStateManager.enableAlpha();
								GlStateManager.enableBlend();
								if (!postDrawEvent.getComponent().getMouseOver())
									GlStateManager.color(0, 0, 0);
								else GlStateManager.color(0, 0.5f, 1);
								icon.getTex().bind();
								icon.draw((int) ClientTickHandler.getPartialTicks(), category.getPos().getXi(), category.getPos().getYi(), 32, 32);
								GlStateManager.popMatrix();
							});
							category.BUS.hook(GuiComponent.MouseOverEvent.class, mouseOverEvent -> {
								// TODO
							});
							category.BUS.hook(GuiComponent.MouseClickEvent.class, mouseClickEvent -> {

							});
							mainIndex.add(category);
							i++;
						}
					}
				}
			}
		}

	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

}
