package com.teamwizardry.wizardry.client.particle.shader;

public abstract class ShaderCallback<T extends Shader> {

    public abstract void call(T shader);

}
