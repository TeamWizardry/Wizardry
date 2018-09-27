package com.teamwizardry.wizardry.common.item.halos;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import com.teamwizardry.wizardry.api.spell.SpellModifierRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;

import baubles.api.BaubleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

/**
 * Created by Demoniaque on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemCreativeHaloBauble extends ItemModBauble implements IHalo {

	public static final ResourceLocation CREATIVE_HALO_MODIFIER_LOC = new ResourceLocation(Wizardry.MODID, "creative_halo");
	
	public ItemCreativeHaloBauble() {
		super("halo_creative");
		setMaxStackSize(1);
	}

	@Override
	public void onWornTick(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player) {
		if (player.world.isRemote) return;

		try (CapManager.CapManagerBuilder mgr = CapManager.forObject(player)) {
			mgr.setMaxMana(ConfigValues.creativeHaloBufferSize);
			mgr.setMaxBurnout(ConfigValues.creativeHaloBufferSize);
			mgr.setMana(ConfigValues.creativeHaloBufferSize);
			mgr.setBurnout(0);
		}
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@Nonnull ItemStack itemStack) {
		return BaubleType.HEAD;
	}

	@Override
	public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, @NotNull ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		tooltip.addAll(getHaloTooltip(stack));
	}
	
	@Override
	public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player)
	{
		SpellModifierRegistry.addModifier(player, CREATIVE_HALO_MODIFIER_LOC, (spell, data) -> {
			List<AttributeModifier> modifiers = new LinkedList<>();
			modifiers.add(new AttributeModifier(AttributeRegistry.MANA, 0, Operation.MULTIPLY));
			modifiers.add(new AttributeModifier(AttributeRegistry.BURNOUT, 0, Operation.MULTIPLY));
			return modifiers;
		});
	}
	
	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player)
	{
		SpellModifierRegistry.removeModifier(player, CREATIVE_HALO_MODIFIER_LOC);
	}
}
