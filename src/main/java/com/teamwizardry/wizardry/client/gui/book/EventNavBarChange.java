package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.eventbus.EventCancelable;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class EventNavBarChange extends EventCancelable {

	private final int page;

	public EventNavBarChange(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}
}
