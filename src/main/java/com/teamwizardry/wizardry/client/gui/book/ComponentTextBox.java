package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Consumer;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.BOOKMARK;
import static com.teamwizardry.wizardry.client.gui.book.GuiBook.MAGNIFIER;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class ComponentTextBox extends ComponentBookMark {

	private static int searchBarCount = 0;
	private static ComponentTextBox focusedTextBox = null;

	private int cursor = 0;
	private int selectionCursor = -1;
	private int cursorRenderFlashingCooldown = 0;

	private String input = "";
	private String select = "";

	private boolean focused = false;

	private ComponentText text;

	public ComponentTextBox(GuiBook book, int id, @Nullable Consumer<String> onType, @Nullable Consumer<String> onEnter) {
		super(book, MAGNIFIER, id);

		searchBarCount++;

		clipping.setClipToBounds(true);

		ComponentVoid textClipWrapper = new ComponentVoid(0, 0, getSize().getXi() - 22, getSize().getYi());
		textClipWrapper.clipping.setClipToBounds(true);
		add(textClipWrapper);

		text = new ComponentText(2, 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		text.getText().setValue("");
		text.getTransform().setTranslateZ(100);
		textClipWrapper.add(text);

		BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			cursorRenderFlashingCooldown = cursorRenderFlashingCooldown > 0 ? --cursorRenderFlashingCooldown : 0;
		});

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			if (focused) {
				focused = false;
				focusedTextBox = null;
			} else {
				if (focusedTextBox != null) {
					focusedTextBox.focused = false;
					focusedTextBox.updateState();
				}
				focused = true;
				focusedTextBox = this;
			}
			updateState();
		});

		// HANDLE ALL TEXT MANIPULATION HERE
		BUS.hook(GuiComponentEvents.KeyDownEvent.class, keyDownEvent -> {
			int cursorCooldown = 50;

			if (GuiScreen.isShiftKeyDown() && (keyDownEvent.getKeyCode() == 205 || keyDownEvent.getKeyCode() == 203)) {

				if (!focused) return;

				// RIGHT ARROW 205
				// LEFT ARROW 203
				if (keyDownEvent.getKeyCode() == 205) {
					selectionCursor = MathHelper.clamp(selectionCursor + 1, 0, input.length());

					select = input.substring(Math.min(cursor, selectionCursor), Math.max(cursor, selectionCursor));

					cursorRenderFlashingCooldown = cursorCooldown;
				} else if (keyDownEvent.getKeyCode() == 203) {
					//cursor = MathHelper.clamp(cursor - 1, 0, input.length() - 1);
					selectionCursor = MathHelper.clamp(selectionCursor - 1, 0, input.length());

					select = input.substring(Math.min(cursor, selectionCursor), Math.max(cursor, selectionCursor));

					cursorRenderFlashingCooldown = cursorCooldown;
				}

			} else if (keyDownEvent.getKeyCode() == 205) {
				if (!focused) return;

				if (cursor != selectionCursor) {
					cursor = selectionCursor;
				} else cursor = selectionCursor = MathHelper.clamp(cursor + 1, 0, input.length());


				if (getTextWidth(input) > BOOKMARK.getWidth() - 22 && getTextWidth(input.substring(0, cursor)) + text.getPos().getXi() > BOOKMARK.getWidth() - 22)
					text.setPos(text.getPos().sub(getTextWidth(Character.toString(input.charAt(MathHelper.clamp(cursor - 1, 0, input.length())))), 0));

				cursorRenderFlashingCooldown = cursorCooldown;

			} else if (keyDownEvent.getKeyCode() == 203) {
				if (!focused) return;

				if (cursor != selectionCursor) {
					cursor = selectionCursor;
				} else cursor = selectionCursor = MathHelper.clamp(cursor - 1, 0, input.length());

				if (text.getPos().getXi() < 1 && -getTextWidth(input.substring(0, cursor)) > text.getPos().getXi())
					text.setPos(text.getPos().add(getTextWidth(Character.toString(input.charAt(cursor + 1))), 0));

				cursorRenderFlashingCooldown = cursorCooldown;

			} else if (GuiScreen.isKeyComboCtrlA(keyDownEvent.getKeyCode())) {
				if (!focused && searchBarCount > 1) {
					focused = true;
					updateState();
				}

				if (!focused) return;

				select = input;

				cursor = input.length();
				selectionCursor = 0;
				cursorRenderFlashingCooldown = cursorCooldown;

			} else if (GuiScreen.isKeyComboCtrlX(keyDownEvent.getKeyCode())) {
				if (!focused) return;

				if (!select.isEmpty()) {
					cursor = selectionCursor = MathHelper.clamp(input.indexOf(select), 0, input.length());
					input = input.replace(select, "");
					GuiScreen.setClipboardString(select);

					select = "";

					text.getText().setValue(input);
					cursorRenderFlashingCooldown = cursorCooldown;

					if (onType != null) onType.accept(input);
				}

			} else if (GuiScreen.isKeyComboCtrlC(keyDownEvent.getKeyCode())) {

				if (!select.isEmpty()) {
					GuiScreen.setClipboardString(select);
					select = "";
					text.getText().setValue(input);
					cursorRenderFlashingCooldown = cursorCooldown;
				}

			} else if (GuiScreen.isKeyComboCtrlV(keyDownEvent.getKeyCode())) {
				if (!focused && searchBarCount > 1) {
					focused = true;
					updateState();
				}

				if (!focused) return;

				if (select.isEmpty()) {
					String clipboard = ChatAllowedCharacters.filterAllowedCharacters(GuiScreen.getClipboardString());
					input += clipboard;

					cursor = selectionCursor = MathHelper.clamp(cursor + clipboard.length(), 0, input.length());

					text.getText().setValue(input);

					cursorRenderFlashingCooldown = cursorCooldown;
				} else {
					String clipboard = GuiScreen.getClipboardString();
					input = input.replace(select, clipboard);

					cursor = selectionCursor = MathHelper.clamp(input.indexOf(select + select.length()), 0, input.length());

					select = "";

					text.getText().setValue(input);
					cursorRenderFlashingCooldown = cursorCooldown;
				}

				if (onType != null) onType.accept(input);
			} else {
				if (!focused && searchBarCount > 1) {
					focused = true;
					updateState();
				}

				if (!focused) return;

				switch (keyDownEvent.getKeyCode()) {

					// BACKSPACE
					case 14:
						if (!select.isEmpty()) {
							StringBuilder builder = new StringBuilder(input);
							builder.replace(Math.min(cursor, selectionCursor), Math.max(cursor, selectionCursor), "");
							input = builder.toString();
							select = "";

							cursor = selectionCursor = MathHelper.clamp(selectionCursor + 1, 0, input.length());
						} else if (!input.isEmpty()) {
							if (GuiScreen.isCtrlKeyDown() && input.charAt(Math.max(cursor - 1, 0)) != ' ') {
								int wordStart = cursor;
								int wordEnd = cursor;

								while (wordStart - 1 >= 0 && input.charAt(wordStart - 1) != ' ') {
									wordStart -= 1;
								}

								while (wordEnd + 1 < input.length() && input.charAt(wordEnd + 1) != ' ') {
									wordStart += 1;
								}

								StringBuilder builder = new StringBuilder(input);
								builder.delete(wordStart, wordEnd);
								input = builder.toString();

								cursor = selectionCursor = Math.min(wordStart + 1, input.length());
							} else {
								StringBuilder builder = new StringBuilder(input);
								builder.deleteCharAt(Math.max(cursor - 1, 0));
								input = builder.toString();
							}
						}

						String s1 = text.getText().getValue(text);
						String s2 = input;
						String[] chars1 = s1.split("");
						String[] chars2 = s2.split("");

						StringBuilder unCommonChars = new StringBuilder();
						primary:
						for (int i = 0; i < chars1.length; i++) {
							String char1 = chars1[i];

							for (int j = 0; j < chars2.length; j++) {
								String char2 = chars2[j];
								if (char2 != null && char1.equals(char2)) {
									chars1[i] = null;
									chars2[j] = null;
									continue primary;
								}
							}

							unCommonChars.append(char1);
						}

						if (text.getPos().getXi() < 1)
							text.setPos(text.getPos().add(getTextWidth(unCommonChars.toString()), 0));

						cursor = selectionCursor = MathHelper.clamp(cursor - 1, 0, input.length());
						cursorRenderFlashingCooldown = cursorCooldown;

						text.getText().setValue(input);

						if (onType != null) onType.accept(input);
						break;

					// ENTER
					case 28:
					case 156:
						if (!input.isEmpty()) {
							if (onEnter != null) onEnter.accept(input.toLowerCase().trim());
							input = "";
							text.setPos(text.getPos().setX(2));

							cursor = selectionCursor = 0;
							cursorRenderFlashingCooldown = cursorCooldown;

							text.getText().setValue(input);

							slideIn();
							text.setVisible(false);

							focused = false;
							updateState();
						}
						break;
					default:
						if (!select.isEmpty() && ChatAllowedCharacters.isAllowedCharacter(keyDownEvent.getKey())) {

							StringBuilder builder = new StringBuilder(input);
							builder.replace(Math.min(cursor, selectionCursor), Math.max(cursor, selectionCursor), Character.toString(keyDownEvent.getKey()));
							input = builder.toString();
							select = "";

							if (getTextWidth(this.input) > BOOKMARK.getWidth() - 22)
								if (getTextWidth(this.input) > getTextWidth(text.getText().getValue(text))) {
									text.setPos(text.getPos().sub(getTextWidth(Character.toString(keyDownEvent.getKey())), 0));
								}

							cursor = selectionCursor = MathHelper.clamp(selectionCursor + 1, 0, input.length());

							text.getText().setValue(input);

							if (onType != null) onType.accept(input);
						} else if (ChatAllowedCharacters.isAllowedCharacter(keyDownEvent.getKey())) {
							select = "";

							StringBuilder builder = new StringBuilder(input);
							builder.insert(cursor, Character.toString(keyDownEvent.getKey()));
							input = builder.toString();

							if (cursor >= input.length() - 1) {
								if (getTextWidth(this.input) > BOOKMARK.getWidth() - 22)
									if (getTextWidth(this.input) > getTextWidth(text.getText().getValue(text))) {
										text.setPos(text.getPos().sub(getTextWidth(Character.toString(keyDownEvent.getKey())), 0));
									}
							} else if (getTextWidth(input.substring(0, cursor)) + text.getPos().getXi() > BOOKMARK.getWidth() - 22) {
								text.setPos(text.getPos().sub(getTextWidth(Character.toString(keyDownEvent.getKey())), 0));
							}

							cursor = selectionCursor = MathHelper.clamp(cursor + 1, 0, input.length());
							cursorRenderFlashingCooldown = cursorCooldown;

							text.getText().setValue(input);

							if (onType != null) onType.accept(input);
						}
				}
			}
		});

		BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {

			if (!focused) {
				slideOutShort();
				text.setVisible(true);
			}
		});

		BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {

			if (!focused) {
				slideIn();
				text.setVisible(false);
			}
		});

		BUS.hook(GuiComponentEvents.PostDrawEvent.class, event -> {

			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

			// RENDER CURSOR
			{
				if (focused && (cursorRenderFlashingCooldown != 0 || Math.sin(System.currentTimeMillis() / 200.0) > 0)) {
					GlStateManager.pushMatrix();
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.enableBlend();
					GlStateManager.disableLighting();
					GlStateManager.disableCull();
					GlStateManager.disableTexture2D();

					int width = input.isEmpty() ? 0 : fontRenderer.getStringWidth(input.substring(0, cursor)) - 1 + text.getPos().getXi();
					int cursorWidth = 1;
					Color color = Color.WHITE;

					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buffer = tessellator.getBuffer();

					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					buffer.pos(width + cursorWidth, 1, 110).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(width, 1, 110).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(width, BOOKMARK.getHeight() - 1, 110).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(width + cursorWidth, BOOKMARK.getHeight() - 1, 110).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

					tessellator.draw();

					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();
				}
			}

			// RENDER SELECTION BOX
			{
				if (!select.isEmpty() && selectionCursor != cursor) {
					GlStateManager.pushMatrix();
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.enableBlend();
					GlStateManager.disableLighting();
					GlStateManager.disableCull();
					GlStateManager.enableAlpha();
					GlStateManager.disableTexture2D();

					int indexStart = MathHelper.clamp(fontRenderer.getStringWidth(input.substring(0, Math.min(cursor, selectionCursor))) + text.getPos().getXi(), 0, BOOKMARK.getWidth() - 22);
					int indexEnd = MathHelper.clamp(fontRenderer.getStringWidth(input.substring(0, Math.max(cursor, selectionCursor))) + text.getPos().getXi(), 0, BOOKMARK.getWidth() - 22);

					Color color = getBook().highlightColor;

					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buffer = tessellator.getBuffer();

					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					buffer.pos(indexEnd, 1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(indexStart, 1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(indexStart, BOOKMARK.getHeight() - 1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(indexEnd, BOOKMARK.getHeight() - 1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

					tessellator.draw();

					GlStateManager.enableTexture2D();
					GlStateManager.enableAlpha();
					GlStateManager.popMatrix();
				}
			}
		});
	}

	private void updateState() {
		if (focused) {
			slideOutLong();
			text.setVisible(true);
		} else {
			slideIn();
			text.setVisible(false);
		}
	}

	private int getTextWidth(String text) {
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
	}
}
