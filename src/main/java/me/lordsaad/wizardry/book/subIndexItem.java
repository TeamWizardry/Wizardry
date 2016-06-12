package me.lordsaad.wizardry.book;

import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 5/2/2016.
 */
public class SubIndexItem {

    private int indexID, pageID;
    private ResourceLocation icon;
    private String text, tip;
    private Button button;

    public SubIndexItem(int indexID, int pageID, ResourceLocation icon, String text, String tip, Button button) {
        this.indexID = indexID;
        this.pageID = pageID;
        this.icon = icon;
        this.text = text;
        this.button = button;
        this.tip = tip;
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

    public ResourceLocation getIcon() {
        return icon;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
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
}
