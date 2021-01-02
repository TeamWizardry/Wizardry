package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.prism.Save;
import com.teamwizardry.wizardry.api.capability.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.mana.ManaCapability;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileManaBattery extends TileEntity {
    @Save
    public final ManaCapability manaStorage = new ManaCapability(0, 10000, 0, 5000);
    private final LazyOptional<IManaCapability> manaCap = LazyOptional.of(() -> manaStorage);;

    public TileManaBattery() {
        super(LibTileEntityType.MANA_BATTERY.get());
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ManaCapability.MANA_CAPABILITY) {
            return manaCap.cast();
        } else {
            return super.getCapability(cap);
        }
    }
}
