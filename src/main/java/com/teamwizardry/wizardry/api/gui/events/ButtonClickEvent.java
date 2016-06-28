package com.teamwizardry.wizardry.api.gui.events;

import com.teamwizardry.wizardry.api.gui.GuiComponent;
import com.teamwizardry.wizardry.api.gui.GuiEvent;

public class ButtonClickEvent extends GuiEvent {

    public ButtonClickEvent(GuiComponent component) {
        super(component);
    }

}
