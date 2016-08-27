package com.teamwizardry.wizardry.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.common.structure.StructureMatchResult;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.helper.CraftingPlateItemStackHelper;
import com.teamwizardry.wizardry.common.Structures;
import com.teamwizardry.wizardry.common.spell.parsing.Parser;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileEntity implements ITickable {

    private ArrayList<CraftingPlateItemStackHelper> inventory = new ArrayList<>();
    private boolean structureComplete = false, isCrafting = false, isAnimating = false, animationComplete = false, burst = false;
    private int craftingTime = 100, craftingTimeLeft = 100;
    private int pearlAnimationTime = 500, pearlAnimationTimeLeft = 500;
    private CraftingPlateItemStackHelper pearl;
    private IBlockState state;

    public TileCraftingPlate() {
    }

    public void validateStructure() {
        Structures.reload();
		StructureMatchResult match = Structures.craftingAltar.match(this.worldObj, this.pos);

	    setStructureComplete(true);
        /*if (match.allErrors.size() == 0) {
            worldObj.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
            InWorldRender.INSTANCE.unsetStructure();
            setStructureComplete(true);
        } else {
            InWorldRender.INSTANCE.setStructure(Structures.craftingAltar, this.pos);
            setStructureComplete(false);
        }*/
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

                if (item.getEntityItem().getItem() instanceof Infusable) {
                    if (!inventory.isEmpty()) {
                        Infusable pearl = (Infusable) item.getEntityItem().getItem();
                        if (pearl.getType(item.getEntityItem()) == PearlType.MUNDANE) {
                            this.pearl = new CraftingPlateItemStackHelper(item.getEntityItem());
                            this.pearl.setPoint(new Vec3d(0.5, 1, 0.5));
                            isCrafting = true;
                            craftingTime = inventory.size() * 10;
                            craftingTimeLeft = inventory.size() * 10;
                        }
                    }
                } else inventory.add(new CraftingPlateItemStackHelper(item.getEntityItem()));

                update = true;
                worldObj.removeEntity(item);
            }
            if (update) worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

            SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldObj, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), new Vec3d(8, 8, 8));
            ambient.setAlpha(0.5f);
            ambient.setScale(0.5f);
            ambient.setMaxAge(100);
            ambient.setFadeOut();
            ambient.setShrink();
            ambient.setFadeIn();
            ambient.setJitter(8, 0.1, 0.1, 0.1);
            ambient.setRandomDirection(0.2, 0.2, 0.2);


            // > 1 to prevent java.lang.ArithmeticException: / by zero in TileCraftingPlateRenderer.class
            if (isCrafting)
                if (craftingTimeLeft > 1) --craftingTimeLeft;
                else {
                    isAnimating = true;
                    isCrafting = false;
                }
            else if (isAnimating)
                if (pearlAnimationTimeLeft > 1) --pearlAnimationTimeLeft;
                else {
                    isAnimating = false;
                    animationComplete = true;
                }

            if (isCrafting) {
                SparkleFX ambient2 = Wizardry.proxy.spawnParticleSparkle(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5));
                ambient2.setFadeIn();
                ambient2.setMaxAge(10);
                ambient2.setScale(0.5f);
                ambient2.setAlpha(0.1f);
                ambient2.setGrow();
                ambient2.setShrink();
                ambient2.setFadeOut();
                ambient2.setBlurred();
                for (int i = 0; i < 5; i++)
                    Wizardry.proxy.spawnParticleLensFlare(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), 50, 0.3);

            } else if (isAnimating) {
                for (int i = 0; i < 10; i++) {
                    SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, new Vec3d(pos.getX() + 0.5, pos.getX() + 1, pos.getZ() + 0.5));
                    fizz.setFadeIn();
                    fizz.setMaxAge(50);
                    fizz.setScale(0.5f);
                    fizz.setAlpha(0.1f);
                    fizz.setGrow();
                    fizz.setShrink();
                    fizz.setFadeOut();
                    fizz.setJitter(10, 0.01, 0, 0.01);
                    fizz.setRandomDirection(0.05, 0, 0.05);
                    fizz.setMotion(0, ThreadLocalRandom.current().nextDouble(0.05, 0.2), 0);
                }

            } else if (animationComplete) {
                for (int i = 0; i < 10; i++) {
                    SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, new Vec3d(pos.getX() + pearl.getPoint().xCoord + 0.5, pos.getX() + pearl.getPoint().yCoord, pos.getZ() + pearl.getPoint().zCoord + 0.5));
                    fizz.setFadeIn();
                    fizz.setMaxAge(10);
                    fizz.setScale(0.5f);
                    fizz.setAlpha(0.1f);
                    fizz.setGrow();
                    fizz.setShrink();
                    fizz.setFadeOut();
                    fizz.setRandomDirection(0.1, 0.1, 0.1);
                }
                structureComplete = false;
            }

            if (animationComplete) {
            	List<ItemStack> condensed = condenseItemList(inventory.stream().map(CraftingPlateItemStackHelper::getItemStack).collect(Collectors.toList()));
                Parser spellParser = new Parser(condensed);
                Module parsedSpell = null;
                if (!worldObj.isRemote)
                {
                	try {
                		while (parsedSpell == null)
                			parsedSpell = spellParser.parseInventoryToModule();
                	}
                	catch (NoSuchElementException ignored)
                	{}
                }

                if (parsedSpell != null) {
                    NBTTagCompound compound = pearl.getItemStack().getTagCompound();
                    compound.setString("type", PearlType.INFUSED.toString());
                    compound.setTag("Spell", parsedSpell.getModuleData());
                    pearl.getItemStack().setTagCompound(compound);
                    EntityItem pearlItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + pearl.getPoint().yCoord, pos.getZ() + 0.5, pearl.getItemStack());
                    pearlItem.setVelocity(0, 0.8, 0);
                    pearlItem.forceSpawn = true;
                    if(!worldObj.isRemote)
                        worldObj.spawnEntityInWorld(pearlItem);

                    for (int i = 0; i < 100 * Wizardry.proxy.getParticleDensity() / 100; i++) {
                        SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + pearl.getPoint().yCoord, pos.getZ() + 0.5));
                        fizz.setFadeIn();
                        fizz.setMaxAge(20);
                        fizz.setScale(0.5f);
                        fizz.setAlpha(0.1f);
                        fizz.setGrow();
                        fizz.setShrink();
                        fizz.setFadeOut();
                        fizz.setJitter(10, 0.005, 0.005, 0.005);
                        fizz.setRandomDirection(0.05, 0.005, 0.05);
                    }

                    pearl = null;
                    inventory = new ArrayList<>();
                    animationComplete = false;
                    craftingTimeLeft = craftingTime;
                    pearlAnimationTimeLeft = pearlAnimationTime;
                } else System.err.println("Something went wrong! @" + pos);
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

    public boolean isBurst() {
        return burst;
    }

    public void setBurst(boolean burst) {
        this.burst = burst;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }
    
    private List<ItemStack> condenseItemList(List<ItemStack> list)
    {
    	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    	items.add(list.remove(0));
    	while (list.size() > 0)
    	{
    		if (ModuleRegistry.areItemsEqual(list.get(0), items.get(items.size() - 1)))
    			items.get(items.size() - 1).stackSize += list.remove(0).stackSize;
    		else
    			items.add(list.remove(0));
    	}
    	return items;
    }
}