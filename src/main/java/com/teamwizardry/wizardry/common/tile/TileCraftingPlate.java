package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.utils.MathUtils;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.lib.LibTheme;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TileCraftingPlate extends TileEntity implements ITickableTileEntity, IHopper {

    private static final int INV_SIZE = 256;
    private static final int MAX_STACK_SIZE = 6;
    private int transferCooldown = -1;
    private NonNullList<ItemStack> inventory;

    public TileCraftingPlate() {
        super(LibTileEntityType.CRAFTING_PLATE);

        inventory = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() != stack2.getItem()) {
            return false;
        } else if (stack1.getDamage() != stack2.getDamage()) {
            return false;
        } else if (stack1.getCount() + stack2.getCount() > MAX_STACK_SIZE) {
            return false;
        } else {
            return ItemStack.areItemStackTagsEqual(stack1, stack2);
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.inventory = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.inventory);

        this.transferCooldown = compound.getInt("TransferCooldown");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, this.inventory);

        compound.putInt("TransferCooldown", this.transferCooldown);
        return compound;
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            --this.transferCooldown;
            if (!isOnTransferCooldown()) {
                setTransferCooldown(0);
                updateHopper(() -> {
                    for (ItemEntity itementity : getCaptureItems()) {
                        if (captureItem(itementity)) {
                            return true;
                        }
                    }
                    return false;
                });

            }
        }

        if (world != null && world.isRemote && world.getGameTime() % 2 == 0) {

            Vec3d target = new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
                    RandUtil.nextDouble(0, 0.05),
                    RandUtil.nextDouble(-0.01, 0.01));
            for (int i = 0; i < 5; i++) {
                Vec2d randDot = MathUtils.genRandomDotInCircle(0.1f);
                Vec3d origin = new Vec3d(getPos()).add(0.5 + randDot.getX(),
                        0.7 + RandUtil.nextDouble(0, 0.3),
                        0.5 + randDot.getY());
                Wizardry.PROXY.spawnParticle(
                        new GlitterBox.GlitterBoxFactory()
                                .setOrigin(origin)
                                .setTarget(target)
                                .setDrag(RandUtil.nextFloat(0.03f, 0.05f))
                                .setGoalColor(LibTheme.accentColor)
                                .setInitialSize(RandUtil.nextFloat(0.1f, 0.2f))
                                .setGoalSize(0)
                                .createGlitterBox(20));
            }
        }
    }

    private boolean updateHopper(Supplier<Boolean> update) {
        if (this.world != null && !this.world.isRemote) {
            if (!this.isOnTransferCooldown()) {
                boolean success = update.get();

                if (success) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }
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
    public ItemStack addItem(ItemStack stack) {
        for (int i = getSizeInventory() - 1; i >= 0; i--) {
            ItemStack slotStack = getStackInSlot(i);
            if (canCombine(slotStack, stack)) {
                int maxFittable = MAX_STACK_SIZE - slotStack.getCount();
                int reallyFittable = Math.min(stack.getCount(), maxFittable);
                stack.shrink(reallyFittable);
                slotStack.grow(reallyFittable);
                break;
            } else if (slotStack.isEmpty()) {
                ItemStack slotBehind = i == 0 ? ItemStack.EMPTY : getStackInSlot(i - 1);
                if (i == 0 || (!slotBehind.isEmpty()
                        && (stack.getItem() != slotBehind.getItem()
                        || stack.getDamage() != slotBehind.getDamage()
                        || !ItemStack.areItemStackTagsEqual(stack, slotBehind)))) {
                    int maxFittable = MAX_STACK_SIZE;
                    int reallyFittable = Math.min(stack.getCount(), maxFittable);
                    ItemStack copySize = stack.copy();
                    copySize.setCount(reallyFittable);
                    stack.shrink(reallyFittable);
                    setInventorySlotContents(i, copySize);
                    break;
                }
            }
        }
        inventoryChanged();
        return stack;
    }

    public ItemStack removeItem() {
        ItemStack stack = ItemStack.EMPTY;

        for (int i = getSizeInventory() - 1; i >= 0; i--) {
            if (!getStackInSlot(i).isEmpty()) {
                stack = removeStackFromSlot(i);
                break;
            }
        }

        inventoryChanged();
        return stack;
    }

    public List<ItemEntity> getCaptureItems() {
        return getCollectionArea().toBoundingBoxList()
                .stream()
                .flatMap((alignedBB) -> getWorld().getEntitiesWithinAABB(ItemEntity.class,
                        alignedBB.offset(getXPos() - 0.5D, getYPos() - 0.5D, getZPos() - 0.5D),
                        EntityPredicates.IS_ALIVE).stream())
                .collect(Collectors.toList());
    }

    private boolean captureItem(ItemEntity entity) {
        ItemStack itemstack = entity.getItem().copy();
        ItemStack stack = addItem(itemstack);
        boolean removed = false;

        if (stack.isEmpty()) {
            entity.remove();
            removed = true;
        } else {
            entity.setItem(stack);
        }
        return removed;
    }

    public void onEntityCollision(Entity collidedEntity) {
        if (collidedEntity instanceof ItemEntity) {
            BlockPos blockpos = this.getPos();
            if (VoxelShapes.compare(VoxelShapes.create(collidedEntity.getBoundingBox().offset(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())), this.getCollectionArea(), IBooleanFunction.AND)) {
                this.updateHopper(() -> captureItem((ItemEntity) collidedEntity));
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////

    /**
     * Returns a NonNullList<ItemStack> of items currently held in the crafting plate.
     */
    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 13, this.getUpdateTag());
    }

    public void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer() {
        return this.transferCooldown > 8;
    }

    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.inventory = itemsIn;
    }

    private void inventoryChanged() {
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public int getSizeInventory() {
        return INV_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.getItems().get(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int i1) {
        ItemStack stack = ItemStackHelper.getAndSplit(this.getItems(), i, i1);
        if (!stack.isEmpty()) {
            this.markDirty();
        }

        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return ItemStackHelper.getAndRemove(this.getItems(), i);
    }

    @Override
    public int getInventoryStackLimit() {
        return MAX_STACK_SIZE;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        this.getItems().set(i, itemStack);
        if (itemStack.getCount() > this.getInventoryStackLimit()) {
            itemStack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerEntity) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return playerEntity.getDistanceSq((double) this.pos.getX() + 0.5D,
                    (double) this.pos.getY() + 0.5D,
                    (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        this.getItems().clear();
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    @Override
    public double getXPos() {
        return (double) this.pos.getX() + 0.5D;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    @Override
    public double getYPos() {
        return (double) this.pos.getY() + 0.5D;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    @Override
    public double getZPos() {
        return (double) this.pos.getZ() + 0.5D;
    }
}
