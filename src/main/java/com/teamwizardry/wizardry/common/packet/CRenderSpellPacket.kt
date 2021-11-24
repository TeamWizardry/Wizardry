//package com.teamwizardry.wizardry.common.packet;
//
//import java.util.function.Supplier;
//
//import org.jetbrains.annotations.NotNull;
//
//import com.teamwizardry.librarianlib.core.util.Client;
//import com.teamwizardry.librarianlib.core.util.sided.ClientRunnable;
//import com.teamwizardry.librarianlib.courier.CourierBuffer;
//import com.teamwizardry.librarianlib.courier.PacketType;
//import com.teamwizardry.wizardry.api.spell.Instance;
//import com.teamwizardry.wizardry.api.spell.Interactor;
//
//import ll.dev.thecodewarrior.prism.annotation.RefractClass;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraftforge.fml.network.NetworkEvent;
//
//@RefractClass
//public class CRenderSpellPacket extends PacketType<CRenderSpellPacket.Packet> {
//
//    public CRenderSpellPacket() {
//        super(Packet.class);
//    }
//
//    @Override
//    public void encode(Packet packet, @NotNull CourierBuffer buffer) {
//        buffer.writeCompoundTag(packet.instance);
//        buffer.writeCompoundTag(packet.target);
//    }
//
//    @Override
//    public Packet decode(@NotNull CourierBuffer buffer) {
//        return new Packet(buffer.readCompoundTag(), buffer.readCompoundTag());
//    }
//
//    @Override
//    public void handle(Packet packet, @NotNull Supplier<NetworkEvent.Context> context) {
//        context.get().enqueueWork(((ClientRunnable) () -> {
//            PlayerEntity player = Client.getPlayer();
//            if (player == null) return;
//
//            Instance.fromNBT(player.getEntityWorld(), packet.instance)
//            .runClient(player.getEntityWorld(),
//                    Interactor.fromNBT(player.getEntityWorld(), packet.target));
//
//        }));
//    }
//    
//    public static class Packet {
//        public final CompoundNBT instance;
//        public final CompoundNBT target;
//
//        public Packet(CompoundNBT instance, CompoundNBT target) {
//            this.instance = instance;
//            this.target = target;
//        }
//    }
//}
