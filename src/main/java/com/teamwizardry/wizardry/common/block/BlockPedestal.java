package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.math.shapes.Arc3D;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.TilePedestalRenderer;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 5/7/2016.
 */
public class BlockPedestal extends Block implements ITileEntityProvider {

    public BlockPedestal() {
        super(Material.ROCK);
        setUnlocalizedName("pedestal");
        setRegistryName("pedestal");
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TilePedestal.class, "pedestal");
        setCreativeTab(Wizardry.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new TilePedestalRenderer());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TilePedestal te = getTE(world, pos);
            if (te.getStack() == null && player.getHeldItem(hand) != null) {

                te.setStack(player.getHeldItem(hand));
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                player.openContainer.detectAndSendChanges();
            } else {
                ItemStack stack = te.getStack();
                te.setStack(null);
                if (!player.inventory.addItemStackToInventory(stack)) {
                    EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack);
                    world.spawnEntityInWorld(entityItem);
                } else {
                    player.openContainer.detectAndSendChanges();
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TilePedestal te = getTE(worldIn, pos);
        if (te.getConnectedManaBattery() == null) {
            for (int i = -7; i < 7; i++) {
                for (int j = -7; j < 7; j++) {
                    BlockPos blockpos = pos.add(i, +2, j);

                    if (worldIn.getBlockState(blockpos).getBlock() == ModBlocks.MANA_BATTERY)
                        te.setConnectedManaBattery(blockpos);
                }
            }
        } else {
            Vec3d origin = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vec3d target = new Vec3d(te.getConnectedManaBattery().getX() + 0.5, te.getConnectedManaBattery().getY() + 0.5, te.getConnectedManaBattery().getZ() + 0.5);
            te.setPoints(new Arc3D(target, origin, (float) ThreadLocalRandom.current().nextDouble(-3, 3), 30).getPoints());
            Collections.reverse(te.getPoints());
            te.setDraw(true);
        }
    }

    private TilePedestal getTE(World world, BlockPos pos) {
        return (TilePedestal) world.getTileEntity(pos);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TilePedestal();
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }
}
