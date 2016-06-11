package me.lordsaad.wizardry.schematic;

import com.google.common.primitives.UnsignedBytes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 6/10/2016.
 */
public class Schematic {

    short width;
    short height;
    short length;
    private BlockObject[] blockObjects;

    public Schematic(String fileName) {
        try {
            InputStream is = Schematic.class.getResourceAsStream("/assets/wizardry/schematics/" + fileName + ".schematic");
            NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(is);

            is.close();
            width = nbtdata.getShort("Width");
            height = nbtdata.getShort("Height");
            length = nbtdata.getShort("Length");
            int size = width * height * length;
            blockObjects = new BlockObject[size];

            byte[] blockIDs = nbtdata.getByteArray("Blocks");
            byte[] metadata = nbtdata.getByteArray("Data");

            int counter = 0;
            for (int schemY = 0; schemY < height; schemY++) {
                for (int schemZ = 0; schemZ < length; schemZ++) {
                    for (int schemX = 0; schemX < width; schemX++) {
                        int blockId = UnsignedBytes.toInt(blockIDs[counter]);
                        BlockPos pos = new BlockPos(schemX, schemY, schemZ);
                        IBlockState state = Block.getBlockById(blockId).getStateFromMeta(metadata[counter]);

                        blockObjects[counter] = new BlockObject(pos, state);
                        counter++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean check(World world, BlockPos pos, Block centerBlock, EntityPlayer player) {
        boolean success = true;
        List<BlockObject> blocks = new ArrayList<>();
        for (BlockObject obj : blockObjects) {
            if (obj.getState().getBlock() == Blocks.STAINED_HARDENED_CLAY && obj.getState().getBlock().getMetaFromState(obj.getState()) == 14) {
                blocks.add(new BlockObject(pos, centerBlock.getDefaultState()));
            } else
                blocks.add(new BlockObject(new BlockPos(pos.add(obj.getPos().getX(), obj.getPos().getY(), obj.getPos().getZ())).add(-(width / 2), -(height / 2) + 1, -(length / 2)), obj.getState()));
        }

        for (BlockObject obj : blocks) {
            // fix a block that turned to dirt and was supposed to be grass
            if (world.getBlockState(obj.getPos()).getBlock() == Blocks.DIRT && obj.getState().getBlock() == Blocks.GRASS)
                world.setBlockState(obj.getPos(), obj.getState());

            // fix any wrong metadata so the structure isn't stupidly strict
            if (world.getBlockState(obj.getPos()).getBlock() == obj.getState().getBlock() && world.getBlockState(obj.getPos()) != obj.getState() && obj.getState().getBlock() != centerBlock)
                world.setBlockState(obj.getPos(), obj.getState());

            if (world.getBlockState(obj.getPos()).getBlock() != obj.getState().getBlock()) {
                success = false;
                player.addChatMessage(new TextComponentString(obj.getPos() + " is " + world.getBlockState(obj.getPos()) + " but should be " + obj.getState()));
            }
        }
        if (success) player.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Structure complete."));
        else player.addChatMessage(new TextComponentString(TextFormatting.RED + "Structure incomplete."));
        return success;
    }
}
