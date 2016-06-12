package me.lordsaad.wizardry.book.indexes;

import me.lordsaad.wizardry.book.Button;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 5/2/2016.
 */
public class SubIndexItem {

    private int indexID, pageID;
    private ResourceLocation textureIcon;
    private Item itemIcon;
    private Block blockIcon;
    private String text, tip;
    private Button button;
    private TextureType textureType;

    public SubIndexItem(int indexID, int pageID, ResourceLocation icon, String text, String tip, Button button) {
        this.indexID = indexID;
        this.pageID = pageID;
        this.textureIcon = icon;
        this.text = text;
        this.button = button;
        this.tip = tip;
        textureType = TextureType.TEXTURE;
    }

    public SubIndexItem(int indexID, int pageID, Item icon, String text, String tip, Button button) {
        this.indexID = indexID;
        this.pageID = pageID;
        this.itemIcon = icon;
        this.text = text;
        this.button = button;
        this.tip = tip;
        textureType = TextureType.ITEM;
    }

    public SubIndexItem(int indexID, int pageID, Block icon, String text, String tip, Button button) {
        this.indexID = indexID;
        this.pageID = pageID;
        this.blockIcon = icon;
        this.text = text;
        this.button = button;
        this.tip = tip;
        textureType = TextureType.BLOCK;
    }

    public TextureType getTextureType() {
        return textureType;
    }

    public int getIndexID() {
        return indexID;
    }

    public void setIndexID(int indexID) {
        this.indexID = indexID;
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public ResourceLocation getTextureIcon() {
        return textureIcon;
    }

    public void setTextureIcon(ResourceLocation textureIcon) {
        this.textureIcon = textureIcon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Item getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(Item itemIcon) {
        this.itemIcon = itemIcon;
    }

    public Block getBlockIcon() {
        return blockIcon;
    }

    public void setBlockIcon(Block blockIcon) {
        this.blockIcon = blockIcon;
    }

    public enum TextureType {
        TEXTURE, ITEM, BLOCK
    }
}
