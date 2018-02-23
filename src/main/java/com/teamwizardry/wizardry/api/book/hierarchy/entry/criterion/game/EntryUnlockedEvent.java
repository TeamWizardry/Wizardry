package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game;

import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author WireSegal
 * Created at 10:02 PM on 2/21/18.
 */
@Cancelable
public class EntryUnlockedEvent extends Event {
	private final EntityPlayer player;
	private final Entry entry;

	public EntryUnlockedEvent(EntityPlayer player, Entry entry) {
		this.player = player;
		this.entry = entry;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public Entry getEntry() {
		return entry;
	}
}
