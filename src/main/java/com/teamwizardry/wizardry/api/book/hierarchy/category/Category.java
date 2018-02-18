package com.teamwizardry.wizardry.api.book.hierarchy.category;

import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;

import java.util.List;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Category {
	public final List<Entry> entries;

	public Category(List<Entry> entries) {
		this.entries = entries;
	}
}
