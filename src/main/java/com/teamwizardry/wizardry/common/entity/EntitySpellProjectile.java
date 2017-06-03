package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@Savable
public class EntitySpellProjectile extends EntityMod {

	public Color primaryColor = Color.WHITE;
	public Color secondaryColor = Color.WHITE;
	public SpellData spell;
	public Module module;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;

		setRenderDistanceWeight(10);
		dispatchEntityToNearbyPlayers();
	}

	public EntitySpellProjectile(World world, Module module, SpellData spell) {
		super(world);
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;

		this.module = module;
		this.spell = spell;

		if (module != null) {
			if (module.getPrimaryColor() != null) {
				primaryColor = module.getPrimaryColor();
			}

			if (module.getSecondaryColor() != null) secondaryColor = module.getSecondaryColor();
			else {
				Color color = primaryColor;
				secondaryColor = new Color(Math.min(255, color.getRed() + 40), Math.min(255, color.getGreen() + 40), Math.min(255, color.getBlue() + 40));
			}
		}

		setRenderDistanceWeight(10);

		dispatchEntityToNearbyPlayers();
	}

	@Override
	protected void entityInit() {

	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return getEntityBoundingBox();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (ticksExisted > 1000) {
			setDead();
			return;
		}

		if (module == null) return;

		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		rotationPitch = pitch;
		rotationYaw = yaw;

		Vec3d look = PosUtils.vecFromRotations(pitch, yaw);

		if (!isCollided) {
			motionX = look.xCoord;
			motionY = look.yCoord;
			motionZ = look.zCoord;

			move(MoverType.SELF, motionX, motionY, motionZ);
		} else {
			SpellData data = spell.copy();

			RayTraceResult result = Utils.raytrace(world, look, getPositionVector(), 1, this);
			if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
				data.processBlock(result.getBlockPos(), result.sideHit, result.hitVec);
			} else data.processBlock(getPosition(), result != null ? result.sideHit : null, getPositionVector());

			goBoom(data);
			return;
		}

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
		if (!entities.isEmpty()) {
			Entity caster = spell.getData(CASTER);

			if (caster != null && entities.size() == 1 && entities.get(0) instanceof EntitySpellProjectile) {
				EntitySpellProjectile spellProjectile = (EntitySpellProjectile) entities.get(0);
				SpellData otherData = spellProjectile.spell;
				if (otherData != null && otherData.hasData(CASTER)) {
					Entity otherCaster = spell.getData(CASTER);
					if (otherCaster != null && otherCaster.getUniqueID().equals(caster.getUniqueID())) {
						return;
					}
				}
			}

			if (caster != null && entities.contains(caster)) return;

			SpellData data = spell.copy();

			RayTraceResult result = Utils.raytrace(world, look, getPositionVector(), 1, this);
			if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
				data.processEntity(result.entityHit, false);
			} else if (entities.get(0) != null) data.processEntity(entities.get(0), false);

			goBoom(data);
		}
	}

	public void goBoom(SpellData data) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;

		if (module != null)
			module.runNextModule(data);

		PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector(), primaryColor, secondaryColor, 0.3, 0.3, RandUtil.nextInt(100, 200), 75, 25, true),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));

		setDead();
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return super.isInRangeToRender3d(x, y, z);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		super.readCustomNBT(compound);
		NBTTagCompound moduleCompound = compound.getCompoundTag("module");
		Module tempModule = ModuleRegistry.INSTANCE.getModule(moduleCompound.getString("id"));
		if (tempModule != null) {
			Module module = tempModule.copy();
			if (module != null) {
				this.module = module;
				this.module.deserializeNBT(compound);
			}
		}

		spell = new SpellData(world);
		spell.deserializeNBT(compound.getCompoundTag("spell_data"));

		AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("save"), true);
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());

		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
	}
}
