package com.teamwizardry.wizardry.client.multiblock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.teamwizardry.wizardry.client.multiblock.vanillashade.Template;
import com.teamwizardry.wizardry.client.multiblock.vanillashade.Template.BlockInfo;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    public Rotation matchedRotation;
    protected Template template;
    protected TemplateBlockAccess blockAccess;
    protected BlockPos origin = BlockPos.ORIGIN;
    
    public Structure(String name) {
        InputStream stream = Structure.class.getResourceAsStream("/assets/wizardry/schematics/" + name + ".nbt");
        if (stream != null) {
            try {
                parse(stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<BlockInfo> blockInfos() {
        return template.infos() == null ? ImmutableList.of() : template.infos();
    }

    public TemplateBlockAccess getBlockAccess() {
        return blockAccess;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public void setOrigin(BlockPos pos) {
        origin = pos;
    }

    public StructureMatchResult match(World world, BlockPos checkPos) {

        StructureMatchResult none = match(world, checkPos, Rotation.NONE);
        StructureMatchResult reverse = match(world, checkPos, Rotation.CLOCKWISE_180);
        StructureMatchResult cw = match(world, checkPos, Rotation.CLOCKWISE_90);
        StructureMatchResult ccw = match(world, checkPos, Rotation.COUNTERCLOCKWISE_90);

        StructureMatchResult finalList = none;
        matchedRotation = Rotation.NONE;

        if (finalList == null || reverse != null && reverse.allErrors.size() < finalList.allErrors.size()) {
            finalList = reverse;
            matchedRotation = Rotation.CLOCKWISE_180;
        }
        if (finalList == null || cw != null && cw.allErrors.size() < finalList.allErrors.size()) {
            finalList = cw;
            matchedRotation = Rotation.CLOCKWISE_90;
        }
        if (finalList == null || ccw != null && ccw.allErrors.size() < finalList.allErrors.size()) {
            finalList = ccw;
            matchedRotation = Rotation.COUNTERCLOCKWISE_90;
        }

        return finalList;
    }

    public StructureMatchResult match(World world, BlockPos checkPos, Rotation rot) {
        StructureMatchResult result = new StructureMatchResult(checkPos.subtract(origin), rot, this);
        
        List<Template.BlockInfo> infos = template.infos();

        if (infos == null)
            return null;

        for (Template.BlockInfo info : infos) {

            if (info.pos.equals(origin))
                continue;

            BlockPos worldPos = Template.transformedBlockPos(info.pos.subtract(origin), Mirror.NONE, rot).add(checkPos);

            IBlockState worldState = world.getBlockState(worldPos);
            IBlockState templateState = info.blockState;
            boolean match = true;
            
            if (worldState.getBlock() != templateState.getBlock()) {
            	if(worldState.getBlock() == Blocks.AIR) {
                	result.airErrors.add(info.pos);
                } else {
                	result.nonAirErrors.add(info.pos);
                }
            	match = false;
            } else {
                Collection<IProperty<?>> worldProps = worldState.getPropertyNames();
                for (IProperty<?> prop : templateState.getPropertyNames()) {
                    if (IGNORE.contains(prop))
                        continue;

                    if (!worldProps.contains(prop)) {
                    	result.propertyErrors.add(info.pos);
                    	match = false;
                        break;
                    }

                    boolean propsMatch = false;
                    Object worldValue = worldState.getValue(prop);
                    Object templateValue = templateState.getValue(prop);

                    propsMatch = propsMatch || worldValue == templateValue; // if the properties are equal

                    if (!propsMatch) {
                        for (List<?> list : EQUIVALENTS.get(prop)) { // get equivalents for given property
                            if (list.contains(worldValue) && list.contains(templateValue)) {
                                propsMatch = true; // if both are in an equivalent list
                                break;
                            }
                        }
                    }

                    if (!propsMatch) {
                    	result.propertyErrors.add(info.pos);
                    	match = false;
                        break;
                    }
                }
            }
            
            if(match)
            	result.matches.add(info.pos);
            else
            	result.allErrors.add(info.pos);
        }

        return result;
    }

    protected void parse(InputStream stream) {
        template = new Template();
        blockAccess = new TemplateBlockAccess(template);
        try {
            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            template.read(tag);

            NBTTagList list = tag.getTagList("palette", 10);

            int paletteID = -1;

            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound compound = list.getCompoundTagAt(i);

                if ("minecraft:structure_block".equals(compound.getString("Name"))) {
                    paletteID = i;
                    break;
                }
            }

            if (paletteID >= 0) {
                list = tag.getTagList("block", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound compound = list.getCompoundTagAt(i);
                    if (compound.getInteger("state") == paletteID) {
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
