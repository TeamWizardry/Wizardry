package com.teamwizardry.wizardry.client.render.glow;

import com.teamwizardry.librarianlib.fx.shader.ShaderHelper;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.client.fx.Shaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nonnull;

public class GlowingItemRenderLayer implements LayerRenderer<EntityLivingBase> {

    private RenderLivingBase<?> render;

    public GlowingItemRenderLayer(RenderLivingBase<?> render) {
        this.render = render;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean flag = entity.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack left = flag ? entity.getHeldItemOffhand() : entity.getHeldItemMainhand();
        ItemStack right = flag ? entity.getHeldItemMainhand() : entity.getHeldItemOffhand();

        if (left != null || right != null) {
            GlStateManager.pushMatrix();

            if (this.render.getMainModel().isChild) {
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            if (right != null && right.getItem() instanceof IGlowOverlayable) {
                IGlowOverlayable item = (IGlowOverlayable) right.getItem();
                if (item.useOverlay(right)) {
                    if (item.useShader(right))
                        ShaderHelper.Companion.useShader(Shaders.rawColor);
                    if (item.disableLighting(right))
                        GlStateManager.disableLighting();

                    this.renderHeldItem(entity, right, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);

                    if (item.useShader(right))
                        ShaderHelper.Companion.releaseShader();
                    if (item.disableLighting(right))
                        GlStateManager.enableLighting();
                }
            }

            if (left != null && left.getItem() instanceof IGlowOverlayable) {
                IGlowOverlayable item = (IGlowOverlayable) left.getItem();
                if (item.useOverlay(left)) {
                    if (item.useShader(left))
                        ShaderHelper.Companion.useShader(Shaders.rawColor);
                    if (item.disableLighting(left))
                        GlStateManager.disableLighting();

                    this.renderHeldItem(entity, left, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

                    if (item.useShader(left))
                        ShaderHelper.Companion.releaseShader();
                    if (item.disableLighting(left))
                        GlStateManager.enableLighting();
                }
            }

            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide) {
        if (stack != null) {
            GlStateManager.pushMatrix();
            if (entity.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            ((ModelBiped) this.render.getMainModel()).postRenderArm(0.0625F, handSide);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, GlowingOverlayHelper.overlayStack(stack), transform, flag);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
