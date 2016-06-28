package com.teamwizardry.wizardry.client.particle.shader.uniforms;

import com.teamwizardry.wizardry.client.particle.shader.Shader;
import org.lwjgl.opengl.ARBShaderObjects;

public class IntTypes {

    public static class Int extends Uniform {

        public Int(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(int value) {
            ARBShaderObjects.glUniform1iARB(getLocation(), value);
        }
    }

    public static class IntVec2 extends Uniform {

        public IntVec2(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(int x, int y) {
            ARBShaderObjects.glUniform2iARB(getLocation(), x, y);
        }
    }

    public static class IntVec3 extends Uniform {

        public IntVec3(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(int x, int y, int z) {
            ARBShaderObjects.glUniform3iARB(getLocation(), x, y, z);
        }
    }

    public static class IntVec4 extends Uniform {

        public IntVec4(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(int x, int y, int z, int w) {
            ARBShaderObjects.glUniform4iARB(getLocation(), x, y, z, w);
        }
    }

}
