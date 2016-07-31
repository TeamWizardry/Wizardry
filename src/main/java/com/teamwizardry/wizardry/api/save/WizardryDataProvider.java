package com.teamwizardry.wizardry.api.save;

import com.teamwizardry.wizardry.api.Constants;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * Created by Saad on 6/19/2016.
 */
public class WizardryDataProvider implements ICapabilityProvider {

    private IWizardData.BarData manaData;

    public WizardryDataProvider() {
        manaData = new IWizardData.BarData();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Constants.Misc.BAR_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) manaData : null;
    }
}
