package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.client.multiblock.InWorldRender;
import com.teamwizardry.librarianlib.client.multiblock.StructureMatchResult;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.api.item.IInfusible;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.helper.CraftingPlateItemStackHelper;
import com.teamwizardry.wizardry.client.render.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.Structures;
import com.teamwizardry.wizardry.common.spell.parsing.Parser;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileEntity implements ITickable {

    private ArrayList<CraftingPlateItemStackHelper> inventory = new ArrayList<>();
    private boolean structureComplete = false, isCrafting = false, animationComplete = false;
    private int craftingTime = 100, craftingTimeLeft = 100;
    private int pearlAnimationTime = 500, pearlAnimationTimeLeft = 500;
    private CraftingPlateItemStackHelper pearl;
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

    // TODO save inventory's CraftingPlateItemStackHelper as the full object
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        structureComplete = compound.getBoolean("structureComplete");
        inventory = new ArrayList<>();
        if (compound.hasKey("inventory")) {
            NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
                inventory.add(new CraftingPlateItemStackHelper(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i))));
        }
        if (compound.hasKey("pearl")) {
            pearl = new CraftingPlateItemStackHelper(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("pearl")));
            pearl.setPoint(new Vec3d(0.5, 1, 0.5));
        }
    }

    // TODO save inventory's CraftingPlateItemStackHelper as the full object
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean("structureComplete", structureComplete);

        if (inventory.size() > 0) {
            NBTTagList list = new NBTTagList();
            for (CraftingPlateItemStackHelper anInventory : inventory)
                list.appendTag(anInventory.getItemStack().writeToNBT(new NBTTagCompound()));
            compound.setTag("inventory", list);
        }
        if (pearl != null) compound.setTag("pearl", pearl.getItemStack().writeToNBT(new NBTTagCompound()));
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

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public ArrayList<CraftingPlateItemStackHelper> getInventory() {
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
                        this.pearl = new CraftingPlateItemStackHelper(item.getEntityItem());
                        this.pearl.setPoint(new Vec3d(0.5, 1, 0.5));
                        isCrafting = true;
                        craftingTime = inventory.size() * 10;
                        craftingTimeLeft = inventory.size() * 10;
                    }
                } else inventory.add(new CraftingPlateItemStackHelper(item.getEntityItem()));

                update = true;
                worldObj.removeEntity(item);
            }

            if (update) worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

            SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 100, 8, 8, 8, true);
            ambient.jitter(8, 0.1, 0.1, 0.1);
            ambient.randomDirection(0.2, 0.2, 0.2);

            if (isCrafting)
                if (craftingTimeLeft > 0) --craftingTimeLeft;
                else isCrafting = false;
            else if (!animationComplete && craftingTimeLeft == 0 && pearlAnimationTimeLeft > 0) --pearlAnimationTimeLeft;
            if (!animationComplete && craftingTimeLeft == 0 && pearlAnimationTimeLeft == 0) animationComplete = true;

            if (animationComplete) {
                Parser spellParser = new Parser(inventory.stream().map(CraftingPlateItemStackHelper::getItemStack).collect(Collectors.toCollection(ArrayList::new)));
                Module parsedSpell = null;
                try {
                    while (parsedSpell == null)
                        parsedSpell = spellParser.parse();
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                }
                if (parsedSpell != null) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(".");
                    NBTTagCompound compound = pearl.getItemStack().getTagCompound();
                    compound.setString("type", PearlType.INFUSED.toString());
                    compound.setTag("Spell", parsedSpell.getModuleData());
                    pearl.getItemStack().setTagCompound(compound);
                    EntityItem pearlItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + pearl.getPoint().yCoord, pos.getZ() + 0.5, pearl.getItemStack());
                    pearlItem.motionX = 0;
                    pearlItem.motionY = 0;
                    pearlItem.motionZ = 0;
                    worldObj.spawnEntityInWorld(pearlItem);

                    for (int i = 0; i < 10 * Config.particlePercentage / 100; i++) {
                        SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + pearl.getPoint().yCoord, pos.getZ() + 0.5, 0.5F, 0.5F, 50, false);
                        fizz.jitter(20, 0.01, 0, 0.01);
                        fizz.randomDirection(0.05, 0, 0.05);
                        fizz.setMotion(0, ThreadLocalRandom.current().nextDouble(-0.2, -0.05), 0);
                    }

                    pearl = null;
                }
                inventory.clear();
                animationComplete = false;
            }
        }
    }

    public boolean isStructureComplete() {
        return structureComplete;
    }

    public void setStructureComplete(boolean structureComplete) {
        this.structureComplete = structureComplete;
    }

    public CraftingPlateItemStackHelper getPearl() {
        return pearl;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public int getCraftingTimeLeft() {
        return craftingTimeLeft;
    }

    public boolean isCrafting() {
        return isCrafting;
    }

    public int getPearlAnimationTime() {
        return pearlAnimationTime;
    }

    public void setPearlAnimationTime(int pearlAnimationTime) {
        this.pearlAnimationTime = pearlAnimationTime;
    }

    public int getPearlAnimationTimeLeft() {
        return pearlAnimationTimeLeft;
    }

    public void setPearlAnimationTimeLeft(int pearlAnimationTimeLeft) {
        this.pearlAnimationTimeLeft = pearlAnimationTimeLeft;
    }
}