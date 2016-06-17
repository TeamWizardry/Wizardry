package me.lordsaad.wizardry.gui.book.indexes;

import me.lordsaad.wizardry.gui.book.Button;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 5/2/2016.
 */
public class SubIndexElement {

    private int IDInIndex, pageID;
    private ResourceLocation textureIcon;
    private Item itemIcon;
    private String text, tip;
    private Button button;
    private TextureType textureType;

    public SubIndexElement(int IDInIndex, int pageID, ResourceLocation icon, String text, String tip, Button button) {
        this.textureIcon = icon;
        this.pageID = pageID;
        this.IDInIndex = IDInIndex;
        this.text = text;
        this.button = button;
        this.tip = tip;
        textureType = TextureType.TEXTURE;
    }

    public SubIndexElement(int IDInIndex, int pageID, Item icon, String text, String tip, Button button) {
        this.itemIcon = icon;
        this.pageID = pageID;
        this.IDInIndex = IDInIndex;
        this.text = text;
        this.button = button;
        this.tip = tip;
        textureType = TextureType.ITEM;
    }

    public SubIndexElement(int IDInIndex, int pageID, Block icon, String text, String tip, Button button) {
        this.itemIcon = Item.getItemFromBlock(icon);
        this.pageID = pageID;
        this.IDInIndex = IDInIndex;
        this.text = text;
        this.button = button;
        this.tip = tip;
        textureType = TextureType.ITEM;
    }

    public TextureType getTextureType() {
        return textureType;
    }

    public int getIDInIndex() {
        return IDInIndex;
    }

    public void setIDInIndex(int IDInIndex) {
        this.IDInIndex = IDInIndex;
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

    enum TextureType {
        TEXTURE, ITEM
    }
}
