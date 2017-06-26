package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
public class EntitySpellProjectile extends EntityMod {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> DATA_COLOR2 = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public SpellData spell;
	public Module module;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;
		applyColor(Color.WHITE);
		applyColor2(Color.WHITE);

		setRenderDistanceWeight(10);
	}

	public EntitySpellProjectile(World world, Module module, SpellData spell) {
		super(world);
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

		setRenderDistanceWeight(10);
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

		if (world.isRemote) return;
		if (isDead) return;

		if (!isCollided) {
			motionX = look.x;
			motionY = look.y;
			motionZ = look.z;

			move(MoverType.SELF, motionX, motionY, motionZ);
		} else {
			SpellData data = spell.copy();

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 1).setSkipEntity(this).trace();
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

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 1).setSkipEntity(this).trace();
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

		if (module != null && module.nextModule != null) {
			Module nextModule = module.nextModule;
			nextModule.castSpell(data);
		}

		PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector(), new Color(getDataManager().get(DATA_COLOR)), new Color(getDataManager().get(DATA_COLOR2)), 0.3, 0.3, RandUtil.nextInt(100, 200), 75, 25, true),
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
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
		compound.setInteger("color2", getDataManager().get(DATA_COLOR2));
	}
}
