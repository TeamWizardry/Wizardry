package me.lordsaad.wizardry.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class PacketBase implements IMessage {

    public PacketBase() {
    }

    public abstract void handle(MessageContext ctx);

    public IMessage reply(MessageContext ctx) {
        return null;
    }

    public static class Handler implements IMessageHandler<PacketBase, IMessage> {

        @Override
        public IMessage onMessage(PacketBase message, MessageContext ctx) {
            IThreadListener mainThread;
            if (ctx.netHandler instanceof NetHandlerPlayServer)
                mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            else
                mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    message.handle(ctx);
                }
            });
            return message.reply(ctx); // no response in this case
        }
    }
}
