package com.teamwizardry.wizardry.gui.util;

import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;

public class ScissorUtil {
	@SuppressWarnings("unused")
	private static ScissorUtil INSTANCE = new ScissorUtil();
	
	private ScissorUtil() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private static int screenScale;
	
	@SubscribeEvent
	public static void updateResolution(InitGuiEvent.Pre event) {
		screenScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
	}
	
	public static void push() {
		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
	}
	
	public static void pop() {
		GL11.glPopAttrib();
	}
	
	public static boolean enable() {
		boolean wasEnabled = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		return wasEnabled;
	}
	
	public static void disable() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public static void set(int left, int top, int width, int height) {
		GL11.glScissor(left * screenScale, Minecraft.getMinecraft().displayHeight - (top + height) * screenScale,
                width * screenScale, height * screenScale);
	}

}
