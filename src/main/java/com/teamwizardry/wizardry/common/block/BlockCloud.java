package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 8/27/2016.
 */
public class BlockCloud extends BlockMod {

    public BlockCloud() {
        super("cloud", Material.ROCK);
        GameRegistry.registerTileEntity(TilePedestal.class, "cloud");
        setCreativeTab(Wizardry.tab);
    }

    @Nullable
    @Override
    public ModCreativeTab getCreativeTab() {
        return Wizardry.tab;
    }
}
