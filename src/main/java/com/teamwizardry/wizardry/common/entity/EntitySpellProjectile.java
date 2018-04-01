package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
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
 * Created by Demoniaque.
 */
public class EntitySpellProjectile extends EntityMod {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> DATA_COLOR2 = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public SpellData spellData;
	public SpellRing spellRing;
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

	public EntitySpellProjectile(World world, SpellRing spellRing, SpellData spellData, double dist, double speed, double gravity) {
		super(world);
		this.dist = dist;
		this.speed = speed;
		this.gravity = gravity;
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;

		this.spellRing = spellRing;
		this.spellData = spellData;

		applyColor(spellRing.getPrimaryColor());
		applyColor2(spellRing.getSecondaryColor());

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

		if (spellRing == null || spellData == null) {
			setDead();
			world.removeEntity(this);
			return;
		}

		Vec3d origin = spellData.getOrigin();

		rotationPitch = spellData.getData(PITCH, 0F);
		rotationYaw = spellData.getData(YAW, 0F);
		Vec3d look = spellData.getData(LOOK);
		if (look == null) {
			setDead();
			world.removeEntity(this);
			return;
		}

		if (origin == null || dist < getDistance(origin.x, origin.y, origin.z)) {
			spellData.processBlock(getPosition(), EnumFacing.getFacingFromVector((float) look.x, (float) look.y, (float) look.z), getPositionVector());
			goBoom(spellData);
			return;
		}

		if (isDead) return;

		if (!collided) {

			// MOVE //
			motionX += ((look.x * speed) - motionX);
			motionY += ((look.y * speed) - motionY);
			motionZ += ((look.z * speed) - motionZ);

			// GRAVITY
			//if (getDistanceSq(origin.x, origin.y, origin.z) > 4)
			//motionY -= gravity;

			move(MoverType.SELF, motionX, motionY, motionZ);
		} else {

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 5).setSkipEntity(this).trace();
			spellData.processTrace(result, getPositionVector());
			goBoom(spellData);
			return;
		}

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
		if (!entities.isEmpty()) {
			Entity caster = spellData.getCaster();

			// Don't collide with other spell projectiles
			for (Entity entity : entities) {
				if (entity == caster) return;
				if (entity instanceof EntitySpellProjectile) return;
			}

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 1).setSkipEntity(this).trace();
			spellData.processTrace(result, getPositionVector());

			goBoom(spellData);
		}
	}

	private void goBoom(SpellData data) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;

		if (spellRing.getChildRing() != null) {
			spellRing.getChildRing().runSpellRing(data);
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
		if (compound.hasKey("spell_ring")) {
			spellRing = SpellRing.deserializeRing(compound.getCompoundTag("spell_ring"));
		}

		if (compound.hasKey("spell_data")) {
			spellData = SpellData.deserializeData(world, compound.getCompoundTag("spell_data"));
		}

		applyColor(spellRing.getPrimaryColor());
		applyColor2(spellRing.getSecondaryColor());

		dist = compound.getDouble("distance");
		speed = compound.getDouble("speed");
		gravity = compound.getDouble("gravity");
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {

		// Stupid Wawla, refusing to fix their problems...
		// https://github.com/micdoodle8/Galacticraft/commit/543e6afad64e51b02252a07489d0832fb93faa8d
		// https://github.com/Darkhax-Minecraft/WAWLA/issues/75
		if (world.isRemote) return;

		compound.setTag("spell_ring", spellRing.serializeNBT());
		compound.setTag("spell_data", spellData.serializeNBT());

		compound.setDouble("distance", dist);
		compound.setDouble("speed", speed);
		compound.setDouble("gravity", gravity);
	}
}
