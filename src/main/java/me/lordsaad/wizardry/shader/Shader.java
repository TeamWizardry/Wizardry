package me.lordsaad.wizardry.shader;

import org.lwjgl.opengl.GL20;

import me.lordsaad.wizardry.Logs;
import me.lordsaad.wizardry.shader.uniforms.IntTypes;
import me.lordsaad.wizardry.shader.uniforms.Uniform;
import me.lordsaad.wizardry.shader.uniforms.UniformType;

public class Shader {
	public static final Shader NONE = new Shader(0);
	
	public final IntTypes.Int time;
	
	private final int glName;
	private final Uniform[] uniforms;
 
	public Shader(int program) {
		glName = program;
		
		int uniformCount = GL20.glGetProgrami(getGlName(), GL20.GL_ACTIVE_UNIFORMS);
		int uniformLength = GL20.glGetProgrami(getGlName(), GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
		uniforms = new Uniform[uniformCount];
		int index = 0;
		for (int i = 0; i < uniformCount; i++) {
			String name 		= GL20.glGetActiveUniform(getGlName(), i, uniformLength);
			int type 			= GL20.glGetActiveUniformType(getGlName(), i);
			int size 			= GL20.glGetActiveUniformSize(getGlName(), i);
			int location 		= GL20.glGetUniformLocation(getGlName(), name);
 
			Uniform uniform 	= makeUniform(name, type, size, location);
			uniforms[index++] 	= uniform;
		}
		
		time = (IntTypes.Int) getUniform("time");
	}
	
	public int getGlName() {
		return glName;
	}
 
	public <T extends Uniform> T getUniform(String name) {
		for (int i = 0; i < uniforms.length; i++) {
			if (uniforms[i].getName().equals(name)) {
				try {
					return (T) uniforms[i];
				} catch (ClassCastException e) {
					Logs.debug("Uniform %s was wrong type. (%s)", name, uniforms[i].getType().name());
				}
			}
		}
		Logs.debug("Can't find uniform %s", name);
		return null;
	}
 
	private Uniform makeUniform(String name, int type, int size, int location) {
		UniformType enumType = UniformType.getByGlEnum(type);
		return enumType.make(this, name, enumType, size, location);
	}
 
}
