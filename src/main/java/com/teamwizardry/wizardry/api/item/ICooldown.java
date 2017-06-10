package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.IContinousSpell;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface ICooldown {

	Function2<EntityLivingBase, Object, Unit> playerHandler = MethodHandleHelper.wrapperForSetter(EntityLivingBase.class, "aE", "field_184617_aD", "ticksSinceLastSwing");

	default void setCooldown(ItemStack stack, EnumHand hand, EntityPlayer player, World world) {
		for (Module module : SpellStack.getAllModulesSoftly(stack)) if (module instanceof IContinousSpell) return;

		int maxCooldown = 0;
		for (Module module : SpellStack.getAllModulesSoftly(stack)) {
			if (module instanceof IContinousSpell) return;
			if (module.getCooldownTime() > maxCooldown) maxCooldown = module.getCooldownTime();
		}

		ItemNBTHelper.setInt(stack, "cooldown_ticks", maxCooldown);
		ItemNBTHelper.setInt(stack, Constants.NBT.LAST_COOLDOWN, maxCooldown);
		ItemNBTHelper.setLong(stack, Constants.NBT.LAST_CAST, world.getTotalWorldTime());
		playerHandler.invoke(player, 1000);
		Wizardry.proxy.setItemStackHandHandler(hand, stack);
	}

	default void updateCooldown(ItemStack stack, EntityPlayer player) {
		for (Module module : SpellStack.getAllModulesSoftly(stack)) if (module instanceof IContinousSpell) return;

		if (ItemNBTHelper.getInt(stack, "cooldown_ticks", 0) > 0)
			ItemNBTHelper.setInt(stack, "cooldown_ticks", ItemNBTHelper.getInt(stack, "cooldown_ticks", 0) - 1);
		else ItemNBTHelper.removeEntry(stack, "cooldown_ticks");
	}

	default boolean isCoolingDown(ItemStack stack) {
		return ItemNBTHelper.verifyExistence(stack, "cooldown_ticks");
	}
}
