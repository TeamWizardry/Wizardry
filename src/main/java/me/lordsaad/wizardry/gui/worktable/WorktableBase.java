package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.spells.SpellIngredients;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableBase extends GuiScreen {

    public static int left, top, right;
    public static int backgroundWidth = 214, backgroundHeight = 220; // SIZE OF PAPER
    public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");
    public static HashMap<SpellIngredients.IngredientType, ArrayList<Module>> modules;

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;
        modules = new HashMap<>();
        initModules();
    }

    private void initModules() {
        for (Class clazz : Modules.class.getDeclaredClasses()) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    Module module = (Module) field.get(clazz);
                    modules.putIfAbsent(module.getType(), new ArrayList<>());
                    ArrayList<Module> category = modules.get(module.getType());
                    category.add(module);
                    modules.put(module.getType(), category);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // RENDER BACKGROUND //
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(left, top, 0, 0, backgroundWidth, backgroundHeight);
        // RENDER BACKGROUND //

        int row = 0, column = 0, iconSize = 17, iconSeparation = 1;
        for (Module module : modules.get(SpellIngredients.IngredientType.EFFECT)) {
            int x = left + iconSeparation + (row * iconSize) + (row * iconSeparation);
            int y = top + iconSeparation + (column * iconSize) + (column * iconSeparation);

            module.setX(x);
            module.setY(y);
            mc.renderEngine.bindTexture(module.getIcon());
            drawTexturedModalRect(x, y, 0, 0, 16, 16);

            if (row >= 10) {
                row = 0;
                column++;
            } else row++;
        }

        // Utils.drawLine2D(left, top, right, top + 100, 10, 30, Color.BLACK.getRGB());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}
