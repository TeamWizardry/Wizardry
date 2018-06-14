package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.common.core.nemez.NemezEventHandler;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 7:04 PM on 1/15/18.
 */
@PacketRegister(Side.CLIENT)
public class PacketNemezReversal extends PacketBase {

	public static final int SYNC_AMOUNT = 20;

	@Save
	public NBTTagCompound nemez;

	public PacketNemezReversal(NemezTracker nemez) {
		this.nemez = new NBTTagCompound();
		this.nemez.setTag("root", nemez.nextNMoments(SYNC_AMOUNT));
	}

	public PacketNemezReversal() {
		// NO-OP
	}

	@Override
	public void handle(@Nonnull MessageContext ctx) {
		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				NemezTracker tracker = NemezEventHandler.getCurrent();
				tracker.absorb(nemez.getTagList("root", Constants.NBT.TAG_COMPOUND));
			}
		});
	}
}
