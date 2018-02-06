package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import java.awt.*;
import java.util.function.Consumer;

public class ComponentTextBox extends GuiComponent {

	private String input = "";

	private String select = "";

	private boolean focused = false;

	private Consumer<String> enter = null;

	public ComponentTextBox(int posX, int posY, int width, Consumer<String> onSearch) {
		super(posX, posY, width, 21);

		ComponentRect highlight = new ComponentRect(posX - 2, posY - 2, width + 4, getSize().getYi() + 4);
		highlight.getColor().setValue(new Color(0xCC00faff));
		add(highlight);
		highlight.setVisible(false);

		ComponentRect rect = new ComponentRect(posX, posY, width, getSize().getYi());
		rect.getColor().setValue(new Color(0x80FFFFFF));
		add(rect);

		ComponentText text = new ComponentText(posX, posY + 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		text.getTransform().setScale(2);
		text.getText().setValue("");
		add(text);

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			if (mouseClickEvent.component.getMouseOver()) {
				focused = true;
				highlight.setVisible(true);
			} else {
				focused = false;
				highlight.setVisible(false);
			}
		});

		BUS.hook(GuiComponentEvents.KeyDownEvent.class, keyDownEvent -> {
			if (!focused) return;

			if (GuiScreen.isKeyComboCtrlA(keyDownEvent.getKeyCode())) {
				select = input;

			} else if (GuiScreen.isKeyComboCtrlX(keyDownEvent.getKeyCode())) {

				if (!select.isEmpty()) {
					input = input.replace(select, "");
					GuiScreen.setClipboardString(select);
					select = "";

					text.getText().setValue(input);
				}

			} else if (GuiScreen.isKeyComboCtrlC(keyDownEvent.getKeyCode())) {

				if (!select.isEmpty()) {
					GuiScreen.setClipboardString(select);
					select = "";

					text.getText().setValue(input);
				}

			} else if (GuiScreen.isKeyComboCtrlV(keyDownEvent.getKeyCode())) {
				if (select.isEmpty()) {
					input += GuiScreen.getClipboardString();

					text.getText().setValue(input);

				} else {
					input = input.replace(select, GuiScreen.getClipboardString());

					text.getText().setValue(input);
				}
			} else {
				select = "";

				switch (keyDownEvent.getKeyCode()) {

					case 14:
						if (!input.isEmpty()) {
							input = input.substring(0, input.length() - 1);
						}

						break;
					case 28:
					case 156:
						if (!input.isEmpty()) {
							onSearch.accept(input);
							input = "";
						}
						break;
					default:
						if (input.length() < 100 && ChatAllowedCharacters.isAllowedCharacter(keyDownEvent.getKey())) {
							this.input += Character.toString(keyDownEvent.getKey());
						}
				}

				text.getText().setValue(input);
			}
		});
	}
}
