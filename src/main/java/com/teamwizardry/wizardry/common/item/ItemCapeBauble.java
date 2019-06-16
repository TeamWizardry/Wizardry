package com.teamwizardry.wizardry.common.item;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.ICape;
import com.teamwizardry.wizardry.api.spell.SpellModifierRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Demoniaque on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemCapeBauble extends ItemModBauble implements ICape {

	public static final ResourceLocation CAPE_MODIFIER_LOC = new ResourceLocation(Wizardry.MODID, "cape");
	
	public ItemCapeBauble() {
		super("cape");
		setMaxStackSize(1);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (player.world.isRemote) return;

		tickCape(itemstack);
	}

	@Nonnull
	@Override
	@Optional.Method(modid = "baubles")
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(getCapeTooltip(stack));
	}
	
	@Override
	public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player)
	{
		SpellModifierRegistry.addModifier(player, CAPE_MODIFIER_LOC, (spell, data) -> {
			List<AttributeModifier> modifiers = new LinkedList<>();
			float capeReduction = getCapeReduction(player);
			modifiers.add(new AttributeModifier(AttributeRegistry.MANA, capeReduction, Operation.MULTIPLY));
			//	modifiers.add(new AttributeModifier(AttributeRegistry.BURNOUT, capeReduction, Operation.MULTIPLY));
			return modifiers;
		});
	}
	
	private final float getCapeReduction(EntityLivingBase caster)
	{
		ItemStack stack = BaublesSupport.getItem(caster, ModItems.CAPE);
		if (stack != ItemStack.EMPTY) {
			float time = NBTHelper.getInt(stack, "maxTick", 0);
			return (float) MathHelper.clamp(1 - (time / 1000000.0), 0.25, 1);
		}
		return 1;
	}
	
	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player)
	{
		SpellModifierRegistry.removeModifier(player, CAPE_MODIFIER_LOC);
	}
}
