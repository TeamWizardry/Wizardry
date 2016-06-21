package me.lordsaad.wizardry.shader.shaders;

import me.lordsaad.wizardry.shader.Shader;
import me.lordsaad.wizardry.shader.uniforms.FloatTypes;
import me.lordsaad.wizardry.shader.uniforms.FloatTypes.Float;
import me.lordsaad.wizardry.shader.uniforms.FloatTypes.FloatVec4;
import me.lordsaad.wizardry.shader.uniforms.IntTypes;
import me.lordsaad.wizardry.shader.uniforms.IntTypes.Int;

public class BurstShader extends Shader {

	public IntTypes.Int count;
	public FloatTypes.Float rotationSpeed;
	
	public FloatTypes.FloatVec4 glowColor;
	public FloatTypes.FloatVec4 centerColor;
	
	public IntTypes.Int rayFade;
	public FloatTypes.Float glowFade;
	
	public FloatTypes.Float lengthRandomness;
	public FloatTypes.Float centerRadius;

	
	public BurstShader(int program) {
		super(program);
		
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
