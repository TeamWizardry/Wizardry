package com.teamwizardry.wizardry.client.particle;

import com.teamwizardry.librarianlib.core.rendering.BlendMode;
import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding;
import com.teamwizardry.librarianlib.glitter.bindings.EaseBinding;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule;
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

public class Glitter extends ParticleSystem {

	@Override
	public void configure() {
		StoredBinding origin = bind(3);
		StoredBinding previousPos = bind(3);
		StoredBinding target = bind(3);
		StoredBinding initialColor = bind(4);
		StoredBinding goalColor = bind(4);
		StoredBinding initialSize = bind(1);
		StoredBinding goalSize = bind(1);
		StoredBinding gravity = bind(1);
		StoredBinding friction = bind(1);
		StoredBinding drag = bind(1);
		StoredBinding bounce = bind(1);

		getUpdateModules().add(new BasicPhysicsUpdateModule(origin,
				previousPos,
				target,
				true,
				gravity,
				bounce,
				friction,
				drag));

		getRenderModules().add(new SpriteRenderModule(SpriteRenderModule.simpleRenderType(
				new ResourceLocation(Wizardry.MODID, "textures/particles/sparkle.png"), BlendMode.getADDITIVE()),
				origin,
				previousPos,
				new ConstantBinding(0, 1, 0, 1),
				new EaseBinding(getLifetime(), getAge(), null, null, Easing.linear, 1, initialSize, goalSize)
		));
	}

	public void spawn(GlitterBox box) {
		addParticle(box.lifetime,
				box.origin.x, box.origin.y, box.origin.z,
				box.origin.x, box.origin.y, box.origin.z,
				box.target.x, box.target.y, box.target.z,
				box.initialColor.getRed() / 255.0, box.initialColor.getGreen() / 255.0, box.initialColor.getBlue() / 255.0, 1,
				box.goalColor.getRed() / 255.0, box.goalColor.getGreen() / 255.0, box.goalColor.getBlue() / 255.0, 1,
				box.initialSize, box.goalSize,
				box.gravity,
				box.friction,
				box.drag,
				box.bounce
		);
	}
}

