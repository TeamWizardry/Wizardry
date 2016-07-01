package com.teamwizardry.wizardry.api.gui;

/**
 * Created by Saad on 6/19/2016.
 */
public interface IWizardData {

    class BarData implements IWizardData {
        public int burnoutMax = 100, manaMax = 100, burnoutAmount = burnoutMax, manaAmount = 0;
    }
}
