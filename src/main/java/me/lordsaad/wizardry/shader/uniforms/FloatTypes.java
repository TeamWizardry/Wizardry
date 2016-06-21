package me.lordsaad.wizardry.shader.uniforms;

import org.lwjgl.opengl.ARBShaderObjects;

import me.lordsaad.wizardry.shader.Shader;

public class FloatTypes {
	
	public static class Float extends Uniform {
		 
		public Float(Shader owner, String name, UniformType type,
				int size, int location) {
			super(owner, name, type, size, location);
		}
 
		public void set(float value) {
			ARBShaderObjects.glUniform1fARB(getLocation(), value);
		}
		
		public void set(double value) { set((float)value); }
	}
 
	public static class FloatVec2 extends Uniform {
 
		public FloatVec2(Shader owner, String name, UniformType type,
				int size, int location) {
			super(owner, name, type, size, location);
		}
 
		public void set(float x, float y) {
			ARBShaderObjects.glUniform2fARB(getLocation(), x, y);
		}
		
		public void set(double x, double y) { set((float)x, (float)y); }
	}
 
	public static class FloatVec3 extends Uniform {
 
		public FloatVec3(Shader owner, String name, UniformType type,
				int size, int location) {
			super(owner, name, type, size, location);
		}
 
		public void set(float x, float y, float z) {
			ARBShaderObjects.glUniform3fARB(getLocation(), x, y, z);
		}
		
		public void set(double x, double y, double z) { set((float)x, (float)y, (float)z); }
	}
 
	public static class FloatVec4 extends Uniform {
 
		public FloatVec4(Shader owner, String name, UniformType type,
				int size, int location) {
			super(owner, name, type, size, location);
		}
 
		public void set(float x, float y, float z, float w) {
			ARBShaderObjects.glUniform4fARB(getLocation(), x, y, z, w);
		}
		
		public void set(double x, double y, double z, double w) { set((float)x, (float)y, (float)z, (float)w); }
	}
}
