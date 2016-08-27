package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.IModItemProvider;
import com.teamwizardry.librarianlib.common.util.VariantHelper;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 2:13 PM on 8/26/16.
 */
public abstract class ItemWizardryFood extends ItemFood implements IModItemProvider {

    public final String[] variants;

    private final String bareName;
    private final String modId;

    public ItemWizardryFood(String name, int amount, float saturation, boolean wolfFood, String... variants) {
        super(amount, saturation, wolfFood);
        modId = Loader.instance().activeModContainer().getModId();
        bareName = name;
        this.variants = VariantHelper.setupItem(this, name, variants, getCreativeTab());
    }

    @NotNull
    @Override
    public Item setUnlocalizedName(@NotNull String unlocalizedName) {
        VariantHelper.setUnlocalizedNameForItem(this, modId, unlocalizedName);
        return super.setUnlocalizedName(unlocalizedName);
    }

    @NotNull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int dmg = stack.getItemDamage();
        return "item." + modId + ":" + (dmg > variants.length ? bareName : variants[dmg]);
    }

    @Override
    public void getSubItems(@NotNull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < variants.length; i++)
            subItems.add(new ItemStack(itemIn, 1, i));
    }

    @Nonnull
    @Override
    public String[] getVariants() {
        return variants;
    }

    @Nonnull
    @Override
    public Item getProvidedItem() {
        return this;
    }

    @NotNull
    @Override
    public ModCreativeTab getCreativeTab() {
        return Wizardry.tab;
    }

    @Nullable
    @Override
    public ItemMeshDefinition getCustomMeshDefinition() {
        return null;
    }
}
