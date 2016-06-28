package com.teamwizardry.wizardry.client.particle.shader.uniforms;

import com.teamwizardry.wizardry.api.util.misc.Logs;
import com.teamwizardry.wizardry.client.particle.shader.Shader;
import com.teamwizardry.wizardry.client.particle.shader.uniforms.Uniform.NoUniform;
import org.lwjgl.opengl.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public enum UniformType {
    NONE(NoUniform::new),
    // bools
    BOOL(BoolTypes.Bool::new),
    BOOL_VEC2(BoolTypes.BoolVec2::new),
    BOOL_VEC3(BoolTypes.BoolVec3::new),
    BOOL_VEC4(BoolTypes.BoolVec4::new),

    // ints
    INT(IntTypes.Int::new),
    INT_VEC2(IntTypes.IntVec2::new),
    INT_VEC3(IntTypes.IntVec3::new),
    INT_VEC4(IntTypes.IntVec4::new),

    // unsigned ints
    UINT(NoUniform::new),
    UINT_VEC2(NoUniform::new),
    UINT_VEC3(NoUniform::new),
    UINT_VEC4(NoUniform::new),
    UINT_ATOMIC_COUNTER(NoUniform::new),

    // floats
    FLOAT(FloatTypes.Float::new),
    FLOAT_VEC2(FloatTypes.FloatVec2::new),
    FLOAT_VEC3(FloatTypes.FloatVec3::new),
    FLOAT_VEC4(FloatTypes.FloatVec4::new),

    FLOAT_MAT2(NoUniform::new),
    FLOAT_MAT3(NoUniform::new),
    FLOAT_MAT4(NoUniform::new),
    FLOAT_MAT2x3(NoUniform::new),
    FLOAT_MAT2x4(NoUniform::new),
    FLOAT_MAT3x2(NoUniform::new),
    FLOAT_MAT3x4(NoUniform::new),
    FLOAT_MAT4x2(NoUniform::new),
    FLOAT_MAT4x3(NoUniform::new),

    // doubles
    DOUBLE(NoUniform::new),
    DOUBLE_VEC2(NoUniform::new),
    DOUBLE_VEC3(NoUniform::new),
    DOUBLE_VEC4(NoUniform::new),

    DOUBLE_MAT2(NoUniform::new),
    DOUBLE_MAT3(NoUniform::new),
    DOUBLE_MAT4(NoUniform::new),
    DOUBLE_MAT2x3(NoUniform::new),
    DOUBLE_MAT2x4(NoUniform::new),
    DOUBLE_MAT3x2(NoUniform::new),
    DOUBLE_MAT3x4(NoUniform::new),
    DOUBLE_MAT4x2(NoUniform::new),
    DOUBLE_MAT4x3(NoUniform::new),

    // samplers: 1D, 2D, other
    SAMPLER_1D(NoUniform::new),
    SAMPLER_1D_SHADOW(NoUniform::new),
    SAMPLER_1D_ARRAY(NoUniform::new),
    SAMPLER_1D_ARRAY_SHADOW(NoUniform::new),

    SAMPLER_2D(NoUniform::new),
    SAMPLER_2D_SHADOW(NoUniform::new),
    SAMPLER_2D_ARRAY(NoUniform::new),
    SAMPLER_2D_ARRAY_SHADOW(NoUniform::new),
    SAMPLER_2D_MULTISAMPLE(NoUniform::new),
    SAMPLER_2D_MULTISAMPLE_ARRAY(NoUniform::new),

    SAMPLER_3D(NoUniform::new),
    SAMPLER_CUBE(NoUniform::new),
    SAMPLER_CUBE_SHADOW(NoUniform::new),
    SAMPLER_BUFFER(NoUniform::new),
    SAMPLER_2D_RECT(NoUniform::new),
    SAMPLER_2D_RECT_SHADOW(NoUniform::new),

    // int samplers: 1D, 2D, other
    INT_SAMPLER_1D(NoUniform::new),
    INT_SAMPLER_1D_ARRAY(NoUniform::new),

    INT_SAMPLER_2D(NoUniform::new),
    INT_SAMPLER_2D_ARRAY(NoUniform::new),
    INT_SAMPLER_2D_MULTISAMPLE(NoUniform::new),
    INT_SAMPLER_2D_MULTISAMPLE_ARRAY(NoUniform::new),

    INT_SAMPLER_3D(NoUniform::new),
    INT_SAMPLER_CUBE(NoUniform::new),
    INT_SAMPLER_BUFFER(NoUniform::new),
    INT_SAMPLER_2D_RECT(NoUniform::new),

    // unsigned int samplers: 1D, 2D, other
    UINT_SAMPLER_1D(NoUniform::new),
    UINT_SAMPLER_1D_ARRAY(NoUniform::new),

    UINT_SAMPLER_2D(NoUniform::new),
    UINT_SAMPLER_2D_ARRAY(NoUniform::new),
    UINT_SAMPLER_2D_MULTISAMPLE(NoUniform::new),
    UINT_SAMPLER_2D_MULTISAMPLE_ARRAY(NoUniform::new),

    UINT_SAMPLER_3D(NoUniform::new),
    UINT_SAMPLER_CUBE(NoUniform::new),
    UINT_SAMPLER_BUFFER(NoUniform::new),
    UINT_SAMPLER_2D_RECT(NoUniform::new),

    // images: 1D, 2D, other
    IMAGE_1D(NoUniform::new),
    IMAGE_1D_ARRAY(NoUniform::new),

    IMAGE_2D(NoUniform::new),
    IMAGE_2D_ARRAY(NoUniform::new),
    IMAGE_2D_RECT(NoUniform::new),
    IMAGE_2D_MULTISAMPLE(NoUniform::new),
    IMAGE_2D_MULTISAMPLE_ARRAY(NoUniform::new),

    IMAGE_3D(NoUniform::new),
    IMAGE_CUBE(NoUniform::new),
    IMAGE_BUFFER(NoUniform::new),


    // int images: 1D, 2D, other
    INT_IMAGE_1D(NoUniform::new),
    INT_IMAGE_1D_ARRAY(NoUniform::new),

    INT_IMAGE_2D(NoUniform::new),
    INT_IMAGE_2D_ARRAY(NoUniform::new),
    INT_IMAGE_2D_RECT(NoUniform::new),
    INT_IMAGE_2D_MULTISAMPLE(NoUniform::new),
    INT_IMAGE_2D_MULTISAMPLE_ARRAY(NoUniform::new),

    INT_IMAGE_3D(NoUniform::new),
    INT_IMAGE_CUBE(NoUniform::new),
    INT_IMAGE_BUFFER(NoUniform::new),

    // unsigned int images: 1D, 2D, other
    UINT_IMAGE_1D(NoUniform::new),
    UINT_IMAGE_1D_ARRAY(NoUniform::new),

    UINT_IMAGE_2D(NoUniform::new),
    UINT_IMAGE_2D_ARRAY(NoUniform::new),
    UINT_IMAGE_2D_RECT(NoUniform::new),
    UINT_IMAGE_2D_MULTISAMPLE(NoUniform::new),
    UINT_IMAGE_2D_MULTISAMPLE_ARRAY(NoUniform::new),

    UINT_IMAGE_3D(NoUniform::new),
    UINT_IMAGE_CUBE(NoUniform::new),
    UINT_IMAGE_BUFFER(NoUniform::new),;

    static Map<Integer, UniformType> map = new HashMap<>();
    static Class<?>[] classes = new Class<?>[]{
            GL11.class, GL20.class, GL21.class, GL30.class, GL31.class, GL32.class, GL33.class, GL40.class, GL42.class
    };

    static {
        // advanced shader -> gl21, basic shader -> gl20;
        for (UniformType type : values()) {
            String name = "GL_" + type.name().replaceAll("UINT", "UNSIGNED_INT");
            for (Class<?> clazz : classes) {
                try {
                    Field f = clazz.getField(name);
                    Class<?> t = f.getType();
                    if (t == int.class) {
                        type.type = f.getInt(null);
                        map.put(type.type, type);
                        Logs.debug(" == Found %s.%s, it is %d (0x%s)", clazz.getName(), name, type.type, Integer.toHexString(type.type));
                        break;
                    }
                } catch (NoSuchFieldException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
            if (!map.containsValue(type)) {
                Logs.error("Couldn't find %s", name);
            }
        }
    }

    protected int type;
    private UniformInitializer initializer;

    UniformType(UniformInitializer initializer) {
        this.initializer = initializer;
    }

    public static UniformType getByGlEnum(int type) {
        UniformType uniformType = map.get(type);
        if (uniformType == null) uniformType = NONE;
        return uniformType;
    }

    public Uniform make(Shader owner, String name, UniformType type, int size, int location) {
        return initializer.make(owner, name, type, size, location);
    }

    @FunctionalInterface
    public interface UniformInitializer {
        Uniform make(Shader owner, String name, UniformType type, int size, int location);
    }

}
