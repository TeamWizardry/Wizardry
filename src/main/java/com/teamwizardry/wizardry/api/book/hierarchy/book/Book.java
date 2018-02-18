package com.teamwizardry.wizardry.api.book.hierarchy.book;


import com.teamwizardry.wizardry.api.book.hierarchy.category.Category;

import java.util.List;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Book {
	public final List<Category> categories;

	public Book(List<Category> categories) {
		this.categories = categories;
	}
}
