package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntityBackupZombie;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderSummonZombie extends RenderBiped<EntityBackupZombie> {
	private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");

	public RenderSummonZombie(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSummonZombie(), 0.5F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			@Override
			protected void initArmor() {
				this.modelLeggings = new ModelZombie(0.5F, true);
				this.modelArmor = new ModelZombie(1.0F, true);
			}
		};
		this.addLayer(layerbipedarmor);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(@NotNull EntityBackupZombie entity) {
		return ZOMBIE_TEXTURES;
	}
}
