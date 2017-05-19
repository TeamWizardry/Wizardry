package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.network.PacketParticleFairyExplode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.PITCH;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.YAW;

/**
 * Created by LordSaad.
 */
public class EntitySpellProjectile extends Entity {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public static final DataParameter<Boolean> DATA_COLLIDED = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.BOOLEAN);
	public SpellData spell;
	public Module module;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;
		applyColor(Color.WHITE);
		applyCollided(false);

		setRenderDistanceWeight(10);
	}

	public EntitySpellProjectile(World world, Module module, SpellData spell) {
		super(world);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;

		this.module = module;
		this.spell = spell;

		if (module != null && module.getColor() != null) applyColor(module.getColor());
		else applyColor(Color.WHITE);
		applyCollided(false);

		setRenderDistanceWeight(10);
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DATA_COLOR, 0);
		this.getDataManager().register(DATA_COLLIDED, false);
	}

	private void applyColor(Color color) {
		this.getDataManager().set(DATA_COLOR, color.getRGB());
		this.getDataManager().setDirty(DATA_COLOR);
	}

	private void applyCollided(boolean collided) {
		this.getDataManager().set(DATA_COLLIDED, collided);
		this.getDataManager().setDirty(DATA_COLLIDED);
	}

	@Override
	public void onUpdate() {
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
			motionX = 0;
			motionY = 0;
			motionZ = 0;

			applyCollided(true);
			// TODO: not here
			PacketHandler.NETWORK.sendToAllAround(new PacketParticleFairyExplode(getPositionVector(), new Color(getDataManager().get(DATA_COLOR))),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));

			if (module != null && module.nextModule != null) {
				Module nextModule = module.nextModule;
				SpellData newSpell = spell.copy();

				RayTraceResult result = new RayTraceResult(this);
				if (result.typeOfHit == RayTraceResult.Type.ENTITY)
					newSpell.crunchData(result.entityHit, false);
				else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					newSpell.addData(SpellData.DefaultKeys.BLOCK_HIT, result.getBlockPos());
					newSpell.addData(SpellData.DefaultKeys.TARGET_HIT, result.hitVec);
				}
				nextModule.castSpell(newSpell);
			}
			setDead();
		}
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
	public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		NBTTagCompound moduleCompound = compound.getCompoundTag("module");
		Module module = ModuleRegistry.INSTANCE.getModule(moduleCompound.getString("id"));
		if (module != null) {
			module.deserializeNBT(compound);
			Module.process(module);
			this.module = module;
		}

		spell = new SpellData(world);
		spell.deserializeNBT(compound.getCompoundTag("spell_data"));
		applyColor(new Color(compound.getInteger("color")));

		applyCollided(compound.getBoolean("collided"));
	}

	@Override
	public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
		compound.setBoolean("collided", getDataManager().get(DATA_COLLIDED));
	}
}
