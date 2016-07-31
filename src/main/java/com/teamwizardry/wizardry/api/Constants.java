package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.save.IWizardData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Created by Saad on 6/14/2016.
 */
public class Constants {

    public static class GuiButtons {
        public static final int NAV_BAR_NEXT = 1;
        public static final int NAV_BAR_BACK = 2;
        public static final int NAV_BAR_INDEX = 3;
        public static final int BOOKMARK = 4;
        public static final int SCHEMATIC_UP_LAYER = 5;
        public static final int SCHEMATIC_DOWN_LAYER = 6;
    }

    public static class PageNumbers {
        public static final int GUIDE = 0;
        public static final int WORKTABLE = 1;
        public static final int BASICS = 2;
        public static final int BASICS_GETTING_STARTED = 3;
    }

    public static class WorkTable {
        public static final int DONE_BUTTON = 0;
        public static final int CONFIRM_BUTTON = 1;
    }

    public static class Misc {
        @CapabilityInject(IWizardData.class)
        public static Capability<IWizardData> BAR_HANDLER_CAPABILITY = null;
    }
}
