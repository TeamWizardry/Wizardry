package com.teamwizardry.wizardry.crafting.mana;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModSounds;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class FluidRecipeBuilder
{
    private static Set<BlockPos> allLiquidInPool(World world, BlockPos pos, int needed, Fluid fluid) {
        if (needed <= 0) return Sets.newHashSet();

        Block block = fluid.getBlock();
        if (block == null) return Sets.newHashSet();

        IBlockState sourceBlock = block.getDefaultState();

        BlockPos.MutableBlockPos topPos = new BlockPos.MutableBlockPos(pos);
        IBlockState stateAt = world.getBlockState(topPos);
        boolean lastWasFluid = false;
        while (stateAt.getBlock() == block) {
            lastWasFluid = stateAt == sourceBlock;
            stateAt = world.getBlockState(topPos.setPos(topPos.getX(), topPos.getY() + 1, topPos.getZ()));
        }
        topPos.setPos(topPos.getX(), topPos.getY() - 1, topPos.getZ());

        BlockPos.MutableBlockPos tool = new BlockPos.MutableBlockPos();
        Set<BlockPos> positions = Sets.newHashSet(topPos.toImmutable());

        Set<BlockPos> visited = Sets.newHashSet(positions);
        Set<BlockPos> resultants = Sets.newHashSet();
        if (lastWasFluid)
            resultants.addAll(positions);

        while (resultants.size() < needed && !positions.isEmpty() && visited.size() < 1000) {
            BlockPos point = positions.iterator().next();
            positions.remove(point);
            for (int index = EnumFacing.VALUES.length - 1; index >= 0; index--) {
                EnumFacing facing = EnumFacing.byIndex(index);
                tool.setPos(point.getX() + facing.getXOffset(),
                        point.getY() + facing.getYOffset(),
                        point.getZ() + facing.getZOffset());

                if (!visited.contains(tool)) {
                    BlockPos immutable = tool.toImmutable();
                    visited.add(immutable);
                    stateAt = world.getBlockState(tool);
                    if (stateAt.getBlock() == block) {
                        positions.add(immutable);
                        if (stateAt == sourceBlock) {
                            resultants.add(immutable);

                            if (resultants.size() >= needed)
                                return resultants;
                        }
                    }
                }
            }
        }

        return resultants;
    }
    
    public static FluidCrafter buildFluidCrafter(String identifier, ItemStack outputItem, Ingredient input, List<Ingredient> extraInputs, Fluid fluid, int duration, int required, boolean consume, boolean explode, boolean bubbling, boolean harp, boolean instant) {
        Ingredient outputIngredient = Ingredient.fromStacks(outputItem);
        List<Ingredient> inputs = Lists.newArrayList(extraInputs);

        return new FluidCrafter((world, pos, items) -> {
            if (allLiquidInPool(world, pos, required, fluid).size() < required)
                return false;

            List<ItemStack> list = items.stream().map(entity -> entity.getItem().copy()).collect(Collectors.toList());

            List<Ingredient> inputList = new ArrayList<>(inputs);
            inputList.add(input);
            
            for (Ingredient itemIn : inputList) {
                boolean foundMatch = false;
                List<ItemStack> toRemove = new LinkedList<>();
                for (ItemStack item : list) {
                    if (itemIn.apply(item) && !outputIngredient.apply(item)) {
                        foundMatch = true;
                        item.shrink(1);
                        if (item.isEmpty())
                            toRemove.add(item);
                        break;
                    }
                }
                if (!foundMatch)
                    return false;
                list.removeAll(toRemove);
                toRemove.clear();
            }
            return true;
        }, (world, pos, items, currentDuration) -> {
            EntityItem entityItem = items.stream().filter(entity -> input.apply(entity.getItem())).findFirst().orElse(null);
            if (entityItem != null) {
                if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
                if (bubbling && currentDuration % 10 == 0)
                    world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
            }
        }, (world, pos, items, currentDuration) -> {
            if (consume) {
                Block block = fluid.getBlock();
                if (block != null) {
                    IBlockState defaultState = block.getDefaultState();
                    Iterator<IProperty<?>> properties = defaultState.getPropertyKeys().iterator();
                    IBlockState drainState = defaultState;
                    if (properties.hasNext())
                        drainState = drainState.cycleProperty(properties.next());

                    for (BlockPos position : allLiquidInPool(world, pos, required, fluid))
                        world.setBlockState(position, drainState);
                }
            }

            List<Ingredient> inputList = new ArrayList<>(inputs);
            inputList.add(input);

            int count = 0;
            recipeLoop:
            {
                boolean itemFound = false;
                do
                {
                    for (Ingredient itemIn : inputList) {
                        for (EntityItem entity : items) {
                            if (itemIn.apply(entity.getItem()) && !outputIngredient.apply(entity.getItem())) {
                                entity.getItem().shrink(1);
                                if (entity.getItem().isEmpty())
                                    entity.setDead();
                                break;
                            }
                        }
                    }
                    
                    count += outputItem.getCount();
                    
                    for (Ingredient itemIn : inputList)
                    {
                        itemFound = false;
                        for (EntityItem entity : items)
                        {
                            if (itemIn.apply(entity.getItem()) && !outputIngredient.apply(entity.getItem()))
                            {
                                itemFound = true;
                                break;
                            }
                        }
                        if (!itemFound)
                            break recipeLoop;
                    }
                }
                while (duration == 0 && !consume && instant && itemFound);
            }           

            EntityItem output;
            do
            {
                output = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, outputItem.copy());
                output.getItem().setCount(Math.min(count, outputItem.getMaxStackSize()));
                output.motionX = 0;
                output.motionY = 0;
                output.motionZ = 0;
                output.forceSpawn = true;
                world.spawnEntity(output);
                
                count -= outputItem.getMaxStackSize();
            }
            while (count > 0);

            if (explode) {
                PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.posX, output.posY, output.posZ, 256));
                PosUtils.boom(world, output.getPositionVector(), output, 3, false);
            }

            if (harp)
                world.playSound(null, output.posX, output.posY, output.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
        }, identifier, duration).setInputs(input, inputs).setOutput(outputItem).setDoesConsume(consume).setRequired(required).setFluid(fluid);
    }

    public static FluidCrafter buildFluidCrafter(String identifier, IBlockState outputBlock, Ingredient input, List<Ingredient> extraInputs, Fluid fluid, int duration, int required, boolean consume, boolean explode, boolean bubbling, boolean harp) {
        List<Ingredient> inputs = Lists.newArrayList(extraInputs);

        FluidCrafter builder = new FluidCrafter((world, pos, items) -> {
            if (allLiquidInPool(world, pos, required, fluid).size() < required)
                return false;

            List<ItemStack> list = items.stream().map(entity -> entity.getItem().copy()).collect(Collectors.toList());
            
            List<Ingredient> inputList = new ArrayList<>(inputs);
            inputList.add(input);
            
            for (Ingredient itemIn : inputList) {
                boolean foundMatch = false;
                List<ItemStack> toRemove = new LinkedList<>();
                for (ItemStack item : list) {
                    if (itemIn.apply(item)) {
                        foundMatch = true;
                        item.shrink(1);
                        if (item.isEmpty())
                            toRemove.add(item);
                        break;
                    }
                }
                if (!foundMatch)
                    return false;
                list.removeAll(toRemove);
                toRemove.clear();
            }
            return true;
        }, (world, pos, items, currentDuration) -> {
            EntityItem entityItem = items.stream().filter(entity -> input.apply(entity.getItem())).findFirst().orElse(null);
            if (entityItem != null) {
                if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
                if (bubbling && currentDuration % 10 == 0)
                    world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
            }
        }, (world, pos, items, currentDuration) -> {
            if (consume) {
                Block block = fluid.getBlock();
                if (block != null) {
                    IBlockState defaultState = block.getDefaultState();
                    Iterator<IProperty<?>> properties = defaultState.getPropertyKeys().iterator();
                    IBlockState drainState = defaultState;
                    if (properties.hasNext())
                        drainState = drainState.cycleProperty(properties.next());

                    for (BlockPos position : allLiquidInPool(world, pos, required, fluid))
                        world.setBlockState(position, drainState);
                }
            }

            List<Ingredient> inputList = new ArrayList<>(inputs);
            inputList.add(input);

            for (Ingredient itemIn : inputList) {
                for (EntityItem entity : items) {
                    if (itemIn.apply(entity.getItem())) {
                        entity.getItem().shrink(1);
                        if (entity.getItem().isEmpty())
                            entity.setDead();
                    }
                }
            }

            world.setBlockState(pos, outputBlock);
            Vec3d output = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            if (explode) {
                PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output, Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.x, output.y, output.z, 256));
                PosUtils.boom(world, output, null, 3, false);
            }

            if (harp)
                world.playSound(null, output.x, output.y, output.z, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
        }, identifier, duration).setInputs(input, inputs).setIsBlock(true).setDoesConsume(true).setRequired(required).setFluid(fluid);

        Fluid fluidOutput = FluidRegistry.lookupFluidForBlock(outputBlock.getBlock());
        if (fluidOutput != null)
            builder.setOutput(new FluidStack(fluidOutput, 1000));
        else
            builder.setOutput(new ItemStack(outputBlock.getBlock(), 1, outputBlock.getBlock().damageDropped(outputBlock)));

        return builder;
    }

    @FunctionalInterface
    private interface ManaCrafterPredicate {
        boolean check(World world, BlockPos pos, List<EntityItem> items);
    }

    @FunctionalInterface
    private interface ManaCrafterConsumer {
        void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration);
    }
    
    public static class FluidCrafter {
        private ManaCrafterPredicate isValid;
        private ManaCrafterConsumer tick;
        private ManaCrafterConsumer finish;
        private String identifier;
        private int duration;

        private Fluid fluid = ModFluids.MANA.getActual();
        private Ingredient primaryInput = Ingredient.EMPTY;
        private List<Ingredient> extraInputs = Collections.emptyList();
        private ItemStack output = ItemStack.EMPTY;
        private FluidStack fluidOutput = null;
        private boolean block = false;
        private boolean doesConsume = false;
        private int required = 1;

        private FluidCrafter(ManaCrafterPredicate isValid, ManaCrafterConsumer tick, ManaCrafterConsumer finish, String identifier, int duration) {
            this.isValid = isValid;
            this.tick = tick;
            this.finish = finish;
            this.identifier = identifier;
            this.duration = duration;
        }

        private FluidCrafter setInputs(Ingredient primary, List<Ingredient> inputs) {
            this.primaryInput = primary;
            this.extraInputs = inputs;
            return this;
        }

        private FluidCrafter setOutput(ItemStack output) {
            this.output = output;
            return this;
        }

        private FluidCrafter setFluid(Fluid fluid) {
            this.fluid = fluid;
            return this;
        }

        private FluidCrafter setIsBlock(boolean state) {
            this.block = state;
            return this;
        }

        private FluidCrafter setDoesConsume(boolean state) {
            this.doesConsume = state;
            return this;
        }

        public Ingredient getMainInput() {
            return primaryInput;
        }

        public List<Ingredient> getInputs() {
            return extraInputs;
        }

        public ItemStack getOutput() {
            return output;
        }

        private FluidCrafter setOutput(FluidStack output) {
            this.fluidOutput = output;
            return this;
        }

        public Fluid getFluid() {
            return fluid;
        }

        public FluidStack getFluidOutput() {
            return fluidOutput;
        }

        public boolean isBlock() {
            return block;
        }

        public boolean doesConsume() {
            return doesConsume;
        }

        public int getRequired() {
            return required;
        }

        private FluidCrafter setRequired(int required) {
            this.required = required;
            return this;
        }

        public FluidCraftInstance build() {
            return new FluidCraftInstance(identifier, duration, fluid) {
                @Override
                public boolean isValid(World world, BlockPos pos, List<EntityItem> items) {
                    Block at = world.getBlockState(pos).getBlock();
                    return at == fluid.getBlock() && isValid.check(world, pos, items);
                }

                @Override
                public void tick(World world, BlockPos pos, List<EntityItem> items) {
                    super.tick(world, pos, items);
                    tick.consume(world, pos, items, currentDuration);
                }

                @Override
                public void finish(World world, BlockPos pos, List<EntityItem> items) {
                    finish.consume(world, pos, items, currentDuration);
                }
            };
        }
    }
}
