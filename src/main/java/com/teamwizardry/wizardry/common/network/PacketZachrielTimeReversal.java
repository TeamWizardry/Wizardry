package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez.NemezEventHandler;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez.NemezArenaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 * Created at 7:04 PM on 1/15/18.
 */
@PacketRegister(Side.CLIENT)
public class PacketZachrielTimeReversal extends PacketBase {

    @Save
    public NBTTagCompound nemez;

    public PacketZachrielTimeReversal(NemezArenaTracker nemez) {
        this.nemez = new NBTTagCompound();
        this.nemez.setTag("root", nemez.serializeNBT());
    }

    public PacketZachrielTimeReversal() {
        // NO-OP
    }

    @Override
    public void handle(@NotNull MessageContext ctx) {
        ClientRunnable.run(new ClientRunnable() {
            @Override
            @SideOnly(Side.CLIENT)
            public void runIfClient() {
                NemezArenaTracker tracker = new NemezArenaTracker();
                tracker.deserializeNBT(nemez.getTagList("root", Constants.NBT.TAG_COMPOUND));
                NemezEventHandler.reverseTime(Minecraft.getMinecraft().world, tracker);
            }
        });
    }
}
