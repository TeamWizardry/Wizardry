package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItemRegistry;
import com.teamwizardry.wizardry.client.render.block.TileHaloInfuserRenderer;
import com.teamwizardry.wizardry.common.entity.EntityHaloInfusionItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by Demoniaque.
 */
@TileRegister("halo_infuser")
@TileRenderer(TileHaloInfuserRenderer.class)
public class TileHaloInfuser extends TileModTickable {

	@Module
	private ModuleInventory haloInv = new ModuleInventory(1);

	private ArrayList<EntityHaloInfusionItem> entities = new ArrayList<>();

	@Override
	public void onLoad() {
		super.onLoad();
		refreshEntities();
	}

	@Override
	public void tick() {
		if (entities.size() != HaloInfusionItemRegistry.getItems().size()) {
			refreshEntities();
			updateItems(true);
		}
	}

	public ItemStack getHalo() {
		return haloInv.getHandler().getStackInSlot(0);
	}

	public void setHalo(ItemStack stack) {
		this.haloInv.getHandler().setStackInSlot(0, stack);
		updateItems(true);
	}

	public void refreshEntities() {
		entities.forEach(entityHaloInfusionItem -> world.removeEntity(entityHaloInfusionItem));
		entities.clear();

		int count = HaloInfusionItemRegistry.getItems().size();
		double radius = 3;
		for (int i = 0; i < count; i++) {

			float angle = (float) (i * Math.PI * 2.0 / count);
			double x = (pos.getX() + 0.5 + MathHelper.cos(angle) * radius);
			double z = (pos.getZ() + 0.5 + MathHelper.sin(angle) * radius);

			EntityHaloInfusionItem entity = new EntityHaloInfusionItem(world, HaloInfusionItemRegistry.EMPTY, getPos(), i);
			entity.setPosition(x, pos.getY() + 2, z);
			entity.forceSpawn = true;
			world.spawnEntity(entity);
			entities.add(entity);
		}
	}

	public void updateItems(boolean soft) {
		NBTTagList slots = ItemNBTHelper.getList(getHalo(), "slots", NBTTagString.class);
		if (slots == null || slots.tagCount() < HaloInfusionItemRegistry.getItems().size() - 1) {
			slots = new NBTTagList();

			for (int i = 0; i < HaloInfusionItemRegistry.getItems().size(); i++) {
				slots.appendTag(new NBTTagString(HaloInfusionItemRegistry.EMPTY.getNbtName()));
			}
			ItemNBTHelper.setList(getHalo(), "slots", slots);
		}

		for (int i = 0; i < HaloInfusionItemRegistry.getItems().size(); i++) {

			EntityHaloInfusionItem entity = entities.get(i);

			String itemName = slots.getStringTagAt(i);

			entity.setHaloInfusionItem(HaloInfusionItemRegistry.getItemFromName(itemName), soft);
		}
	}

	public ItemStack extractHalo() {
		return haloInv.getHandler().extractItem(0, 1, false);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 4096;
	}
}
