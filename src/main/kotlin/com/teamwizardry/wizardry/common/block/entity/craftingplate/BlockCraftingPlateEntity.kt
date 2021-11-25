package com.teamwizardry.wizardry.common.block.entity.craftingplate

import com.teamwizardry.wizardry.common.init.ModBlocks
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.Hopper
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.math.min

open class BlockCraftingPlateEntity(pos: BlockPos?, state: BlockState?) : BlockEntity(ModBlocks.craftingPlateEntity, pos, state), Hopper {
    var transferCooldown = -1
        protected set
    var inventory: DefaultedList<ItemStack>? = null
        protected set

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        inventory = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY)
        Inventories.readNbt(nbt, inventory)
        transferCooldown = nbt.getInt("TransferCooldown")
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
        nbt.putInt("TransferCooldown", transferCooldown)
        return nbt
    }

    private fun updateHopper(update: Supplier<Boolean>): Boolean {
        if (this.world == null || this.world!!.isClient) return false
        if (isOnTransferCooldown()) return false
        if (update.get()) {
            transferCooldown = 8
            this.markDirty()
            return true
        }
        return false
    }

    /**
     * Go backwards from last slot, if item matches, stack it.
     * This ensures modifiers clump together, but don't screw up order of the same item in other parts of
     * a spell.
     *
     * @param stack The ItemStack to insert into the recipe line
     * @return The stack left
     */
    fun addItem(stack: ItemStack): ItemStack {
        for (i in INV_SIZE - 1 downTo 0) {
            val slotStack = getStack(i)
            if (canCombine(slotStack, stack) && slotStack.count < BlockCraftingPlateEntity.maxCountPerStack) {
                val fittable = min(stack.count, BlockCraftingPlateEntity.maxCountPerStack - slotStack.count)
                stack.decrement(fittable)
                slotStack.increment(fittable)
                inventoryChanged()
                break
            } else if (slotStack.isEmpty) {
                val slotBehind = if (i == 0) ItemStack.EMPTY else getStack(i - 1)
                if (i == 0 || !canCombine(slotBehind, stack)) {
                    val fittable = min(stack.count, BlockCraftingPlateEntity.maxCountPerStack)
                    val copySize = stack.copy()
                    copySize.count = fittable
                    stack.decrement(fittable)
                    setStack(i, copySize)
                    inventoryChanged()
                    break
                }
            }
        }
        return stack
    }

    fun removeItem(): ItemStack {
        var stack = ItemStack.EMPTY
        for (i in INV_SIZE - 1 downTo 0) {
            if (getStack(i).isEmpty) continue
            stack = removeStack(i)
            inventoryChanged()
            break
        }
        return stack
    }

    private fun getCaptureItems(): List<ItemEntity> {
        return inputAreaShape.boundingBoxes
                .stream()
                .flatMap {alignedBB: Box ->
                    this.world?.getEntitiesByClass(
                            ItemEntity::class.java,
                            alignedBB.offset(hopperX - 0.5, hopperY - 0.5, hopperZ - 0.5),
                            EntityPredicates.VALID_ENTITY
                    )?.stream() ?: Stream.empty()
                }
                .collect(Collectors.toList())
    }

    private fun captureItem(entity: ItemEntity): Boolean {
        val stack = addItem(entity.stack.copy())
        if (stack.isEmpty) {
            entity.remove(Entity.RemovalReason.DISCARDED)
            return true
        }
        entity.stack = stack
        return false
    }

    fun onEntityCollision(collidedEntity: Entity) {
        if (collidedEntity !is ItemEntity) return
        val pos: BlockPos = this.getPos()
        val shape: VoxelShape = VoxelShapes.cuboid(
            collidedEntity.boundingBox.offset(
                -pos.x.toDouble(),
                -pos.y.toDouble(),
                -pos.z.toDouble()
            )
        )
        if (VoxelShapes.matchesAnywhere(
                        shape,
                        this.inputAreaShape
                ) {a: Boolean, b: Boolean -> a && b}
        ) updateHopper { captureItem(collidedEntity) }
    }
    //////////////////////////////////////////////////////////////////////////

    fun isOnTransferCooldown(): Boolean { return transferCooldown > 0 }

    fun mayTransfer(): Boolean { return transferCooldown > 8 }

    private fun inventoryChanged() {
        this.markDirty()
        this.world?.updateListeners(this.getPos(), this.cachedState, this.cachedState, 3)
    }

    override fun size(): Int {
        return INV_SIZE
    }

    override fun isEmpty(): Boolean {
        return inventory?.stream()?.allMatch {obj: ItemStack -> obj.isEmpty} ?: true
    }

    override fun getStack(index: Int): ItemStack {
        return inventory?.get(index) ?: ItemStack.EMPTY
    }

    override fun removeStack(index: Int, amount: Int): ItemStack {
        val stack: ItemStack = Inventories.splitStack(inventory, index, amount)
        if (!stack.isEmpty) this.markDirty()
        return stack
    }

    override fun removeStack(index: Int): ItemStack {
        return Inventories.removeStack(inventory, index)
    }

    override fun setStack(index: Int, stack: ItemStack) {
        inventory?.set(index, stack)
        if (stack.count > BlockCraftingPlateEntity.maxCountPerStack) stack.count = BlockCraftingPlateEntity.maxCountPerStack
        this.markDirty()
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return if (this.world?.getBlockEntity(this.getPos()) !== this) false else player.squaredDistanceTo(Vec3d.ofCenter(this.getPos())) <= 64
    }

    override fun clear() {
        inventory?.clear()
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    override fun getHopperX(): Double { return this.getPos().x + 0.5 }

    /**
     * Gets the world Y position for this hopper entity.
     */
    override fun getHopperY(): Double { return this.getPos().y.toDouble() + 0.5 }

    /**
     * Gets the world Z position for this hopper entity.
     */
    override fun getHopperZ(): Double { return this.getPos().z.toDouble() + 0.5 }

    companion object {
        private const val INV_SIZE = 256
        const val maxCountPerStack = 6

        private fun canCombine(left: ItemStack, right: ItemStack): Boolean {
            if (left.item !== right.item) return false else if (left.damage != right.damage) return false
            return ItemStack.areNbtEqual(left, right)
        }

        @Environment(EnvType.CLIENT)
        fun clientTick(world: World?, entity: BlockCraftingPlateEntity?) {
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
        fun serverTick(world: World?, entity: BlockCraftingPlateEntity) {
            if (world == null) return
            entity.transferCooldown--
            if (entity.isOnTransferCooldown()) return
            entity.transferCooldown = 0
            entity.updateHopper {
                for (itementity in entity.getCaptureItems()) {
                    if (entity.captureItem(itementity)) {
                        return@updateHopper true
                    }
                }
                false
            }
        }
    }
}