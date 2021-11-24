package com.teamwizardry.wizardry.common.block.entity.craftingplate;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.teamwizardry.wizardry.common.init.ModBlocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class BlockCraftingPlateEntity extends BlockEntity implements Hopper
{
    private static final int INV_SIZE = 256;
    private static final int MAX_STACK_SIZE = 6;
    private int transferCooldown = -1;
    private DefaultedList<ItemStack> inventory;
    
    public BlockCraftingPlateEntity(BlockPos pos, BlockState state)
    {
        super(ModBlocks.craftingPlateEntity, pos, state); // TODO - give actual arguments
        DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
    }
    
    private static boolean canCombine(ItemStack left, ItemStack right)
    {
        if (left.getItem() != right.getItem()) return false;
        else if (left.getDamage() != right.getDamage()) return false;
        return ItemStack.areNbtEqual(left, right);
    }
    
    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        
        nbt.putInt("TransferCooldown", this.transferCooldown);
        return nbt;
    }

    @Environment(EnvType.CLIENT)
    public static void clientTick(World world, BlockCraftingPlateEntity entity)
    {
//        if (world == null) return;
//        if (world.getTime() % 2 != 0) return;
//        Vec3d target = new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
//                              RandUtil.nextDouble(0, 0.05),
//                              RandUtil.nextDouble(-0.01, 0.01));
//        for (int i = 0; i < 5; i++)
//        {
//            Vec2d randDot = MathUtils.genRandomDotInCircle(0.1f);
//            Vec3d origin = Vec3d.ofCenter(entity.getPos(), 0.7).add(randDot.getX(), RandUtil.nextDouble(0,0.3), randDot.getY());
            // TODO - re-add glitter
//            Wizardry.PROXY.spawnParticle(
//                    new GlitterBox.GlitterBoxFactory()
//                            .setOrigin(origin)
//                            .setTarget(target)
//                            .setDrag(RandUtil.nextFloat(0.03f, 0.05f))
//                            .setGoalColor(LibTheme.accentColor)
//                            .setInitialSize(RandUtil.nextFloat(0.1f, 0.2f))
//                            .setGoalSize(0)
//                            .createGlitterBox(20));
//        }
    }
    
    @Environment(EnvType.SERVER)
    public static void serverTick(World world, BlockCraftingPlateEntity entity)
    {
        if (world == null) return;
        
        entity.transferCooldown--;
        if (entity.isOnTransferCooldown()) return;
        
        entity.setTransferCooldown(0);
        entity.updateHopper(() -> {
            for (ItemEntity itementity : entity.getCaptureItems()) {
                if (entity.captureItem(itementity)) {
                    return true;
                }
            }
            return false;
        });
    }
    
    private boolean updateHopper(Supplier<Boolean> update)
    {
        if (this.world == null || this.world.isClient) return false;
        if (this.isOnTransferCooldown()) return false;
        
        if (update.get())
        {
            this.setTransferCooldown(8);
            this.markDirty();
            return true;
        }
        return false;
    }

    /**
     * Go backwards from last slot, if item matches, stack it.
     * This ensures modifiers clump together, but don't screw up order of the same item in other parts of
     * a spell.
     *
     * @param stack The ItemStack to insert into the recipe line
     * @return The stack left
     */
    public ItemStack addItem(ItemStack stack)
    {
        for (int i = INV_SIZE - 1; i >= 0; i--)
        {
            ItemStack slotStack = getStack(i);
            if (canCombine(slotStack, stack) && slotStack.getCount() < MAX_STACK_SIZE)
            {
                int fittable = Math.min(stack.getCount(), MAX_STACK_SIZE - slotStack.getCount());
                stack.decrement(fittable);
                slotStack.increment(fittable);
                inventoryChanged();
                break;
            }
            else if (slotStack.isEmpty())
            {
                ItemStack slotBehind = i == 0 ? ItemStack.EMPTY : getStack(i-1);
                if (i == 0 || !canCombine(slotBehind, stack))
                {
                    int fittable = Math.min(stack.getCount(), MAX_STACK_SIZE);
                    ItemStack copySize = stack.copy();
                    copySize.setCount(fittable);
                    stack.decrement(fittable);
                    setStack(i, copySize);
                    inventoryChanged();
                    break;
                }
            }
        }
        return stack;
    }

    public ItemStack removeItem()
    {
        ItemStack stack = ItemStack.EMPTY;
        
        for (int i = INV_SIZE - 1; i >= 0; i--)
        {
            if (getStack(i).isEmpty()) continue;
            stack = removeStack(i);
            inventoryChanged();
            break;
        }
        
        return stack;
    }

    private List<ItemEntity> getCaptureItems()
    {
        return getInputAreaShape().getBoundingBoxes()
                           .stream()
                           .flatMap(alignedBB -> getWorld().getEntitiesByClass(ItemEntity.class,
                                   alignedBB.offset(getHopperX() - 0.5, getHopperY() - 0.5, getHopperZ() - 0.5),
                                   EntityPredicates.VALID_ENTITY).stream())
                           .collect(Collectors.toList());
    }
    
    private boolean captureItem(ItemEntity entity)
    {
        ItemStack stack = addItem(entity.getStack().copy());
        if (stack.isEmpty())
        {
            entity.remove(RemovalReason.DISCARDED);
            return true;
        }
        entity.setStack(stack);
        return false;
    }
    
    public void onEntityCollision(Entity collidedEntity)
    {
        if (!(collidedEntity instanceof ItemEntity)) return;
        BlockPos pos = this.getPos();
        VoxelShape shape = VoxelShapes.cuboid(collidedEntity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()));
        if (VoxelShapes.matchesAnywhere(shape, this.getInputAreaShape(), (a,b) -> a && b))
            this.updateHopper(() -> captureItem((ItemEntity) collidedEntity));
    }
    
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a NonNullList<ItemStack> of items currently held in the crafting plate.
     */
    public DefaultedList<ItemStack> getInventory() { return this.inventory; }
    
    public void setTransferCooldown(int ticks) { this.transferCooldown = ticks; }
    
    private boolean isOnTransferCooldown() { return this.transferCooldown > 0; }
    
    public boolean mayTransfer() { return this.transferCooldown > 8; }
    
    public DefaultedList<ItemStack> getItems() { return this.inventory; }
    
    protected void setItems(DefaultedList<ItemStack> items) { this.inventory = items; }
    
    private void inventoryChanged()
    {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }
    
    public int size() { return INV_SIZE; }
    
    @Override public boolean isEmpty() { return this.getItems().stream().allMatch(ItemStack::isEmpty); }
    
    @Override public ItemStack getStack(int index) { return this.getItems().get(index); }
    
    @Override
    public ItemStack removeStack(int index, int amount)
    {
        ItemStack stack = Inventories.splitStack(this.getItems(), index, amount);
        if (!stack.isEmpty()) this.markDirty();
        return stack;
    }
    
    @Override public ItemStack removeStack(int index) { return Inventories.removeStack(this.getItems(), index); }
    
    @Override public int getMaxCountPerStack() { return MAX_STACK_SIZE; }
    
    @Override
    public void setStack(int index, ItemStack stack)
    {
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getMaxCountPerStack())
            stack.setCount(this.getMaxCountPerStack());
        this.markDirty();
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        if (this.getWorld().getBlockEntity(this.getPos()) != this) return false;
        return player.squaredDistanceTo(Vec3d.ofCenter(this.getPos())) <= 64;
    }
    
    @Override public void clear() { this.getItems().clear(); }
    
    /**
     * Gets the world X position for this hopper entity.
     */
    @Override public double getHopperX() { return this.getPos().getX() + 0.5; }
    
    /**
     * Gets the world Y position for this hopper entity.
     */
    @Override public double getHopperY() { return (double) this.getPos().getY() + 0.5; }

    /**
     * Gets the world Z position for this hopper entity.
     */
    @Override public double getHopperZ() { return (double) this.getPos().getZ() + 0.5; }
}
