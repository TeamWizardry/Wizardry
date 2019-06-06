package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.mana.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.common.tile.TileJar;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function2;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static net.minecraft.util.EnumActionResult.PASS;
import static net.minecraft.util.EnumActionResult.SUCCESS;

/**
 * Created by Demoniaque on 8/27/2016.
 */
public class ItemJar extends ItemMod implements IItemColorProvider {

	public ItemJar() {
		super("jar_item", "jar_empty", "jar_jam", "jar_fairy");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		if (stack.getItemDamage() == 1) return EnumAction.DRINK;
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new WizardryCapabilityProvider(new CustomWizardryCapability(1000, 1000, 0, 0));
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		stack.shrink(1);
		if (entityLiving instanceof EntityPlayer) {
			((EntityPlayer) entityLiving).addItemStackToInventory(new ItemStack(ModBlocks.JAR));
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			entityplayer.getFoodStats().addStats(4, 7f);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, ModSounds.SPARKLE, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			entityLiving.addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 200, 1, true, false));
		}

		return stack;
	}

	@NotNull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() != 1) {
			BlockPos offset = pos.offset(side);
			IBlockState offsetState = world.getBlockState(offset);
			if (offsetState.getBlock().isAir(offsetState, world, offset) || offsetState.getBlock().isReplaceable(world, offset)) {

				if (!world.mayPlace(ModBlocks.JAR, offset, false, side, player)) return PASS;

				boolean replacable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
				boolean success = world.setBlockState(replacable ? pos : offset, ModBlocks.JAR.getDefaultState());
				if (success) {
					world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1, 1, false);

					if (!player.isCreative())
						stack.shrink(1);

					TileEntity tileEntity = world.getTileEntity(replacable ? pos : offset);
					if (tileEntity instanceof TileJar) {
						TileJar jar = (TileJar) tileEntity;
						jar.color = new Color(NBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF));
						jar.age = NBTHelper.getInt(stack, Constants.NBT.FAIRY_AGE, 0);
						jar.hasFairy = NBTHelper.getBoolean(stack, Constants.NBT.FAIRY_INSIDE, false);
						jar.markDirty();
						world.checkLight(replacable ? pos : offset);
					}
				}

				return SUCCESS;
			}
		}
		return PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() == 1) {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> ((tintIndex == 0) && (stack.getItemDamage() != 0)) ? NBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF) : 0xFFFFFF;
	}
}
