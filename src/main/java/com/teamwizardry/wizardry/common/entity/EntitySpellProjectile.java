package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
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
public class EntitySpellProjectile extends EntityMod {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> DATA_COLOR2 = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public SpellData spell;
	public Module module;
	private double dist;
	private double speed;
	private double gravity;

	public EntitySpellProjectile(World world) {
		super(world);
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;
		applyColor(Color.WHITE);
		applyColor2(Color.WHITE);

		if (world.isRemote)
			setRenderDistanceWeight(30);
	}

	public EntitySpellProjectile(World world, Module module, SpellData spell, double dist, double speed, double gravity) {
		super(world);
		this.dist = dist;
		this.speed = MathHelper.clamp(speed, 0.5, 20);
		this.gravity = gravity;
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;

		this.module = module;
		this.spell = spell;

		if (module != null) {
			if (module.getPrimaryColor() != null) applyColor(module.getPrimaryColor());
			else applyColor(Color.WHITE);

			if (module.getSecondaryColor() != null) applyColor2(module.getSecondaryColor());
			else applyColor2(Color.WHITE);
		}

		if (world.isRemote)
			setRenderDistanceWeight(30);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return getEntityBoundingBox();
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DATA_COLOR, 0);
		this.getDataManager().register(DATA_COLOR2, 0);
	}

	private void applyColor(Color color) {
		this.getDataManager().set(DATA_COLOR, color.getRGB());
		this.getDataManager().setDirty(DATA_COLOR);
	}

	private void applyColor2(Color color) {
		this.getDataManager().set(DATA_COLOR2, color.getRGB());
		this.getDataManager().setDirty(DATA_COLOR2);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (world.isRemote) return;

		if (module == null || spell == null) {
			setDead();
			world.removeEntity(this);
			return;
		}

		Vec3d origin = spell.getData(ORIGIN);

		rotationPitch = spell.getData(PITCH, 0F);
		rotationYaw = spell.getData(YAW, 0F);
		Vec3d look = spell.getData(LOOK);
		if (look == null) {
			setDead();
			world.removeEntity(this);
			return;
		}

		if (origin == null || dist < getDistance(origin.x, origin.y, origin.z)) {
			spell.processBlock(getPosition(), EnumFacing.getFacingFromVector((float) look.x, (float) look.y, (float) look.z), getPositionVector());
			spell.addData(ORIGIN, getPositionVector());
			goBoom(spell);
			return;
		}

		if (isDead) return;

		if (!collided) {

			// MOVE //
			motionX = look.x * speed;
			motionY = look.y * speed;
			motionZ = look.z * speed;

			// GRAVITY
			//if (getDistanceSq(origin.x, origin.y, origin.z) > 4)
			//motionY -= gravity;

			move(MoverType.SELF, motionX, motionY, motionZ);
		} else {
			SpellData data = spell.copy();

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 1).setSkipEntity(this).trace();
			if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
				data.processBlock(result.getBlockPos(), result.sideHit, result.hitVec);
				data.addData(ORIGIN, result.hitVec);
			} else {
				data.processBlock(getPosition(), result.sideHit, getPositionVector());
				data.addData(ORIGIN, getPositionVector());
			}
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

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 1).setSkipEntity(this).trace();
			if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
				data.processEntity(result.entityHit, false);
				data.addData(ORIGIN, result.hitVec);
			} else if (entities.get(0) != null) {
				data.processEntity(entities.get(0), false);
				data.addData(ORIGIN, entities.get(0).getPositionVector().addVector(0, entities.get(0).getEyeHeight(), 0));
			}

			goBoom(data);
		}
	}

	private void goBoom(SpellData data) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;

		if (module != null && module.nextModule != null) {
			Module nextModule = module.nextModule;
			nextModule.castSpell(data);
		}

		PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector(), new Color(getDataManager().get(DATA_COLOR)), new Color(getDataManager().get(DATA_COLOR2)), 0.3, 0.3, RandUtil.nextInt(30, 50), 10, 25, true),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));

		setDead();
		world.removeEntity(this);
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
		applyColor(new Color(compound.getInteger("color")));
		applyColor2(new Color(compound.getInteger("color2")));

		dist = compound.getDouble("distance");
		speed = compound.getDouble("speed");
		gravity = compound.getDouble("gravity");
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		if (world.isRemote) return; // Stupid Wawla, refusing to fix their problems...
									// https://github.com/micdoodle8/Galacticraft/commit/543e6afad64e51b02252a07489d0832fb93faa8d
									// https://github.com/Darkhax-Minecraft/WAWLA/issues/75
		
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
		compound.setInteger("color2", getDataManager().get(DATA_COLOR2));

		compound.setDouble("distance", dist);
		compound.setDouble("speed", speed);
		compound.setDouble("gravity", gravity);
	}
}
