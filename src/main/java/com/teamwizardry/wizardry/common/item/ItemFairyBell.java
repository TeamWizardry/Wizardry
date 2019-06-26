package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import com.teamwizardry.wizardry.api.entity.FairyData;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.network.capability.PacketUpdateMiscCapToServer;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ItemFairyBell extends ItemMod {

	public ItemFairyBell() {
		super("fairy_bell");
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onScroll(MouseEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (Keyboard.isCreated() && event.getDwheel() != 0) {

			for (EnumHand hand : EnumHand.values()) {
				ItemStack stack = player.getHeldItem(hand);

				if (stack.getItem() != ModItems.FAIRY_BELL)
					continue;


				IMiscCapability cap = MiscCapabilityProvider.getCap(Minecraft.getMinecraft().player);
				if (cap == null) continue;

				cap.setSelectedFairy(null);

				PacketHandler.NETWORK.sendToServer(new PacketUpdateMiscCapToServer(cap.serializeNBT()));
			}
		}
	}


	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (playerIn.world.isRemote || playerIn.isSneaking())
			return super.itemInteractionForEntity(stack, playerIn, target, hand);

		if (target instanceof EntityFairy) {

			EntityFairy fairy = (EntityFairy) target;
			FairyData fairyData = fairy.getDataFairy();

			if (fairyData == null) return super.itemInteractionForEntity(stack, playerIn, target, hand);

			if (fairyData.isDepressed) {
				IMiscCapability cap = MiscCapabilityProvider.getCap(playerIn);
				if (cap != null) {
					UUID selected = cap.getSelectedFairyUUID();
					if (selected != null && selected.equals(fairy.getUniqueID())) {
						cap.setSelectedFairy(null);
						playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.25f);

						playerIn.sendStatusMessage(new TextComponentTranslation("item.wizardry:fairy_bell.status.deselected"), true);
					} else {
						cap.setSelectedFairy(fairy.getUniqueID());

						boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

						if (!movingMode) {
							playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.75f);
						} else {
							playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 1.25f);
						}

						playerIn.sendStatusMessage(new TextComponentTranslation(movingMode ? "item.wizardry:fairy_bell.status.fairy_moving" : "item.wizardry:fairy_bell.status.fairy_aiming"), true);

					}
					cap.dataChanged(playerIn);
				}
			}
		}

		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	@NotNull
	@Override
	public String getItemStackDisplayName(@NotNull ItemStack stack) {
		boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

		if (movingMode) {
			return LibrarianLib.PROXY.translate("item.wizardry:fairy_bell_moving_mode");
		} else return LibrarianLib.PROXY.translate("item.wizardry:fairy_bell_aiming_mode");
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @NotNull EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) return super.onItemRightClick(worldIn, playerIn, handIn);

		IMiscCapability cap = MiscCapabilityProvider.getCap(playerIn);
		if (cap == null) return super.onItemRightClick(worldIn, playerIn, handIn);

		EntityFairy entityFairy = cap.getSelectedFairyEntity(worldIn);


		double reach = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d posEyes = playerIn.getPositionVector().add(0, playerIn.getEyeHeight(), 0);
		RayTraceResult rayTraceResult = new RayTrace(worldIn, playerIn.getLook(1f), posEyes, reach).setEntityFilter(input -> input != null && !input.getUniqueID().equals(playerIn.getUniqueID())).setReturnLastUncollidableBlock(true).setIgnoreBlocksWithoutBoundingBoxes(true).trace();

		if (rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY && rayTraceResult.entityHit instanceof EntityFairy && entityFairy != null && rayTraceResult.entityHit.getUniqueID().equals(entityFairy.getUniqueID()))
			return super.onItemRightClick(worldIn, playerIn, handIn);

		if (playerIn.isSneaking()) {
			NBTHelper.setBoolean(stack, "moving_mode", !NBTHelper.getBoolean(stack, "moving_mode", true));

			cap.setSelectedFairy(null);
			cap.dataChanged(playerIn);

			return super.onItemRightClick(worldIn, playerIn, handIn);
		} else {

			if (entityFairy == null) return super.onItemRightClick(worldIn, playerIn, handIn);

			if (rayTraceResult.hitVec == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK)
				return super.onItemRightClick(worldIn, playerIn, handIn);

			if (worldIn.isBlockLoaded(rayTraceResult.getBlockPos())) {
				IBlockState state = worldIn.getBlockState(rayTraceResult.getBlockPos());
				if (state.getBlock().isCollidable()) {

					boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

					if (entityFairy.originPos != null) {
						if (!movingMode) {

							Vec3d hitVec = rayTraceResult.hitVec;
							Vec3d subtract = hitVec.subtract(entityFairy.getPositionVector());
							double length = subtract.length();
							hitVec = entityFairy.getPositionVector().add(subtract.normalize().scale(MathHelper.clamp(length, -3, 3)));

							entityFairy.setLookTarget(hitVec);

							playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.75f);

							cap.setSelectedFairy(null);
							cap.dataChanged(playerIn);
						}
					}
				}
			}
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote || playerIn.isSneaking())
			return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		IMiscCapability cap = MiscCapabilityProvider.getCap(playerIn);
		if (cap == null) return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		EntityFairy entityFairy = cap.getSelectedFairyEntity(worldIn);
		if (entityFairy == null) return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		cap.setSelectedFairy(null);
		cap.dataChanged(playerIn);

		if (worldIn.isBlockLoaded(pos) && !worldIn.isAirBlock(pos)) {
			IBlockState state = worldIn.getBlockState(pos);
			if (state.getBlock().isCollidable()) {

				ItemStack stack = playerIn.getHeldItem(hand);
				boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

				if (entityFairy.originPos != null) {
					if (!movingMode) {

						Vec3d hitVec = new Vec3d(pos).add(hitX, hitY, hitZ);
						Vec3d subtract = hitVec.subtract(entityFairy.getPositionVector());
						double length = subtract.length();
						hitVec = entityFairy.getPositionVector().add(subtract.normalize().scale(MathHelper.clamp(length, -3, 3)));

						entityFairy.setLookTarget(hitVec);

						playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.75f);

						cap.setSelectedFairy(null);
						cap.dataChanged(playerIn);


					} else {

						entityFairy.moveTo(pos.offset(facing));
						entityFairy.setLookTarget(null);

						playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 1.25f);
					}
				}
			}
		}

		return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
