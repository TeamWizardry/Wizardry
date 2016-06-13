package me.lordsaad.wizardry.items;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/13/2016.
 */
public class ItemRing extends Item {

    public ItemRing() {
        setRegistryName("ring");
        setUnlocalizedName("ring");
        GameRegistry.register(this);
        setCreativeTab(Wizardry.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation full = new ModelResourceLocation(getRegistryName() + "_pearl", "inventory");
        ModelResourceLocation empty = new ModelResourceLocation(getRegistryName(), "inventory");

        ModelLoader.setCustomMeshDefinition(this, stack -> {
            if (stack.getItemDamage() == 1) {
                return full;
            } else {
                return empty;
            }
        });
    }
}