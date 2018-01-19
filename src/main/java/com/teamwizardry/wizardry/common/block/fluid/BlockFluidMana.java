package com.teamwizardry.wizardry.common.block.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.ManaTracker;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipes;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockFluidMana extends BlockFluidClassic {

	public BlockFluidMana() {
		super(FluidMana.instance, Material.WATER);
		setRegistryName("mana");
		setQuantaPerBlock(6);
		setUnlocalizedName("mana");
	}

	@Override
	public Fluid getFluid() {
		return FluidMana.instance;
	}

	@Override
	public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
		super.updateTick(world, pos, state, rand);

		if (world.isRemote)
			LibParticles.FIZZING_AMBIENT(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
		// Fizz all entities in the pool
		run(world, pos, entityIn,
				entity -> true,
				entity -> {
					if (world.isRemote) LibParticles.FIZZING_AMBIENT(world, entityIn.getPositionVector());
				});

		// Nullify gravity of player
		run(world, pos, entityIn,
				entity -> entity instanceof EntityLivingBase,
				entity -> {
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));

					if (RandUtil.nextInt(50) == 0) entity.attackEntityFrom(DamageSourceMana.INSTANCE, 0.1f);
				});

		// Subtract player food
		run(world, pos, entityIn,
				entity -> entity instanceof EntityPlayer,
				entity -> {

					if (!world.isRemote) {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(Wizardry.MODID, "advancements/advancement_crunch.json"));
						if (advancement == null) return;
						AdvancementProgress progress = ((EntityPlayerMP) entity).getAdvancements().getProgress(advancement);
						for (String s : progress.getRemaningCriteria()) {
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(advancement, s);
						}
					}
					if (!((EntityPlayer) entity).capabilities.isCreativeMode && RandUtil.nextInt(50) == 0)
						((EntityPlayer) entity).getFoodStats().addExhaustion(1f);
				});
		// Explode explodable items
		run(world, pos, entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof IExplodable,
				entity -> {
					ManaTracker.INSTANCE.addManaCraft(entity.world, entity.getPosition(), ManaRecipes.INSTANCE.new ExplodableCrafter());
				});
		
		run(world, pos, entityIn,
				entity -> entity instanceof EntityItem && ManaRecipes.RECIPES.keySet().stream().anyMatch(item -> item.apply(((EntityItem) entity).getItem())),
				entity -> {
					Ingredient key = ManaRecipes.RECIPES.keySet().stream().filter(item -> item.apply(((EntityItem) entity).getItem())).findFirst().orElse(null);
					ManaRecipes.RECIPES.get(key).forEach(crafter -> ManaTracker.INSTANCE.addManaCraft(entity.world, entity.getPosition(), crafter.build()));
				});
	}

	public void run(World world, BlockPos pos, Entity entity, Predicate<Entity> test, Consumer<Entity> process) {
		float height = getFluidHeightForRender(world, pos, world.getBlockState(pos.up()));
		AxisAlignedBB bb = new AxisAlignedBB(pos).contract(0, 1 - height, 0);
		if (entity.getCollisionBoundingBox().intersects(bb) && test.test(entity)) process.accept(entity);
	}
}
