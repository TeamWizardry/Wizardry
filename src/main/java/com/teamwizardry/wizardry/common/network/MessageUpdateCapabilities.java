package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Saad on 8/16/2016.
 */
public class MessageUpdateCapabilities implements IMessage {

	private NBTTagCompound tags;

	public MessageUpdateCapabilities() {
	}

	public MessageUpdateCapabilities(NBTTagCompound tag) {
		tags = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tags = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tags);
	}

	public static class CapsMessageHandler implements IMessageHandler<MessageUpdateCapabilities, IMessage> {

		@Override
		public IMessage onMessage(final MessageUpdateCapabilities message, final MessageContext ctx) {
            IThreadListener mainThread = (ctx.side.isClient()) ? Minecraft.getMinecraft() : (IThreadListener) ctx.getServerHandler().playerEntity.world;
            mainThread.addScheduledTask(() -> WizardryCapabilityProvider.get(Minecraft.getMinecraft().player).loadNBTData(message.tags));
            return null;
		}
	}
}
