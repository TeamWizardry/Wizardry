package me.lordsaad.wizardry.gui.book.indexes;

import me.lordsaad.wizardry.gui.book.util.Color;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 5/2/2016.
 */
public class SubIndexElement {

    private int linkPage;
    private ResourceLocation textureIcon;
    private Item itemIcon;
    private int itemDamage;
    private String text, link, tip;
    private TextureType textureType;
    private Color color;

    public SubIndexElement(String text, String link, int linkPage, String tip, int color, ResourceLocation tex) {
        this.text = text;
        this.tip = tip;
        this.link = link;
        this.linkPage = linkPage;
        this.textureIcon = tex;
        this.textureType = TextureType.TEXTURE;
        if ((color & 0xff000000) == 0) {
            color = color | 0xff000000; // if there is no alpha specified it should be opaque
        }
        this.color = Color.argb(color);
    }

    public SubIndexElement(String text, String link, int linkPage, String tip, int color, Item item, int damage) {
        this.text = text;
        this.tip = tip;
        this.link = link;
        this.linkPage = linkPage;
        this.itemIcon = item;
        this.itemDamage = damage;
        this.textureType = TextureType.ITEM;
        if ((color & 0xff000000) == 0) {
            color = color | 0xff000000; // if there is no alpha specified it should be opaque
        }
        this.color = Color.argb(color);
    }

    public void iconGlColor() {
        color.glColor();
    }

    public TextureType getTextureType() {
        return textureType;
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

    public int getItemDamage() {
        return itemDamage;
    }

    public void setItemDamage(int itemDamage) {
        this.itemDamage = itemDamage;
    }

    public int getLinkPage() {
        return linkPage;
    }

    public void setLinkPage(int linkPage) {
        this.linkPage = linkPage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public enum TextureType {
        TEXTURE, ITEM
    }
}
