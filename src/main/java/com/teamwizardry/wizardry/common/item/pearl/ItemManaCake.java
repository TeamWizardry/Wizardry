package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.IWizardData;
import com.teamwizardry.wizardry.api.capability.WizardHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 3/7/2016.
 */
public class ItemManaCake extends ItemFood {

    public ItemManaCake() {
        super(0, 0.3F, false);
        setRegistryName("mana_cake");
        setUnlocalizedName("mana_cake");
        GameRegistry.register(this);
        setCreativeTab(Wizardry.tab);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        IWizardData.BarData data = WizardHandler.getEntityData(player);
        if (data.manaMax >= data.manaAmount + 300)
            data.manaAmount += 300;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
