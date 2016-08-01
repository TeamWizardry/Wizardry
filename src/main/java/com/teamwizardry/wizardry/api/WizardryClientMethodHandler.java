package com.teamwizardry.wizardry.api;

import com.google.common.base.Throwables;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.publicLookup;

/**
 * Created by LordSaad44
 */
@SideOnly(Side.CLIENT)
public class WizardryClientMethodHandler {

	@Nonnull
	private static final MethodHandle setModelVisibilities;

	static {
		try {
			String[] libObf = {"setModelVisibilities", "func_177137_d", "d"};
			Method f = ReflectionHelper.findMethod(RenderPlayer.class, null, libObf, AbstractClientPlayer.class);
			setModelVisibilities = publicLookup().unreflect(f);
		} catch (Throwable t) {
			t.printStackTrace();
			throw Throwables.propagate(t);
		}
	}

	public static void setModelVisibilities(@Nonnull RenderPlayer renderPlayer, @Nonnull AbstractClientPlayer player) {
		try {
			setModelVisibilities.invokeExact(renderPlayer, player);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}

	private static RuntimeException propagate(Throwable t) {
		t.printStackTrace();
		return Throwables.propagate(t);
	}
}
