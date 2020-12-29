package com.teamwizardry.wizardry.common.packet;

import com.teamwizardry.librarianlib.core.util.Client;
import com.teamwizardry.librarianlib.core.util.sided.SidedSupplier;
import com.teamwizardry.librarianlib.courier.PacketType;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import ll.dev.thecodewarrior.prism.annotation.RefractClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@RefractClass
public class CRenderSpellPacket extends PacketType<CRenderSpellPacket.Packet> {

    public CRenderSpellPacket() {
        super(Packet.class);
    }

    @Override
    public void encode(Packet packet, @NotNull PacketBuffer buffer) {
        buffer.writeCompoundTag(packet.instance);
        buffer.writeCompoundTag(packet.target);
    }

    @Override
    public Packet decode(@NotNull PacketBuffer buffer) {
        return new Packet(buffer.readCompoundTag(), buffer.readCompoundTag());
    }

    @Override
    public void handle(Packet packet, @NotNull Supplier<NetworkEvent.Context> context) {
        PlayerEntity player = SidedSupplier.client(Client::getPlayer);
        if (player == null) return;

        context.get().enqueueWork(() -> {

            Instance.fromNBT(player.getEntityWorld().getWorld(), packet.instance)
                    .runClient(player.getEntityWorld().getWorld(),
                            Interactor.fromNBT(player.getEntityWorld().getWorld(), packet.target));
        });
    }

    public static class Packet {
        public final CompoundNBT instance;
        public final CompoundNBT target;

        public Packet(CompoundNBT instance, CompoundNBT target) {
            this.instance = instance;
            this.target = target;
        }
    }

}
