package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

import static com.teamwizardry.wizardry.client.gui.book.BookGui.ARROW;

public class ComponentRecipe extends GuiComponent<ComponentRecipe> {

	private static final Vec2d pos = new Vec2d(35, 25);
	private BookGui bookGui;

	public ComponentRecipe(BookGui bookGui) {
		super(pos.getXi(), pos.getYi(), 200, 300);
		this.bookGui = bookGui;

		reset();
	}

	public void reset() {
		getChildren().forEach(guiComponent -> guiComponent.invalidate());

		if (ItemNBTHelper.getBoolean(bookGui.bookItem, "has_recipe", false)) {


			JsonObject object = new Gson().fromJson(ItemNBTHelper.getString(bookGui.bookItem, "spell_recipe", null), JsonObject.class);
			if (object == null) return;

			ArrayList<ItemStack> inventory = new ArrayList<>();
			JsonArray array = object.getAsJsonArray("list");
			for (int i = 0; i < array.size(); i++) {
				JsonElement element = array.get(i);
				if (!element.isJsonObject()) continue;
				JsonObject obj = element.getAsJsonObject();
				String name = obj.getAsJsonPrimitive("name").getAsString();
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
				if (item == null) continue;
				ItemStack stack = new ItemStack(item);
				stack.setItemDamage(obj.getAsJsonPrimitive("meta").getAsInt());
				stack.setCount(obj.getAsJsonPrimitive("count").getAsInt());
				inventory.add(stack);
			}
			SpellBuilder builder = new SpellBuilder(inventory);

			List<Module> modules = builder.getSpell();
			ArrayList<String> lines = new ArrayList<>();
			Module lastModule = null;
			for (Module module : modules) {
				if (lastModule == null) lastModule = module;
				if (module != null) {
					if (module != lastModule) lines.add("");
					Module tempModule = module;
					int i = 0;
					while (tempModule != null) {
						lines.add(new String(new char[i]).replace("\0", "-") + "> " + tempModule.getReadableName());
						tempModule = tempModule.nextModule;
						i++;
					}
				}
			}
			StringBuilder finalString = new StringBuilder();
			for (String line : lines) {
				finalString.append("\n").append(line);
			}

			ComponentText title = new ComponentText(0, pos.getYi(), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
			title.getWrap().setValue(100);
			title.getScale().setValue(2f);
			title.getText().setValue(finalString.toString());
			add(title);

			JsonArray array2 = object.getAsJsonArray("list");
			int row = 0;
			for (int i = 0; i < array2.size(); i++) {
				if (i > 0 && i % 6 == 0) row++;

				if (array2.get(i).isJsonObject()) {
					JsonObject object1 = array2.get(i).getAsJsonObject();
					if (object1.has("name") && object1.get("name").isJsonPrimitive()) {

						String name = object1.getAsJsonPrimitive("name").getAsString();
						int meta = object1.has("meta") ? object1.get("meta").getAsJsonPrimitive().getAsInt() : 0;
						int count = object1.has("count") ? object1.get("count").getAsJsonPrimitive().getAsInt() : 0;
						Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
						if (item == null) continue;

						ItemStack stack = new ItemStack(item, count, meta);

						ComponentStack componentStack = new ComponentStack(220 + 4 + 24 * (i % 6), 14 + 24 * row);
						componentStack.getStack().setValue(stack);
						add(componentStack);

						if (i % 6 == 0) continue;
						ComponentSprite next = new ComponentSprite(ARROW, 220 + -4 + 24 * (i % 6), 20 + 24 * row, 9, 4);
						add(next);
					}
				}
			}
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
