package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class ModuleBlink extends Module {

	public static final String COORD_SET = "blink_coords";
	public static final String POS_X = "blink_x";
	public static final String POS_Y = "blink_y";
	public static final String POS_Z = "blink_z";

	private boolean useCoord;
	private BlockPos pos = new BlockPos(0, 0, 0);

	public ModuleBlink(ItemStack stack) {
		super(stack);
		attributes.addAttribute(Attribute.DISTANCE);
	}

	@Override
	public ModuleType getType() {
		return ModuleType.EFFECT;
	}

	@Override
	public String getDescription() {
		return "If no position is set, blink forward based on the power. Otherwise, teleport to the set location.";
	}

	@Override
	public String getDisplayName() {
		return "Blink";
	}

	@Override
	public NBTTagCompound getModuleData() {
		NBTTagCompound compound = super.getModuleData();
		compound.setBoolean(COORD_SET, useCoord);
		compound.setInteger(POS_X, pos.getX());
		compound.setInteger(POS_Y, pos.getY());
		compound.setInteger(POS_Z, pos.getZ());

		compound.setDouble(Constants.Module.POWER, attributes.apply(Attribute.DISTANCE, 1.0));
		compound.setDouble(Constants.Module.MANA, attributes.apply(Attribute.MANA, 10.0));
		compound.setDouble(Constants.Module.BURNOUT, attributes.apply(Attribute.BURNOUT, 10.0));
		return compound;
	}

	public ModuleBlink setPos(BlockPos pos) {
		useCoord = true;
		this.pos = pos;
		return this;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		double power = spell.getDouble(Constants.Module.POWER);

		Vec3d look = caster.getLook(1.0F);

        if (!caster.world.isRemote && (caster instanceof EntityLivingBase)) {
            EnderTeleportEvent event = new EnderTeleportEvent((EntityLivingBase) caster, caster.posX, caster.posY, caster.posZ, 0);
			if (MinecraftForge.EVENT_BUS.post(event))
				return false;
		}
		double posX = look.xCoord * power;
		double posY = look.yCoord * power;
		double posZ = look.zCoord * power;
		caster.setPositionAndUpdate(caster.posX + posX, caster.posY + posY, caster.posZ + posZ);
        caster.world.playSound(null, caster.prevPosX, caster.prevPosY, caster.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, caster.getSoundCategory(), 1.0F, 1.0F);
        caster.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

		return true;
	}
}
