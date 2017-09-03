package com.teamwizardry.wizardry.common.block;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.render.ClusterObject;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Created by Saad on 6/10/2016.
 */
public class BlockCraftingPlate extends BlockModContainer implements IStructure {

	private static final AxisAlignedBB AABB_CRAFTING_PLATE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);

	public BlockCraftingPlate() {
		super("crafting_plate", Material.WOOD);
		setHardness(2.0F);
		setResistance(15.0f);
		setSoundType(SoundType.WOOD);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingPlate.class, new TileCraftingPlateRenderer());
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return 15;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileCraftingPlate();
	}

	private TileCraftingPlate getTE(IBlockAccess world, BlockPos pos) {
		return (TileCraftingPlate) world.getTileEntity(pos);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		TileCraftingPlate plate = getTE(worldIn, pos);
		if (plate == null) {
			super.breakBlock(worldIn, pos, state);
			return;
		}

		for (ClusterObject obj : plate.inventory) {
			stacks.add(obj.stack);
		}

		for (ItemStack itemStack : stacks) {
			if (!itemStack.isEmpty()) {
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStack);
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (tickStructure(worldIn, playerIn, pos)) {
			if (!(playerIn.getHeldItemMainhand().getItem() == ModItems.MAGIC_WAND)) {
				TileCraftingPlate plate = getTE(worldIn, pos);
				if (plate.isCrafting) return false;
				if (!heldItem.isEmpty()) {
					if (heldItem.getItem() == ModItems.BOOK && playerIn.isCreative()) {
						ItemStack pearl = new ItemStack(ModItems.PEARL_NACRE);

						JsonObject object = new Gson().fromJson(ItemNBTHelper.getString(heldItem, "spell_recipe", null), JsonObject.class);
						if (object == null) return false;

						ArrayList<ItemStack> inventory = new ArrayList<>();
						JsonArray array = object.getAsJsonArray("list");
						for (int i = 0; i < array.size(); i++) {
							JsonElement element = array.get(i);
							if (!element.isJsonObject()) continue;
							JsonObject obj = element.getAsJsonObject();
							String name = obj.getAsJsonPrimitive("name").getAsString();
							Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
							if (item == null) continue;
							ItemStack stack = new ItemStack(item);
							stack.setItemDamage(obj.getAsJsonPrimitive("meta").getAsInt());
							stack.setCount(obj.getAsJsonPrimitive("count").getAsInt());
							inventory.add(stack);
						}
						SpellBuilder builder = new SpellBuilder(inventory);
						NBTTagList list = new NBTTagList();
						for (Module module : builder.getSpell()) list.appendTag(module.serializeNBT());
						ItemNBTHelper.setList(pearl, Constants.NBT.SPELL, list);
						ItemNBTHelper.setFloat(pearl, Constants.NBT.RAND, playerIn.world.rand.nextFloat());

						plate.output = pearl;
						plate.markDirty();
						PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 2, 2, 500, 300, 20, false),
								new NetworkRegistry.TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));

						worldIn.playSound(null, pos, ModSounds.BASS_BOOM, SoundCategory.BLOCKS, 1f, (float) RandUtil.nextDouble(1, 1.5));
						return true;
					}

					ItemStack stack = heldItem.copy();
					stack.setCount(1);
					heldItem.setCount(heldItem.getCount() - 1);

					float yaw = playerIn.rotationYaw;
					float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
					float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
					Vec3d origin = new Vec3d(offX, playerIn.getEyeHeight(), offZ).add(playerIn.getPositionVector());

					plate.inventory.add(new ClusterObject(plate, stack, worldIn, origin.subtract(new Vec3d(pos))));
					playerIn.openContainer.detectAndSendChanges();

				} else if (plate.output != null) {
					playerIn.setHeldItem(hand, plate.output.copy());
					plate.output = null;
					playerIn.openContainer.detectAndSendChanges();

				} else if (!plate.inventory.isEmpty()) {
					playerIn.setHeldItem(hand, plate.inventory.remove(plate.inventory.size() - 1).stack);
					playerIn.openContainer.detectAndSendChanges();
				}
				worldIn.notifyBlockUpdate(pos, state, state, 3);
				return true;
			}
			return true;
		} else return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB_CRAFTING_PLATE;
	}

	@Override
	public Structure getStructure() {
		return ModStructures.INSTANCE.structures.get("crafting_altar");
	}

	@Override
	public Vec3i offsetToCenter() {
		return new Vec3i(6, 2, 6);
	}
}
