package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.util.Client;
import com.teamwizardry.librarianlib.core.util.sided.SidedSupplier;
import com.teamwizardry.librarianlib.courier.CourierPacket;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import ll.dev.thecodewarrior.prism.annotation.RefractClass;
import ll.dev.thecodewarrior.prism.annotation.RefractConstructor;
import ll.dev.thecodewarrior.prism.annotation.RefractGetter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

@RefractClass
public class CRenderSpellPacket implements CourierPacket {

    public final CompoundNBT instance;
    public final CompoundNBT target;

    @RefractConstructor
    public CRenderSpellPacket(CompoundNBT instance, CompoundNBT target) {
        this.instance = instance;
        this.target = target;
    }

    @RefractGetter("instance")
    public CompoundNBT myFunInstanceGetter() {
        return instance;
    }

    @RefractGetter("target")
    public CompoundNBT myFunTargetGetter() {
        return target;
    }

    @Override
    public void handle(NetworkEvent.@NotNull Context context) {
        PlayerEntity player = SidedSupplier.client(Client::getPlayer);
        if (player == null) return;

        context.enqueueWork(() -> {

            Instance.fromNBT(player.getEntityWorld().getWorld(), instance)
                    .runClient(player.getEntityWorld().getWorld(),
                            Interactor.fromNBT(player.getEntityWorld().getWorld(), target));
        });
    }
}
