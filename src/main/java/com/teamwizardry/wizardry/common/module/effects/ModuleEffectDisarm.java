package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function1;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_disarm")
public class ModuleEffectDisarm implements IModuleEffect {

	private Function1<EntityLiving, Object> inventoryHandsDropChances = MethodHandleHelper.wrapperForGetter(EntityLiving.class, "inventoryHandsDropChances", "field_184655_bs", "bs");

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);

		if (targetEntity instanceof EntityLivingBase) {
			if (!world.isRemote) {
				if (!spellRing.taxCaster(world, spell, true)) return false;

				ItemStack held = ((EntityLivingBase) targetEntity).getHeldItemMainhand();

				if (targetEntity instanceof EntityPlayer) {
					ItemStack copy = held.copy();
					held.setCount(0);
					EntityItem item = new EntityItem(world, targetEntity.posX, targetEntity.posY + 1, targetEntity.posZ, copy);
					item.setDefaultPickupDelay();
					world.playSound(null, targetEntity.getPosition(), ModSounds.ELECTRIC_BLAST, SoundCategory.NEUTRAL, 1, 1);
					return world.spawnEntity(item);
				} else {
					ItemStack stack = held.copy();
					held.setCount(0);

					float dropChance = 0;

					if (targetEntity instanceof EntityLiving) {
						EntityLiving entity = (EntityLiving) targetEntity;

						Object o = inventoryHandsDropChances.invoke(entity);
						float[] dropChances;
						if (o instanceof float[]) {
							dropChances = (float[]) o;
							dropChance = dropChances[EntityEquipmentSlot.MAINHAND.getIndex()];
						}
					}

					boolean flag = dropChance > 1.0;

					if (!held.isEmpty() && flag && RandUtil.nextDouble() < dropChance) {
						EntityItem item = new EntityItem(world, targetEntity.posX, targetEntity.posY + 1, targetEntity.posZ, stack);
						item.setPickupDelay(5);
						world.playSound(null, targetEntity.getPosition(), ModSounds.ELECTRIC_BLAST, SoundCategory.NEUTRAL, 1, 1);
						return world.spawnEntity(item);
					}
				}
			}
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, instance.getPrimaryColor());
	}
}
