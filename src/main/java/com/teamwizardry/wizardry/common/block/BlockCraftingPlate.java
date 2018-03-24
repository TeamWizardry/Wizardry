package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.IInfusable;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Demoniaque on 6/10/2016.
 */
public class BlockCraftingPlate extends BlockModContainer implements IStructure {

	private static final AxisAlignedBB AABB_CRAFTING_PLATE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);

	public BlockCraftingPlate() {
		super("crafting_plate", Material.WOOD);
		setHardness(2.0F);
		setResistance(15.0f);
		setSoundType(SoundType.WOOD);
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (isStructureComplete(worldIn, pos)) {
			TileCraftingPlate plate = getTE(worldIn, pos);
			if (!plate.inputPearl.getHandler().getStackInSlot(0).isEmpty()) return false;
			if (!heldItem.isEmpty()) {
				if (heldItem.getItem() == ModItems.BOOK && playerIn.isCreative()) {
					ItemStack pearl = new ItemStack(ModItems.PEARL_NACRE);

					NBTTagList spellList = ItemNBTHelper.getList(heldItem, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
					if (spellList == null) return false;

					SpellBuilder builder = new SpellBuilder(SpellUtils.getSpellChains(spellList), true, true);

					//Color lastColor = SpellUtils.getAverageSpellColor(builder.getSpell());
//
					//float[] hsv = ColorUtils.getHSVFromColor(lastColor);
					//ItemNBTHelper.setFloat(pearl, "hue", hsv[0]);
					//ItemNBTHelper.setFloat(pearl, "saturation", hsv[1]);
					ItemNBTHelper.setFloat(pearl, Constants.NBT.RAND, playerIn.world.rand.nextFloat());
					ItemNBTHelper.setList(pearl, Constants.NBT.SPELL, spellList);

					plate.outputPearl.getHandler().setStackInSlot(0, pearl);
					plate.markDirty();
					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 2, 2, 500, 300, 20, false),
							new NetworkRegistry.TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));

					worldIn.playSound(null, pos, ModSounds.BASS_BOOM, SoundCategory.BLOCKS, 1f, (float) RandUtil.nextDouble(1, 1.5));
					return true;
				} else {
					ItemStack stack = heldItem.copy();
					stack.setCount(1);
					heldItem.shrink(1);

					if (stack.getItem() instanceof IInfusable) {
						plate.inputPearl.getHandler().setStackInSlot(0, stack);
					} else {
						for (int i = 0; i < plate.realInventory.getHandler().getSlots(); i++) {
							if (plate.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
								plate.realInventory.getHandler().setStackInSlot(i, stack);

								int finalI = i;
								ClientRunnable.run(new ClientRunnable() {
									@Override
									@SideOnly(Side.CLIENT)
									public void runIfClient() {
										if (plate.renderHandler != null)
											((TileCraftingPlateRenderer) plate.renderHandler).addAnimation(finalI, true, false);
									}
								});
								break;
							}
						}
					}

					playerIn.openContainer.detectAndSendChanges();
					worldIn.notifyBlockUpdate(pos, state, state, 3);
					return true;
				}
			} else {

				if (!plate.outputPearl.getHandler().getStackInSlot(0).isEmpty()) {
					playerIn.setHeldItem(hand, plate.outputPearl.getHandler().extractItem(0, 1, false));
					playerIn.openContainer.detectAndSendChanges();
					worldIn.notifyBlockUpdate(pos, state, state, 3);

					return true;
				} else {
					boolean empty = true;
					for (int i = 0; i < plate.realInventory.getHandler().getSlots(); i++) {
						if (!plate.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
							empty = false;
							break;
						}
					}
					if (!empty) {
						for (int i = 0; i < plate.realInventory.getHandler().getSlots(); i++) {
							ItemStack extracted = plate.realInventory.getHandler().getStackInSlot(i);
							if (!extracted.isEmpty()) {
								playerIn.addItemStackToInventory(plate.realInventory.getHandler().extractItem(i, extracted.getCount(), false));
								worldIn.notifyBlockUpdate(pos, state, state, 3);

								plate.positions[i] = Vec3d.ZERO;

								break;
							}
						}
					}
				}
				return true;
			}

		} else {
			if (playerIn.isCreative() && playerIn.isSneaking()) {
				tickStructure(worldIn, playerIn, pos);
			} else {
				TileCraftingPlate plate = getTE(worldIn, pos);
				plate.revealStructure = !plate.revealStructure;
				plate.markDirty();
			}
		}
		return heldItem.isEmpty();
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
	public CachedStructure getStructure() {
		return ModStructures.INSTANCE.structures.get("crafting_altar");
	}

	@Override
	public Vec3i offsetToCenter() {
		return new Vec3i(4, 1, 4);
	}
}
