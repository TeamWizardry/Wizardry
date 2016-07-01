package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.client.multiblock.InWorldRender;
import com.teamwizardry.librarianlib.client.multiblock.StructureMatchResult;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IInfusible;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.common.Structures;
import com.teamwizardry.wizardry.common.spell.parsing.Parser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileEntity implements ITickable {

    private ArrayList<ItemStack> inventory = new ArrayList<>();
    private boolean structureComplete = false, isCrafting = false;
    private int craftingTime = 100, craftingTimeLeft = 100;
    private ItemStack pearl;
    private IBlockState state;

    public TileCraftingPlate() {
    }

    public void validateStructure() {
        Structures.reload();
        StructureMatchResult match = Structures.craftingAltar.match(this.worldObj, this.pos);

        if (match.allErrors.size() == 0) {
            worldObj.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
            InWorldRender.INSTANCE.unsetStructure();
            setStructureComplete(true);
        } else {
            InWorldRender.INSTANCE.setStructure(Structures.craftingAltar, this.pos);
            setStructureComplete(false);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        structureComplete = compound.getBoolean("structureComplete");
        inventory = new ArrayList<>();
        if (compound.hasKey("inventory")) {
            NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
                inventory.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
        }
        if (compound.hasKey("pearl"))
            pearl = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("pearl"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean("structureComplete", structureComplete);

        if (inventory.size() > 0) {
            NBTTagList list = new NBTTagList();
            for (ItemStack anInventory : inventory)
                list.appendTag(anInventory.writeToNBT(new NBTTagCompound()));
            compound.setTag("inventory", list);
        }
        if (pearl != null) compound.setTag("pearl", pearl.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());

        state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
    }

    public ArrayList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public void update() {
        if (isStructureComplete()) {
            boolean update = false;
            List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 2, 1)));
            for (EntityItem item : items) {

                if (item.getEntityItem().getItem() instanceof IInfusible) {
                    IInfusible pearl = (IInfusible) item.getEntityItem().getItem();
                    if (pearl.getType(item.getEntityItem()) == PearlType.MUNDANE) {
                        this.pearl = item.getEntityItem();
                        isCrafting = true;
                        craftingTime = inventory.size()/* * 100*/;
                        craftingTimeLeft = inventory.size()/* * 100*/;
                    }
                } else inventory.add(item.getEntityItem());

                update = true;
                worldObj.removeEntity(item);
            }

            if (update) worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

            SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 100, 8, 8, 8, true);
                ambient.jitter(8, 0.1, 0.1, 0.1);
                ambient.randomDirection(0.2, 0.2, 0.2);

            if (isCrafting) {
                if (craftingTimeLeft > 0) --craftingTimeLeft;
                else {
                    Parser spellParser = new Parser(inventory);
                    Module parsedSpell = null;
                    try {
                        while (parsedSpell == null)
                            parsedSpell = spellParser.parse();
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                    }
                    if (parsedSpell != null) {
                        NBTTagCompound compound = pearl.getTagCompound();
                        compound.setTag("Spell", parsedSpell.getModuleData());
                        pearl.setTagCompound(compound);
                    }
                    inventory.clear();
                    isCrafting = false;
                }
            }
        }
    }

    public boolean isStructureComplete() {
        return structureComplete;
    }

    public void setStructureComplete(boolean structureComplete) {
        this.structureComplete = structureComplete;
    }

    public ItemStack getPearl() {
        return pearl;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public boolean isCrafting() {
        return isCrafting;
    }
}