package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItem;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItemRegistry;
import com.teamwizardry.wizardry.common.tile.TileHaloInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/21/2016.
 */
public class EntityHaloInfusionItem extends EntityMod {

	private BlockPos infuserPos;
	private int slot;
	@Nonnull
	private HaloInfusionItem haloInfusionItem = HaloInfusionItemRegistry.EMPTY;

	public EntityHaloInfusionItem(World worldIn) {
		super(worldIn);
		setSize(1F, 1F);
		isAirBorne = true;
	}

	public EntityHaloInfusionItem(World worldIn, @Nonnull HaloInfusionItem item, BlockPos infuserPos, int slot) {
		super(worldIn);
		this.infuserPos = infuserPos;
		this.slot = slot;
		setSize(1F, 1F);
		haloInfusionItem = item;
		isAirBorne = true;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world == null || infuserPos == null) return;

		TileEntity tile = world.getTileEntity(infuserPos);
		if (tile == null || !(tile instanceof TileHaloInfuser)) {
			world.removeEntity(this);
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		if (world == null || infuserPos == null) return false;

		TileEntity tile = world.getTileEntity(infuserPos);
		return tile instanceof TileHaloInfuser && !((TileHaloInfuser) tile).getHalo().isEmpty();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		TileEntity tile = world.getTileEntity(infuserPos);
		if (tile == null || !(tile instanceof TileHaloInfuser)) {
			world.removeEntity(this);
			return false;
		}

		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem.isEmpty()) {
			ItemStack slotted = getHaloInfusionItem().getStack();
			if (!slotted.isEmpty()) {
				player.setHeldItem(hand, slotted);
				setHaloInfusionItem(HaloInfusionItemRegistry.EMPTY, false);
				return true;
			} else return true;
		}

		for (HaloInfusionItem haloInfusionItem : HaloInfusionItemRegistry.getItems()) {
			if (haloInfusionItem.getStack().isItemEqual(heldItem)) {
				setHaloInfusionItem(haloInfusionItem, false);

				// todo: remove from hand
				return true;
			}
		}

		return true;
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		haloInfusionItem = HaloInfusionItem.deserialize(compound.getString("halo_infusion_item"));
		infuserPos = BlockPos.fromLong(compound.getLong("infuser_pos"));
		slot = compound.getInteger("slot");

		setHaloInfusionItem(haloInfusionItem, true);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setString("halo_infusion_item", haloInfusionItem.getNbtName());
		compound.setLong("infuser_pos", infuserPos.toLong());
		compound.setInteger("slot", slot);
	}

	public BlockPos getInfuserPos() {
		return infuserPos;
	}

	@Nonnull
	public HaloInfusionItem getHaloInfusionItem() {
		return haloInfusionItem;
	}

	public void setHaloInfusionItem(@Nonnull HaloInfusionItem haloInfusionItem, boolean soft) {
		this.haloInfusionItem = haloInfusionItem;

		if (soft) return;

		TileEntity tile = world.getTileEntity(infuserPos);
		if (tile instanceof TileHaloInfuser) {
			TileHaloInfuser haloInfuser = (TileHaloInfuser) tile;
			ItemStack halo = haloInfuser.getHalo();

			NBTTagList slots = ItemNBTHelper.getList(halo, "slots", NBTTagString.class);
			if (slots == null || slots.tagCount() < HaloInfusionItemRegistry.getItems().size() - 1) {
				slots = new NBTTagList();

				for (int i = 0; i < HaloInfusionItemRegistry.getItems().size(); i++) {
					slots.appendTag(new NBTTagString(HaloInfusionItemRegistry.EMPTY.getNbtName()));
				}
			}

			slots.set(slot, new NBTTagString(haloInfusionItem.getNbtName()));

			ItemNBTHelper.setList(halo, "slots", slots);
		}
	}
}
