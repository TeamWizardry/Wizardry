package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.Wizardry;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CooldownHandler {

	public static CooldownHandler INSTANCE = new CooldownHandler();

	private Function2<EntityLivingBase, Object, Unit> playerHandler = MethodHandleHelper.wrapperForSetter(EntityLivingBase.class, "aE", "field_184617_aD", "ticksSinceLastSwing");

	private boolean resetMain = false, resetOff = false;

	private CooldownHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		if (playerHandler == null) return;
		if (Minecraft.getMinecraft().player == null) return;

		if (resetMain) {
			playerHandler.invoke(Minecraft.getMinecraft().player, 1000);
			Wizardry.proxy.setItemStackHandHandler(EnumHand.MAIN_HAND, Minecraft.getMinecraft().player.getHeldItemMainhand());
		}
		if (resetOff)
			Wizardry.proxy.setItemStackHandHandler(EnumHand.OFF_HAND, Minecraft.getMinecraft().player.getHeldItemOffhand());
	}

	public void setResetOff(boolean resetOff) {
		this.resetOff = resetOff;
	}

	public void setResetMain(boolean resetMain) {
		this.resetMain = resetMain;
	}
}
