package com.teamwizardry.wizardry.client.fx;

import com.teamwizardry.librarianlib.client.fx.shader.Shader;
import com.teamwizardry.librarianlib.client.fx.shader.ShaderHelper;
import com.teamwizardry.librarianlib.client.fx.shader.uniforms.FloatTypes;
import com.teamwizardry.librarianlib.client.fx.shader.uniforms.IntTypes;

public class Shaders {
	public static final Shaders INSTANCE = new Shaders();
	
	public static BurstShader burst;
	
	private Shaders() {
		burst = ShaderHelper.addShader(new BurstShader(null, "/assets/wizardry/shader/burstNew.frag"));
	}
	
	public static class BurstShader extends Shader {

	    public IntTypes.Int count;
	    public FloatTypes.Float rotationSpeed;

	    public FloatTypes.FloatVec4 glowColor;
	    public FloatTypes.FloatVec4 centerColor;

	    public IntTypes.Int rayFade;
	    public FloatTypes.Float glowFade;

	    public FloatTypes.Float lengthRandomness;
	    public FloatTypes.Float centerRadius;


	    public BurstShader(String vert, String frag) {
	        super(vert, frag);
	    }
	    
	    @Override
	    public void initUniforms() {
	        count = getUniform("COUNT");
	        rotationSpeed = getUniform("rotationMultiplier");

	        glowColor = getUniform("glowColor");
	        centerColor = getUniform("centerColor");

	        rayFade = getUniform("rayFade");
	        glowFade = getUniform("glowFade");

	        lengthRandomness = getUniform("lengthRandomness");
	        centerRadius = getUniform("centerRadius");
	    }
	}
	
}
