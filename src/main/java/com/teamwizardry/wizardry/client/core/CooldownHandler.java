package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.Wizardry;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
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
		if (Minecraft.getMinecraft().player == null) return;

		if (resetMain) {
			playerHandler.invoke(Minecraft.getMinecraft().player, 1000);
			Wizardry.PROXY.setItemStackHandHandler(EnumHand.MAIN_HAND, Minecraft.getMinecraft().player.getHeldItemMainhand());
			resetMain = false;
		}
		if (resetOff) {
			Wizardry.PROXY.setItemStackHandHandler(EnumHand.OFF_HAND, Minecraft.getMinecraft().player.getHeldItemOffhand());
			resetOff = false;
		}
	}

	public static void setResetOff(boolean resetOff) {
		CooldownHandler.resetOff = resetOff;
	}

	public static void setResetMain(boolean resetMain) {
		CooldownHandler.resetMain = resetMain;
	}
}
