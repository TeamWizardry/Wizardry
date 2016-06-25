package me.lordsaad.wizardry.multiblock;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import me.lordsaad.wizardry.multiblock.vanillashade.Template;
import me.lordsaad.wizardry.multiblock.vanillashade.Template.BlockInfo;

public class Structure {
	
	public static final List<IProperty<?>> IGNORE = new ArrayList<IProperty<?>>(Arrays.asList(
		BlockSlab.HALF,
		BlockStairs.SHAPE,
		BlockStairs.FACING,
		BlockPane.EAST, BlockPane.WEST, BlockPane.NORTH, BlockPane.SOUTH,
		BlockRedstoneWire.EAST, BlockRedstoneWire.WEST, BlockRedstoneWire.NORTH, BlockRedstoneWire.SOUTH, BlockRedstoneWire.POWER,
		BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.POWERED,
		BlockRedstoneRepeater.FACING, BlockRedstoneRepeater.DELAY, BlockRedstoneRepeater.LOCKED
	));
	
	/**
	 * For properties that shouldn't be ignored, but have several equivalent values
	 * Property -> list of interchangeable values
	 */
	public static final Multimap<IProperty<?>, List<?>> EQUIVALENTS = HashMultimap.create();
	static {
		EQUIVALENTS.put(BlockQuartz.VARIANT, Arrays.asList(BlockQuartz.EnumType.LINES_X, BlockQuartz.EnumType.LINES_Y, BlockQuartz.EnumType.LINES_Z));
	}
	
	protected Template template;
	protected BlockPos origin = BlockPos.ORIGIN;
	
	public Structure(String name) {
		InputStream stream = Structure.class.getResourceAsStream("/assets/wizardry/structures/" + name + ".nbt");
		if(stream != null) {
			try {
				parse(stream);
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<BlockPos> errors(World world, BlockPos checkPos) {
		
		List<BlockPos> none = errors(world, checkPos, Rotation.NONE);
		List<BlockPos> reverse = errors(world, checkPos, Rotation.CLOCKWISE_180);
		List<BlockPos> cw = errors(world, checkPos, Rotation.CLOCKWISE_90);
		List<BlockPos> ccw = errors(world, checkPos, Rotation.COUNTERCLOCKWISE_90);
		
		List<BlockPos> finalList = none;

		if(reverse.size() < finalList.size())
			finalList = reverse;
		if(cw.size() < finalList.size())
			finalList = cw;
		if(ccw.size() < finalList.size())
			finalList = ccw;
		
		return finalList;
	}
	
	public List<BlockPos> errors(World world, BlockPos checkPos, Rotation rot) {
		List<BlockPos> errorPosList = new ArrayList<>();
				
		List<BlockInfo> infos = template.infos();
		
		BlockPos offset = checkPos.subtract(origin);
		
		for (BlockInfo info : infos) {
			
			if(info.pos.equals(origin))
				continue;
			
			BlockPos worldPos = Template.transformedBlockPos(info.pos.subtract(origin), Mirror.NONE, rot).add(checkPos);
			
			IBlockState worldState = world.getBlockState(worldPos);
			IBlockState templateState = info.blockState;
			
			if(worldState.getBlock() != templateState.getBlock()) {
				errorPosList.add(worldPos);
			} else {
				Collection<IProperty<?>> worldProps = worldState.getPropertyNames();
				for (IProperty<?> prop : templateState.getPropertyNames()) {
					if(IGNORE.contains(prop))
						continue;
					
					if(!worldProps.contains(prop)) {
						errorPosList.add(worldPos);
						break;
					}
					
					boolean propsMatch = false;
					Object worldValue = worldState.getValue(prop);
					Object templateValue = templateState.getValue(prop);
					
					propsMatch = propsMatch || worldValue == templateValue; // if the properties are equal
					
					if(!propsMatch) {
						for (List<?> list : EQUIVALENTS.get(prop)) { // get equivalents for given property
							if(list.contains(worldValue) && list.contains(templateValue)) {
								propsMatch = true; // if both are in an equivalent list
								break;
							}
						}
					}
					
					if(!propsMatch) {
						errorPosList.add(worldPos);
						break;
					}
				}
			}
		}
		
		return errorPosList;
	}
	
	protected void parse(InputStream stream) {
		template = new Template();
		
		try {
			NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
			template.read(tag);
			
			NBTTagList list = tag.getTagList("palette", 10);
			
			int paletteID = -1;
			
			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				
				if("minecraft:structure_block".equals(compound.getString("Name")) &&
				   "save".equals(compound.getCompoundTag("Properties").getString("mode")) ) {
					paletteID = i;
					break;
				}
			}
			
			if(paletteID >= 0) {
				list = tag.getTagList("blocks", 10);
				for(int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound compound = list.getCompoundTagAt(i);
					if( compound.getInteger("state") == paletteID ) {
						NBTTagList posList = compound.getTagList("pos", 3);
						origin = new BlockPos(posList.getIntAt(0), posList.getIntAt(1), posList.getIntAt(2));
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
