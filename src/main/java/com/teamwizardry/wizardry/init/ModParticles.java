package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.client.fx.ParticleSmoke;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModParticles {

	public static final ParticleSmoke PARTICLE_SMOKE = new ParticleSmoke();
}
