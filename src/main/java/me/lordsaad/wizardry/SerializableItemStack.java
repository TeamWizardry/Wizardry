package me.lordsaad.wizardry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * ItemStack.writeToNBT && readFromNBT don't support custom keys. So I made my own.
 */
public class SerializableItemStack {

    private int stackSize;
    private Item item;
    private NBTTagCompound stackTagCompound;
    private int itemDamage;

    public SerializableItemStack(ItemStack stack) {
        this.stackSize = stack.stackSize;
        this.item = stack.getItem();
        this.stackTagCompound = stack.getTagCompound();
        this.itemDamage = stack.getItemDamage();
    }

    public NBTTagCompound writeToNBT(String key, NBTTagCompound nbt) {
        ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(this.item);
        nbt.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        nbt.setByte("Count", (byte) this.stackSize);
        nbt.setShort("Damage", (short) this.itemDamage);

        if (this.stackTagCompound != null) nbt.setTag(key, this.stackTagCompound);

        return nbt;
    }

    public void readFromNBT(String key, NBTTagCompound nbt) {
        this.item = Item.getByNameOrId(nbt.getString("id"));
        this.stackSize = nbt.getByte("Count");
        this.itemDamage = nbt.getShort("Damage");

        if (this.itemDamage < 0) this.itemDamage = 0;

        if (nbt.hasKey(key, 10)) {
            this.stackTagCompound = nbt.getCompoundTag(key);
            if (this.item != null) this.item.updateItemStackNBT(this.stackTagCompound);
        }
    }
}
