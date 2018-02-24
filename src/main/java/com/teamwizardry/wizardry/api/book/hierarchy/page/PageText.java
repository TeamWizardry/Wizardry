package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PageText extends PageString {

	private final String key;
	private final Object[] args;

	public PageText(Entry entry, JsonObject jsonElement) {
		super(entry);
		TranslationHolder holder = TranslationHolder.fromJson(jsonElement);
		key = holder == null ? "" : holder.key;
		args = holder == null ? new Object[0] : holder.args;

	}

	@Override
	public Collection<String> getSearchableKeys() {
		return Lists.newArrayList(key);
	}

	@Override
	public @Nullable Collection<String> getSearchableStrings() {
		return Arrays.stream(args).map(Object::toString).collect(Collectors.toList());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getText() {
		return I18n.format(key, args).replace("&", "§");
	}

	public static class TranslationHolder {
		public final String key;
		public final Object[] args;

		public TranslationHolder(String key, Object[] args) {
			this.key = key;
			this.args = args;
		}

		public static TranslationHolder fromJson(JsonObject jsonElement) {
			List<Object> arguments = Lists.newArrayList();
			try {
				String key = jsonElement.getAsJsonPrimitive("value").getAsString();
				if (jsonElement.has("args"))
					for (JsonElement arg : jsonElement.getAsJsonArray("args"))
						if (arg.isJsonPrimitive()) {
							if (arg.getAsJsonPrimitive().isNumber()) {
								if (arg.getAsLong() != arg.getAsDouble())
									arguments.add(arg.getAsDouble());
								else
									arguments.add(arg.getAsInt());
							} else if (arg.getAsJsonPrimitive().isBoolean())
								arguments.add(arg.getAsBoolean());
							else
								arguments.add(new TranslationHolder(arg.getAsString(), new Object[0]));
						} else if (arg.isJsonNull()) {
							arguments.add("null");
						} else if (arg.isJsonObject()) {
							arguments.add(fromJson(arg.getAsJsonObject()));
						}
				return new TranslationHolder(key, arguments.toArray());
			} catch (Exception ignored) {
			}

			return null;
		}

		@Override
		public String toString() {
			return LibrarianLib.PROXY.translate(key);
		}
	}
}
