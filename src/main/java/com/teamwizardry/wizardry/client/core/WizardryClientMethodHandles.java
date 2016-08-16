package com.teamwizardry.wizardry.client.core;

import com.google.common.base.Throwables;
import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.wizardry.lib.LibObfuscation;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

import static java.lang.invoke.MethodHandles.publicLookup;

public class WizardryClientMethodHandles {

    @Nonnull
    private static final MethodHandle prevEquippedProgressMainGetter, prevEquippedProgressOffGetter,
            equippedProgressMainGetter, equippedProgressOffGetter,
            stackMainGetter, stackOffGetter;

    static {
        try {
            Field f = ReflectionHelper.findField(ItemRenderer.class, LibObfuscation.ITEMRENDERER_PREVEQUIPPEDPROGRESSMAINHAND);
            prevEquippedProgressMainGetter = publicLookup().unreflectGetter(f);

            f = ReflectionHelper.findField(ItemRenderer.class, LibObfuscation.ITEMRENDERER_PREVEQUIPPEDPROGRESSOFFHAND);
            prevEquippedProgressOffGetter = publicLookup().unreflectGetter(f);

            f = ReflectionHelper.findField(ItemRenderer.class, LibObfuscation.ITEMRENDERER_EQUIPPEDPROGRESSMAINHAND);
            equippedProgressMainGetter = publicLookup().unreflectGetter(f);

            f = ReflectionHelper.findField(ItemRenderer.class, LibObfuscation.ITEMRENDERER_EQUIPPEDPROGRESSOFFHAND);
            equippedProgressOffGetter = publicLookup().unreflectGetter(f);

            f = ReflectionHelper.findField(ItemRenderer.class, LibObfuscation.ITEMRENDERER_ITEMSTACKMAINHAND);
            stackMainGetter = publicLookup().unreflectGetter(f);

            f = ReflectionHelper.findField(ItemRenderer.class, LibObfuscation.ITEMRENDERER_ITEMSTACKOFFHAND);
            stackOffGetter = publicLookup().unreflectGetter(f);

        } catch (Throwable t) {
            LibrarianLog.INSTANCE.error("Couldn't initialize methodhandles! Things will be broken!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }

    public static float getPrevEquipMainHand(@Nonnull ItemRenderer renderer) {
        try {
            return (float) prevEquippedProgressMainGetter.invokeExact(renderer);
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    public static float getPrevEquipOffHand(@Nonnull ItemRenderer renderer) {
        try {
            return (float) prevEquippedProgressOffGetter.invokeExact(renderer);
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    public static float getEquipMainHand(@Nonnull ItemRenderer renderer) {
        try {
            return (float) equippedProgressMainGetter.invokeExact(renderer);
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    public static float getEquipOffHand(@Nonnull ItemRenderer renderer) {
        try {
            return (float) equippedProgressOffGetter.invokeExact(renderer);
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    @Nullable
    public static ItemStack getStackMainHand(@Nonnull ItemRenderer renderer) {
        try {
            return (ItemStack) stackMainGetter.invokeExact(renderer);
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    @Nullable
    public static ItemStack getStackOffHand(@Nonnull ItemRenderer renderer) {
        try {
            return (ItemStack) stackOffGetter.invokeExact(renderer);
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    private static RuntimeException propagate(Throwable t) {
        LibrarianLog.INSTANCE.error("Methodhandle failed!");
        t.printStackTrace();
        return Throwables.propagate(t);
    }
}
