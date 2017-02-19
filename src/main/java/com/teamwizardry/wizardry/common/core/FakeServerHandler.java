package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.common.entity.EntityStaffFakePlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class FakeServerHandler extends NetHandlerPlayServer {

	public FakeServerHandler(EntityStaffFakePlayer playerIn) {
		super(playerIn.mcServer, new NetworkManager(net.minecraft.network.EnumPacketDirection.SERVERBOUND), playerIn);
	}

	public void update() {
	}

	public void kickPlayerFromServer(@Nonnull String reason) {
	}

	public void processInput(CPacketInput packetIn) {
	}

	public void processVehicleMove(CPacketVehicleMove packetIn) {
	}

	public void processConfirmTeleport(CPacketConfirmTeleport packetIn) {
	}

	public void processPlayer(CPacketPlayer packetIn) {
	}

	public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
	}

	public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<SPacketPlayerPosLook.EnumFlags> relativeSet) {
	}

	public void processPlayerDigging(CPacketPlayerDigging packetIn) {
	}

	public void processRightClickBlock(CPacketPlayerTryUseItemOnBlock packetIn) {
	}

	public void processPlayerBlockPlacement(CPacketPlayerTryUseItem packetIn) {
	}

	public void processCustomPayload(CPacketCustomPayload packetIn) {
	}

	public void handleSpectate(@Nonnull CPacketSpectate packetIn) {
	}

	public void handleResourcePackStatus(CPacketResourcePackStatus packetIn) {
	}

	public void processSteerBoat(@Nonnull CPacketSteerBoat packetIn) {
	}

	public void onDisconnect(ITextComponent reason) {
	}

	public void sendPacket(@Nonnull Packet<?> packetIn) {
	}

	public void processHeldItemChange(CPacketHeldItemChange packetIn) {
	}

	public void processChatMessage(@Nonnull CPacketChatMessage packetIn) {
	}

	public void handleAnimation(CPacketAnimation packetIn) {
	}

	public void processEntityAction(CPacketEntityAction packetIn) {
	}

	public void processUseEntity(CPacketUseEntity packetIn) {
	}

	public void processClientStatus(CPacketClientStatus packetIn) {
	}

	public void processCloseWindow(@Nonnull CPacketCloseWindow packetIn) {
	}

	public void processClickWindow(CPacketClickWindow packetIn) {
	}

	public void processEnchantItem(CPacketEnchantItem packetIn) {
	}

	public void processCreativeInventoryAction(@Nonnull CPacketCreativeInventoryAction packetIn) {
	}

	public void processConfirmTransaction(@Nonnull CPacketConfirmTransaction packetIn) {
	}

	public void processUpdateSign(CPacketUpdateSign packetIn) {
	}

	public void processKeepAlive(CPacketKeepAlive packetIn) {
	}

	public void processPlayerAbilities(CPacketPlayerAbilities packetIn) {
	}

	public void processTabComplete(CPacketTabComplete packetIn) {
	}

	public void processClientSettings(@Nonnull CPacketClientSettings packetIn) {
	}
}
