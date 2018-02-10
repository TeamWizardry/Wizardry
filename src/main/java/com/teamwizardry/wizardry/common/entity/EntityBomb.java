package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class EntityBomb extends EntityThrowable {

	public static final DataParameter<Integer> DATA_BOMB_TYPE = EntityDataManager.createKey(EntityBomb.class, DataSerializers.VARINT);

	public EntityBomb(World worldIn) {
		super(worldIn);
		this.getDataManager().set(DATA_BOMB_TYPE, 0);
		this.getDataManager().setDirty(DATA_BOMB_TYPE);
	}

	public EntityBomb(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
	}

	public EntityBomb(World worldIn, ItemStack bomb) {
		super(worldIn);
	}

	public void setBombItem(ItemStack bomb) {
		this.getDataManager().set(DATA_BOMB_TYPE, bomb.getItemDamage());
		this.getDataManager().setDirty(DATA_BOMB_TYPE);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(DATA_BOMB_TYPE, 0);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (getThrower() == null) return;
		if (result.entityHit != null && getThrower() != null && getThrower().getEntityId() == result.entityHit.getEntityId())
			return;

		int type = getDataManager().get(DATA_BOMB_TYPE);

		if (type == 0) PosUtils.boom(getEntityWorld(), getPositionVector(), null, 10, true);
		else PosUtils.boom(getEntityWorld(), getPositionVector(), null, 10, false);

		ClientRunnable.run(new ClientRunnable() {
			@SideOnly(Side.CLIENT)
			@Override
			public void runIfClient() {

				Color color, color2;
				if (type == 1) {
					color = Color.CYAN;
					color2 = Color.BLUE;
				} else {
					color = Color.RED;
					color2 = Color.ORANGE;
				}

				ParticleBuilder glitter = new ParticleBuilder(10);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.setCollision(true);
				glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.03, -0.04), 0));
				glitter.setCanBounce(true);
				glitter.enableMotionCalculation();
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 500, 0, (i, build) -> {
					build.setMotion(Vec3d.ZERO);
					build.setLifetime(RandUtil.nextInt(50, 100));
					build.setAlphaFunction(new InterpFadeInOut(RandUtil.nextFloat(), RandUtil.nextFloat()));
					build.setScale(RandUtil.nextFloat(0.2f, 1));

					if (type == 0) {
						double radius = RandUtil.nextDouble(20, 40);
						double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
						double r = radius * RandUtil.nextFloat();
						double x = r * MathHelper.cos((float) theta);
						double z = r * MathHelper.sin((float) theta);
						build.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z));
						build.addMotion(build.getPositionOffset().scale(-1.0 / RandUtil.nextDouble(10, 30)));
					} else {
						double radius = RandUtil.nextDouble(1, 3);
						double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
						double r = radius * RandUtil.nextFloat();
						double x = r * MathHelper.cos((float) theta);
						double z = r * MathHelper.sin((float) theta);
						build.addMotion(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z));
					}
					if (RandUtil.nextBoolean()) build.setColor(color);
					else build.setColor(color2);
				});

			}
		});

		setDead();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		this.getDataManager().set(DATA_BOMB_TYPE, compound.getInteger("bomb_type"));
		this.getDataManager().setDirty(DATA_BOMB_TYPE);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("bomb_type", getDataManager().get(DATA_BOMB_TYPE));
	}
}
