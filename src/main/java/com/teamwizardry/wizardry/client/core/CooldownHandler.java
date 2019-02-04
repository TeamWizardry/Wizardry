package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlWheelHolder;
import com.teamwizardry.wizardry.api.util.PearlHandlingUtils;
import com.teamwizardry.wizardry.client.core.renderer.PearlRadialUIRenderer;
import com.teamwizardry.wizardry.init.ModKeybinds;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class CooldownHandler {

	private static Function2<EntityLivingBase, Object, Unit> playerHandler = MethodHandleHelper.wrapperForSetter(EntityLivingBase.class, "aE", "field_184617_aD", "ticksSinceLastSwing");

	private static boolean resetMain = false, resetOff = false;

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		if (playerHandler == null) return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (resetMain) {
			playerHandler.invoke(player, 1000);
			Wizardry.proxy.setItemStackHandHandler(EnumHand.MAIN_HAND, player.getHeldItemMainhand());
		}
		if (resetOff)
			Wizardry.proxy.setItemStackHandHandler(EnumHand.OFF_HAND, player.getHeldItemOffhand());

		// Keybind logic
		Minecraft mc = Minecraft.getMinecraft();
		if (ModKeybinds.pearlSwapping.isKeyDown() && mc.currentScreen == null &&
				(player.getHeldItemMainhand().getItem() instanceof IPearlWheelHolder || PearlHandlingUtils.canOpenPearlWheel(player))) {
			mc.displayGuiScreen(PearlRadialUIRenderer.INSTANCE);
		}
	}

	public static void setResetOff(boolean resetOff) {
		CooldownHandler.resetOff = resetOff;
	}

	public static void setResetMain(boolean resetMain) {
		CooldownHandler.resetMain = resetMain;
	}
}
