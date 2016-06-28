package com.teamwizardry.wizardry.client.particle.shader.uniforms;

import com.teamwizardry.wizardry.client.particle.shader.Shader;
import org.lwjgl.opengl.ARBShaderObjects;

public class BoolTypes {

    public static class Bool extends Uniform {

        public Bool(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(boolean value) {
            ARBShaderObjects.glUniform1iARB(getLocation(), value ? 1 : 0);
        }
    }

    public static class BoolVec2 extends Uniform {

        public BoolVec2(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(boolean x, boolean y) {
            ARBShaderObjects.glUniform2iARB(getLocation(), x ? 1 : 0, y ? 1 : 0);
        }
    }

    public static class BoolVec3 extends Uniform {

        public BoolVec3(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(boolean x, boolean y, boolean z) {
            ARBShaderObjects.glUniform3iARB(getLocation(), x ? 1 : 0, y ? 1 : 0, z ? 1 : 0);
        }
    }

    public static class BoolVec4 extends Uniform {

        public BoolVec4(Shader owner, String name, UniformType type, int size, int location) {
            super(owner, name, type, size, location);
        }

        public void set(boolean x, boolean y, boolean z, boolean w) {
            ARBShaderObjects.glUniform4iARB(getLocation(), x ? 1 : 0, y ? 1 : 0, z ? 1 : 0, w ? 1 : 0);
        }
    }

}
