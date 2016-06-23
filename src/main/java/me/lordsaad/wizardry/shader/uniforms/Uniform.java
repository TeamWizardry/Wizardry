package me.lordsaad.wizardry.shader.uniforms;

import me.lordsaad.wizardry.Logs;
import me.lordsaad.wizardry.shader.Shader;

public abstract class Uniform {

    public static final NoUniform NONE = new NoUniform(Shader.NONE, "NONE", UniformType.NONE, 0, 0);

    private final Shader owner;
    private final String name;
    private final UniformType type;
    private final int size;
    private final int location;

    public Uniform(Shader owner, String name, UniformType type, int size, int location) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.size = size;
        this.location = location;
    }

    public Shader getProgram() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public UniformType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getLocation() {
        return location;
    }

    public static class NoUniform extends Uniform {
        public NoUniform(Shader owner, String name, UniformType type,
                         int size, int location) {
            super(owner, name, type, size, location);

            Logs.warn("[Shader %s] Uniform %s has unsupported type %s", owner == null ? "!!NULL!!" : owner.getClass().getName(), name, type.name());
        }
    }

}
