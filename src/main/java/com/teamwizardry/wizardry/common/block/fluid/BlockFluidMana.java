package com.teamwizardry.wizardry.common.block.fluid;

import com.teamwizardry.librarianlib.features.base.fluid.BlockModFluid;
import com.teamwizardry.librarianlib.features.base.fluid.ModFluid;
import com.teamwizardry.librarianlib.features.forgeevents.EntityUpdateEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.FluidTracker;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.crafting.mana.FluidRecipeLoader;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipes;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
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
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BlockFluidMana extends BlockModFluid {

	public BlockFluidMana(ModFluid mana) {
		super(mana, Material.WATER);
		setQuantaPerBlock(6);
	}

	@Override
	public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
		super.updateTick(world, pos, state, rand);

		if (world.isRemote)
			LibParticles.FIZZING_AMBIENT(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	@SubscribeEvent
	public static void onEntityUpdate(EntityUpdateEvent event) {
		Entity entityIn = event.getEntity();
		BlockPos pos = entityIn.getPosition();
		World world = entityIn.world;
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == ModFluids.MANA.getActualBlock()) {
			// Fizz all entities in the pool
			if (world.isRemote)
				run(world, pos, state.getBlock(), entityIn,
					entity -> true,
					entity -> LibParticles.FIZZING_AMBIENT(world, entityIn.getPositionVector()));

			// Nullify gravity of player
			if (!world.isRemote)
				run(world, pos, state.getBlock(), entityIn,
						entity -> entity instanceof EntityLivingBase,
						entity -> {
							((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));

							if (RandUtil.nextInt(50) == 0) entity.attackEntityFrom(DamageSourceMana.INSTANCE, 0.1f);
						});

			// Subtract player food
			run(world, pos, state.getBlock(), entityIn,
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
			run(world, pos, state.getBlock(), entityIn,
					entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof IExplodable,
					entity -> FluidTracker.INSTANCE.addManaCraft(entity.world, entity.getPosition(), new ManaRecipes.ExplodableCrafter()));
		}


		run(world, pos, state.getBlock(), entityIn,
				entity -> entity instanceof EntityItem && ManaRecipes.RECIPES.keySet().stream().anyMatch(item -> item.apply(((EntityItem) entity).getItem())),
				entity -> {
					List<Map.Entry<Ingredient, FluidRecipeLoader.FluidCrafter>> allEntries = ManaRecipes.RECIPES.entries().stream().filter(entry ->
							entry.getValue().getFluid().getBlock() == state.getBlock() &&
									entry.getKey().apply(((EntityItem) entity).getItem())).collect(Collectors.toList());
					allEntries.forEach(crafter -> FluidTracker.INSTANCE.addManaCraft(entity.world, entity.getPosition(), crafter.getValue().build()));
				});
	}

	public static void run(World world, BlockPos pos, Block block, Entity entity, Predicate<Entity> test, Consumer<Entity> process) {
		if (!(block instanceof IFluidBlock)) return;
		float height;
		IBlockState up = world.getBlockState(pos.up());
		if (up.getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock)
			height = 1f;
		else
			height = ((IFluidBlock) block).getFilledPercentage(world, pos) * 0.875f;
		AxisAlignedBB bb = new AxisAlignedBB(pos).contract(0, 1 - height, 0);
		AxisAlignedBB entityBox = entity.getCollisionBoundingBox();
		if ((entityBox == null || entityBox.intersects(bb))
				&& test.test(entity)) process.accept(entity);
	}
}
