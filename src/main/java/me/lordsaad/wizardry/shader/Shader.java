package me.lordsaad.wizardry.shader;

import me.lordsaad.wizardry.Logs;
import me.lordsaad.wizardry.shader.uniforms.FloatTypes;
import me.lordsaad.wizardry.shader.uniforms.Uniform;
import me.lordsaad.wizardry.shader.uniforms.UniformType;
import org.lwjgl.opengl.GL20;

public abstract class Shader {
	public static final Shader NONE = new Shader("","") {
		@Override
		public void initUniforms() {}
	};
	
	public FloatTypes.Float time;
	
	private int glName = 0;
	private Uniform[] uniforms;
 
	private String vert, frag;
	
	public Shader(String vert, String frag) {
		this.vert = vert;
		this.frag = frag;
	}
	
	public void init(int program) {
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
		
		time = getUniform("time");
		
		initUniforms();
	}
	
	public abstract void initUniforms();
	
	public String getVert() {
		return vert;
	}

	public String getFrag() {
		return frag;
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
