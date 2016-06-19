package me.lordsaad.wizardry.blocks;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.tileentities.TileEntityManaBattery;
import me.lordsaad.wizardry.tileentities.TileMagiciansWorktable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockManaBattery extends Block implements ITileEntityProvider
{
	public BlockManaBattery()
	{
		super(Material.GROUND);
		setUnlocalizedName("mana_battery");
	    setRegistryName("mana_battery");
	    GameRegistry.register(this);
	    GameRegistry.register(new ItemBlock(this), getRegistryName());
	    GameRegistry.registerTileEntity(TileEntityManaBattery.class, "mana_battery");
	    setCreativeTab(Wizardry.tab);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() 
	{
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{	
		return new TileEntityManaBattery();
	}
}
