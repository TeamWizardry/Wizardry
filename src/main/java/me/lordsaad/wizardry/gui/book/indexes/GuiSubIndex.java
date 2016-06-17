package me.lordsaad.wizardry.gui.book.indexes;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.gui.book.Tip;
import me.lordsaad.wizardry.gui.book.Tippable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Saad on 5/2/2016.
 */

/**
 * Extend this class to make a sub index page.
 */
public class GuiSubIndex extends Tippable {

    public static int indexID;
    int pageNb = 1;
    private HashMap<SubIndexElement, Integer> elements;
    private int i = 0, assignedPageNb = 1;

    @Override
    public void initGui() {
        super.initGui();
        elements = new HashMap<>();
        setHasNavReturn(true);
    }

    public void addElement(SubIndexElement element) {
        if (i > 5) {
            i = 0;
            assignedPageNb++;
        } else i++;
        elements.put(element, assignedPageNb);
    }

    public HashMap<SubIndexElement, Integer> getElements() {
        return elements;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case Constants.GuiButtons.NAV_BAR_BACK: {
                if (pageNb > 1) {
                    pageNb--;
                    mc.thePlayer.openGui(Wizardry.instance, pageNb, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                } else {
                    mc.thePlayer.openGui(Wizardry.instance, Constants.PageNumbers.INDEX, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                    pageNb = -1;
                }
                break;
            }

            case Constants.GuiButtons.NAV_BAR_NEXT: {
                if (elements.size() > pageNb) {
                    pageNb++;
                    mc.thePlayer.openGui(Wizardry.instance, pageNb, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                }
                break;
            }

            case Constants.GuiButtons.NAV_BAR_INDEX: {
                mc.thePlayer.openGui(Wizardry.instance, Constants.PageNumbers.INDEX, mc.theWorld, (int) mc.thePlayer.posX, (int)
                        mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                pageNb = 0;
                break;
            }

            default: {
                elements.keySet().stream().filter(element -> element.getButton() == button).forEach(element -> mc.thePlayer.openGui(Wizardry.instance, element.getPageID(), mc.theWorld, (int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ));
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

//        if (buttonList.size() > 5 && !isNavBarEnabled())
        
        setHasNavNext(elements.size()-pageNb > 5);
        setHasNavPrev(pageNb > 1);
        
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.setBidiFlag(true);

        for (SubIndexElement element : elements.keySet()) {

            // Split elements into 5 per page.
            if (elements.get(element) != pageNb) return;

            GlStateManager.color(1F, 1F, 1F, 1F);
            int x = left + 20, y = top + element.getIDInIndex() * 15;

            element.getButton().xPosition = x;
            element.getButton().yPosition = y + 2;
            element.getButton().width = bookBackgroundWidth;
            element.getButton().drawButton(mc, x, y);
            if (element.getTextureType() == SubIndexElement.TextureType.TEXTURE) {
                mc.renderEngine.bindTexture(element.getTextureIcon());
                drawScaledCustomSizeModalRect(x, y, 0, 0, 15, 15, 15, 15, 15, 15);
            } else Utils.drawNormalItemStack(new ItemStack(element.getItemIcon()), x, y);

            boolean inside = mouseX >= element.getButton().xPosition && mouseX < element.getButton().xPosition + element.getButton().width && mouseY >= element.getButton().yPosition && mouseY < element.getButton().yPosition + element.getButton().height;
            if (inside) {
                x += 3;
                fontRendererObj.drawString(" | " + ChatFormatting.ITALIC + element.getText().trim(), x + 17, y + fontRendererObj.FONT_HEIGHT / 2, 0);
                int tip = setTip(new Tip(element.getTip(), y - 10));
                if (tip != -1) tipManager.put(element.getIDInIndex(), tip);
            } else {
                if (tipManager.containsKey(element.getIDInIndex())) {
                    removeTip(tipManager.get(element.getIDInIndex()));
                    tipManager.remove(element.getIDInIndex());
                }
                fontRendererObj.drawString(" | " + element.getText().trim(), x + 17, y + fontRendererObj.FONT_HEIGHT / 2, 0);
            }
        }
    }
}
