package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.ai.FairyAIWanderAvoidWaterFlying;
import com.teamwizardry.wizardry.common.entity.ai.FairyMoveHelper;
import com.teamwizardry.wizardry.common.entity.ai.WizardryFlyablePathNavigator;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;


/**
 * Created by Demoniaque on 8/21/2016.
 */
@SaveInPlace
public class EntityFairy extends EntityTameable implements EntityFlying {

	public double mana;
	public Color color;
	public boolean dulled;
	public int age;
	public boolean changingCourse = false;
	public int changeCourseTick = 0;
	public float tickPitch = 0;
	public float tickYaw = 0;
	private EntityAIAvoidEntity<EntityPlayer> avoidEntity;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(1F, 1F);
		isAirBorne = true;
		experienceValue = 5;
		color = new Color(RandUtil.nextFloat(), RandUtil.nextFloat(), RandUtil.nextFloat());
		color = color.brighter();
		age = RandUtil.nextInt(10, 300);
		moveHelper = new FairyMoveHelper(this);
	}

	public EntityFairy(World worldIn, Color color, int age, double mana) {
		super(worldIn);
		this.mana = mana;
		setSize(1F, 1F);
		isAirBorne = true;
		experienceValue = 5;
		this.color = color;
		this.age = age;
		moveHelper = new FairyMoveHelper(this);
	}

	@Nullable
	@Override
	public EntityAgeable createChild(@NotNull EntityAgeable ageable) {
		return null;
	}


	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canMateWith(@NotNull EntityAnimal otherAnimal) {
		return false;
	}

	@Override
	public boolean attackEntityAsMob(@NotNull Entity entityIn) {
		if (entityIn instanceof EntityUnicorn) return false;
		if (!isTamed()) return false;
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
	}

	@Override
	public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			if (this.aiSit != null) {
				this.aiSit.setSitting(false);
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	public boolean isFlying() {
		return !this.onGround;
	}

	@Override
	public void initEntityAI() {
		this.tasks.addTask(1, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
		this.tasks.addTask(2, new FairyAIWanderAvoidWaterFlying(this, 1.0D));
		this.tasks.addTask(3, new EntityAIFollow(this, 1.0D, 3.0F, 7.0F));
	}

	@Override
	protected void setupTamedAI() {
		if (this.avoidEntity == null) {
			this.avoidEntity = new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16.0F, 2, 3);
		}

		this.tasks.removeTask(this.avoidEntity);

		if (!this.isTamed()) {
			this.tasks.addTask(0, this.avoidEntity);
		}

		this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntityUnicorn.class, 16, 2, 3));
		this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntitySpiritWight.class, 16, 2, 3));
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(RandUtil.nextDouble(2, 3));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1);
	}

	@NotNull
	@Override
	public PathNavigate createNavigator(@NotNull World worldIn) {
		return new WizardryFlyablePathNavigator(this, worldIn);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.3;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;

		//if (entity.world.isRemote)
		//	LibParticles.AIR_THROTTLE(world, getPositionVector(), entity, color, color.brighter(), -1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		fallDistance = 0;
		setNoGravity(true);

		if (isAIDisabled()) return;
		if (isDead) return;

		if (getNavigator().noPath()) {
			getMoveHelper().setMoveTo(posX + RandUtil.nextDouble(-32, 32), posY + RandUtil.nextDouble(-32, 32), posZ + RandUtil.nextDouble(-32, 32), 1);
		}
	}

	@NotNull
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		ItemStack jar = player.getHeldItemMainhand();
		if (jar.getItem() == ModItems.JAR_ITEM && jar.getItemDamage() == 0) {
			succFairy(jar, player);
			return EnumActionResult.SUCCESS;
		}
		return super.applyPlayerInteraction(player, vec, hand);
	}

	private void succFairy(ItemStack stack, EntityPlayer player) {
		stack.shrink(1);
		ItemStack jar = new ItemStack(ModItems.JAR_ITEM);
		jar.setItemDamage(2);
		NBTHelper.setBoolean(jar, Constants.NBT.FAIRY_INSIDE, true);
		NBTHelper.setInt(jar, Constants.NBT.FAIRY_COLOR, getColor().getRGB());
		NBTHelper.setInt(jar, Constants.NBT.FAIRY_AGE, getAge());
		NBTHelper.setBoolean(jar, Constants.NBT.FAIRY_DULLED, isDulled());
		CapManager.forObject(stack).setMana(mana);
		player.addItemStackToInventory(jar);
		world.removeEntity(this);
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		if (!world.isRemote && getHealth() <= 0)
			PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector().add(0, 0.25, 0), color, color, 0.5, 0.5, RandUtil.nextInt(100, 200), 75, 25, true),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 256));
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		//super.dropLoot(wasRecentlyHit, lootingModifier, source);
		ItemStack fairyWings = new ItemStack(ModItems.FAIRY_WINGS);
		ItemStack fairyDust = new ItemStack(ModItems.FAIRY_DUST);
		NBTHelper.setInt(fairyWings, NBT.FAIRY_COLOR, color.getRGB());
		entityDropItem(fairyDust, RandUtil.nextFloat());
		entityDropItem(fairyWings, RandUtil.nextFloat());
	}

	@NotNull
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
		return compound;
	}

	@Override
	public void deserializeNBT(@NotNull NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		AbstractSaveHandler.readAutoNBT(this, nbt.getCompoundTag("save"), true);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isDulled() {
		return dulled;
	}

	public void setDulled(boolean dulled) {
		this.dulled = dulled;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
