package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/10/2016.
 */
@TileRegister("crafting_plate")
public class TileCraftingPlate extends TileMod implements ITickable, IManaSink, IStructure {

    @Save
    public int craftingTimeLeft = 500;
    @Save
    public int tick;
    @Save
    public boolean isCrafting;
    @Save
    public int craftingTime = 500;
    @Save
    @Nullable
    public ItemStack output;
    @Save
    public boolean structureComplete;
    public List<ClusterObject> inventory = new ArrayList<>();
    public Random random = new Random(getPos().toLong());

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        inventory.clear();
        NBTTagList list = compound.getTagList("clusters", NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            ClusterObject cluster = new ClusterObject();
            cluster.deserializeNBT(list.getCompoundTagAt(i));
            inventory.add(cluster);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
        NBTTagList list = new NBTTagList();
        for (ClusterObject cluster : inventory) list.appendTag(cluster.serializeNBT());
        compound.setTag("clusters", list);
    }

    @NotNull
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    @Override
    public void update() {
        if (world.isRemote || !structureComplete) return;
        if (tick < 360) tick += 10;
        else tick = 0;
        if (!inventory.isEmpty()) {
            for (ClusterObject cluster : inventory) cluster.tick(world, random);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        markDirty();

        if ((output == null) && !isCrafting && !inventory.isEmpty() && (inventory.get(inventory.size() - 1).stack.getItem() instanceof Infusable)) {
            isCrafting = true;
            craftingTimeLeft = 100;
            output = inventory.remove(inventory.size() - 1).stack;
        }

        if (isCrafting) {
            if (craftingTimeLeft > 0) --craftingTimeLeft;
            else {
                isCrafting = false;
                markDirty();

                // TODO PACKET
                LibParticles.CRAFTING_ALTAR_PEARL_EXPLODE(world, new Vec3d(pos).addVector(0.5, 1, 0.5));

                if (!inventory.isEmpty())
                    for (ClusterObject cluster : inventory)
                        LibParticles.CRAFTING_ALTAR_CLUSTER_EXPLODE(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(cluster.current));

                List<ItemStack> stacks = new ArrayList<>();
                for (ClusterObject cluster : inventory) stacks.add(cluster.stack);
                SpellStack spellStack = new SpellStack(stacks);

                ItemStack stack = new ItemStack(ModItems.PEARL_NACRE);
                ItemNBTHelper.setString(stack, "type", PearlType.INFUSED.toString());

                NBTTagList list = new NBTTagList();
                for (Module module : spellStack.compiled) list.appendTag(module.serializeNBT());
                ItemNBTHelper.setList(stack, Constants.NBT.SPELL, list);

                output = stack;
                craftingTimeLeft = craftingTime;
                inventory.clear();
            }
        }
    }

    public static class ClusterObject implements INBTSerializable<NBTTagCompound> {

        public ItemStack stack;
        public Vec3d dest, current, origin;
        public InterpLine trail;
        public double tick;
        public boolean reverse;
        public double speedMultiplier;
        public long worldTime;
        public int destTime;
        private float queue;

        public ClusterObject(ItemStack stack, World world, Random random) {
            double extent = 10.0;
            double theta = 2.0f * (float) Math.PI * random.nextFloat();
            double r = extent * random.nextFloat();
            double x1 = r * MathHelper.cos((float) theta);
            double z1 = r * MathHelper.sin((float) theta);

            dest = new Vec3d(x1, 5 + (random.nextFloat() * 3), z1);
            this.stack = stack;
            origin = current = Vec3d.ZERO;
            trail = new InterpLine(origin, dest);
            reverse = random.nextBoolean();
            speedMultiplier = (2.0f * (random.nextFloat() - 0.5f)) * 3;
            worldTime = world.getTotalWorldTime();
        }

        public ClusterObject() {
        }

        public void tick(World world, Random random) {
            if (tick < 360) tick += 1 * speedMultiplier;
            else tick = 0;
            double timeDifference = (world.getTotalWorldTime() - worldTime);
            current = trail.get((float) timeDifference / destTime);

            if ((world.getTotalWorldTime() - worldTime) >= destTime) {
                double extent = 10.0;
                double theta = 2.0f * (float) Math.PI * random.nextFloat();
                double r = extent * random.nextFloat();
                double x1 = r * MathHelper.cos((float) theta);
                double z1 = r * MathHelper.sin((float) theta);

                Vec3d newDest = new Vec3d(x1, 5 + (random.nextFloat() * 3), z1);
                origin = dest;
                dest = newDest;
                trail = new InterpLine(origin, dest);
                destTime = ThreadLocalRandom.current().nextInt(1000, 5000);
                worldTime = world.getTotalWorldTime();
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("stack", stack.serializeNBT());
            compound.setDouble("dest_x", dest.xCoord);
            compound.setDouble("dest_y", dest.yCoord);
            compound.setDouble("dest_z", dest.zCoord);
            compound.setDouble("current_x", current.xCoord);
            compound.setDouble("current_y", current.yCoord);
            compound.setDouble("current_z", current.zCoord);
            compound.setDouble("origin_x", origin.xCoord);
            compound.setDouble("origin_y", origin.yCoord);
            compound.setDouble("origin_z", origin.zCoord);
            compound.setDouble("speed_multiplier", speedMultiplier);
            compound.setFloat("queue", queue);
            compound.setDouble("tick", tick);
            compound.setInteger("dest_time", destTime);
            compound.setLong("world_time", worldTime);
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
            dest = new Vec3d(nbt.getDouble("dest_x"), nbt.getDouble("dest_y"), nbt.getDouble("dest_z"));
            current = new Vec3d(nbt.getDouble("current_x"), nbt.getDouble("current_y"), nbt.getDouble("current_z"));
            origin = new Vec3d(nbt.getDouble("origin_x"), nbt.getDouble("origin_y"), nbt.getDouble("origin_z"));
            speedMultiplier = nbt.getDouble("speed_multiplier");
            queue = nbt.getFloat("queue");
            tick = nbt.getDouble("tick");
            trail = new InterpLine(origin, dest);
            destTime = nbt.getInteger("dest_time");
            worldTime = nbt.getLong("world_time");
        }
    }
}
