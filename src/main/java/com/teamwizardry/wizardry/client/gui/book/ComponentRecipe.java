package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

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

			GuiComponent<?> lastComponent = null;
			List<Module> modules = builder.getSpell();
			Module lastModule = null;
			for (Module module : modules) {
				if (lastModule == null) lastModule = module;
				if (module != null) {
					Module tempModule = module;
					int i = 0;
					while (tempModule != null) {

						ComponentText recipeText = new ComponentText(0, (int) (pos.getY() / 2.0), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
						recipeText.getWrap().setValue(100);
						recipeText.getScale().setValue(2f);
						recipeText.getText().setValue(StringUtils.repeat("-", i) + "> " + tempModule.getReadableName());
						recipeText.setSize(new Vec2d(200, 16));
						if (lastComponent != null)
							recipeText.setPos(new Vec2d(0, lastComponent.getPos().getY() + lastComponent.getSize().getY()));
						if (lastComponent != null) lastComponent.add(recipeText);
						else add(recipeText);
						lastComponent = recipeText;

						for (String key : tempModule.attributes.getKeySet()) {
							if (!key.equals(Attributes.MANA) && !key.equals(Attributes.BURNOUT)) {
								ComponentText modifierText = new ComponentText(0, lastComponent.getSize().getYi(), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
								modifierText.getWrap().setValue(200);
								modifierText.getText().setValue(StringUtils.repeat(" ", i * 3) + " | "
										+ key.replace("modifier_", "").replace("_", " ")
										+ " x" + (int) Math.round(tempModule.attributes.getDouble(key)));
								modifierText.setSize(new Vec2d(0, 8));
								lastComponent.add(modifierText);
								lastComponent = modifierText;
							}
						}

						tempModule = tempModule.nextModule;
						i++;
					}
				}
			}

			ComponentVoid recipe = new ComponentVoid(224, 0, 50, 50);
			recipe.setChildScale(2);
			add(recipe);
			JsonArray array2 = object.getAsJsonArray("list");
			int row = 0;
			for (int i = 0; i < array2.size(); i++) {
				if (i > 0 && i % 4 == 0) row++;

				if (array2.get(i).isJsonObject()) {
					JsonObject object1 = array2.get(i).getAsJsonObject();
					if (object1.has("name") && object1.get("name").isJsonPrimitive()) {

						String name = object1.getAsJsonPrimitive("name").getAsString();
						int meta = object1.has("meta") ? object1.get("meta").getAsJsonPrimitive().getAsInt() : 0;
						int count = object1.has("count") ? object1.get("count").getAsJsonPrimitive().getAsInt() : 0;
						Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
						if (item == null) continue;

						ItemStack stack = new ItemStack(item, count, meta);

						ComponentStack componentStack = new ComponentStack(32 * (i % 4), 16 * row);
						componentStack.getStack().setValue(stack);
						recipe.add(componentStack);

						if (i % 4 == 0) continue;
						ComponentSprite next = new ComponentSprite(ARROW, -13 + 32 * (i % 4), 6 + 16 * row, (int) (ARROW.getWidth() / 2.0), (int) (ARROW.getHeight() / 2.0));
						recipe.add(next);
					}
				}
			}
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
