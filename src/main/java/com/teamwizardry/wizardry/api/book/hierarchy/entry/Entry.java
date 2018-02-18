package com.teamwizardry.wizardry.api.book.hierarchy.entry;


import com.teamwizardry.wizardry.api.book.hierarchy.page.Page;

import java.util.List;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Entry {
	public final List<Page> pages;

	public Entry(List<Page> pages) {
		this.pages = pages;
	}
}
