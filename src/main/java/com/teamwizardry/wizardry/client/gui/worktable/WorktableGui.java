package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.*;
import com.teamwizardry.librarianlib.features.gui.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.api.util.CubicBezier;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.network.PacketSendSpellToBook;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Saad on 6/17/2016.
 */
@SuppressWarnings("rawtypes")
public class WorktableGui extends GuiBase {

	private static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table_background.png"));
	private static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 480, 224);
	private static final Sprite SCROLL_BAR = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/scroll_bar.png"));
	private static final Sprite SCROLL_BAR_BAR = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/scroll_bar_bar.png"));
	private static final Sprite SIDE_BAR = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar.png"));
	private static final Sprite BUTTON_NORMAL = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button.png"));
	private static final Sprite BUTTON_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_highlighted.png"));
	private static final Sprite BUTTON_PRESSED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_pressed.png"));
	ComponentVoid paper;
	GuiComponent<?> selectedcomponent;
	BiMap<GuiComponent, UUID> paperComponents = HashBiMap.create();
	HashMap<UUID, UUID> componentLinks = new HashMap<>();
	private HashSet<ArrayList<Module>> compiledSpell = new HashSet<>();
	ComponentWhitelistedModifiers whitelistedModifiers;

	public WorktableGui() {
		super(480, 224);

		ComponentRect rect = new ComponentRect(0, 0, 40000, 40000);
		rect.getColor().setValue(new Color(0x80000000, true));
		GlMixin.INSTANCE.transform(rect).setValue(new Vec3d(0, 0, -20));
		getFullscreenComponents().add(rect);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE, 0, 0);
		GlMixin.INSTANCE.transform(background).setValue(new Vec3d(0, 0, 20));
		getMainComponents().add(background);

		paper = new ComponentVoid(180, 19, 180, 188);
		getMainComponents().add(paper);

		ComponentSprite shapes = new ComponentSprite(SIDE_BAR, 29, 31, 48, 80);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollShapes = addModules(shapes, ModuleType.SHAPE);
		GlMixin.INSTANCE.transform(scrollShapes).setValue(new Vec3d(0, 0, 10));
		addScrollbar(shapes, scrollShapes, 77, 31, ModuleType.SHAPE);
		getMainComponents().add(shapes);

		ComponentSprite effects = new ComponentSprite(SIDE_BAR, 93, 31, 48, 80);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollEffects = addModules(effects, ModuleType.EFFECT);
		GlMixin.INSTANCE.transform(scrollEffects).setValue(new Vec3d(0, 0, 10));
		addScrollbar(effects, scrollEffects, 141, 31, ModuleType.EFFECT);
		getMainComponents().add(effects);

		ComponentSprite events = new ComponentSprite(SIDE_BAR, 29, 123, 48, 80);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollEvents = addModules(events, ModuleType.EVENT);
		GlMixin.INSTANCE.transform(scrollEvents).setValue(new Vec3d(0, 0, 10));
		addScrollbar(events, scrollEvents, 77, 123, ModuleType.EVENT);
		getMainComponents().add(events);

		ComponentSprite modifiers = new ComponentSprite(SIDE_BAR, 93, 123, 48, 80);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollModifiers = addModules(modifiers, ModuleType.MODIFIER);
		GlMixin.INSTANCE.transform(scrollModifiers).setValue(new Vec3d(0, 0, 10));
		addScrollbar(modifiers, scrollModifiers, 141, 123, ModuleType.MODIFIER);
		getMainComponents().add(modifiers);

		ComponentSprite save = new ComponentSprite(BUTTON_NORMAL, 395, 30, (int) (88 / 1.5), (int) (24 / 1.5));
		GlMixin.INSTANCE.transform(save).setValue(new Vec3d(0, 0, 20));
		int width = Minecraft.getMinecraft().fontRenderer.getStringWidth("SAVE");
		int height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		ComponentText textSave = new ComponentText((save.getSize().getXi() / 2) - width / 2, (save.getSize().getYi() / 2) - height / 2);
		textSave.getText().setValue("SAVE");
		save.add(textSave);

		save.BUS.hook(GuiComponent.MouseInEvent.class, event -> {
			save.setSprite(BUTTON_HIGHLIGHTED);
		});
		save.BUS.hook(GuiComponent.MouseOutEvent.class, event -> {
			save.setSprite(BUTTON_NORMAL);
		});
		save.BUS.hook(GuiComponent.MouseDownEvent.class, event -> {
			if (event.getComponent().getMouseOver()) {
				save.setSprite(BUTTON_PRESSED);
			}
		});
		save.BUS.hook(GuiComponent.MouseUpEvent.class, event -> {
			if (event.getComponent().getMouseOver()) {
				save.setSprite(BUTTON_HIGHLIGHTED);
			}
		});

		save.BUS.hook(GuiComponent.MouseClickEvent.class, (event) -> {
			final long[] animStart = {System.currentTimeMillis()};

			HashSet<GuiComponent> heads = getHeads();
			if (heads.isEmpty()) return;
			compiledSpell.clear();
			for (GuiComponent component : heads) {
				ArrayList<Module> stream = new ArrayList<>();
				compileModule(stream, component);
				compiledSpell.add(stream);
			}

			SpellBuilder builder = new SpellBuilder(compiledSpell);

			for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
				if (stack.getItem() == ModItems.BOOK) {
					int slot = Minecraft.getMinecraft().player.inventory.getSlotFor(stack);
					PacketHandler.NETWORK.sendToServer(new PacketSendSpellToBook(slot, (ArrayList<ItemStack>) builder.getInventory()));
				}
			}

			BiMap<GuiComponent, UUID> paperComponents = HashBiMap.create();

			ComponentVoid fakePaper = new ComponentVoid(0, 0, getFullscreenComponents().getSize().getXi(), getFullscreenComponents().getSize().getYi());
			getFullscreenComponents().add(fakePaper);

			ComponentVoid bookIcon = new ComponentVoid(getFullscreenComponents().getSize().getXi(), 0, 32, 32);
			Sprite bookIconSprite = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/physics_book.png"));
			fakePaper.add(bookIcon);

			for (GuiComponent<?> component : this.paperComponents.keySet()) {
				Module module = getModule(component);
				if (module == null) continue;

				Vec2d untransform = component.getParent().unTransformRoot(component, event.getMousePos(), false);
				ComponentSprite plate = new ComponentSprite(TableModule.plate, untransform.getXi(), untransform.getYi(), component.getSize().getXi(), component.getSize().getYi());
				plate.setData(Vec2d.class, plate.getPos());
				plate.addTag(module);
				GlMixin.INSTANCE.transform(plate).setValue(new Vec3d(0, 0, 100));

				Sprite icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + module.getID() + ".png"));
				ComponentSprite iconComp = new ComponentSprite(icon, 2, 2, 12, 12);
				plate.add(iconComp);

				paperComponents.put(plate, this.paperComponents.get(component));
				fakePaper.add(plate);

				//--- RENDER WIRE ---//
				plate.BUS.hook(GuiComponent.PreDrawEvent.class, (event1) -> {

					UUID linkedUuid = componentLinks.get(paperComponents.get(event1.getComponent()));

					GuiComponent component1 = paperComponents.inverse().get(linkedUuid);
					if (component1 == null) return;
					if (linkedUuid == paperComponents.get(event1.getComponent())) return;

					Vec2d toPos = null;
					if (component1.hasData(Vec2d.class, "origin_pos"))
						toPos = (Vec2d) component1.getData(Vec2d.class, "origin_pos");
					if (toPos == null) toPos = component1.getPos();
					toPos = toPos.add(8, 8);

					Vec2d fromPos = null;
					if (event1.getComponent().hasData(Vec2d.class, "origin_pos"))
						fromPos = ((Vec2d) event1.getComponent().getData(Vec2d.class, "origin_pos"));
					if (fromPos == null) fromPos = event1.getComponent().getPos();
					fromPos = fromPos.add(8, 8);

					Module module1 = getModule(component1);
					if (module1 == null) return;

					Module module2 = getModule(event1.getComponent());
					if (module2 == null) return;

					TableModule.drawWire(fromPos, toPos, TableModule.getColorForModule(module2.getModuleType()), TableModule.getColorForModule(module1.getModuleType()));
				});
				//--- RENDER WIRE ---//
			}

			int maxTime = 2;
			double halfTime = maxTime / 2.0;
			animStart[0] = System.currentTimeMillis();
			fakePaper.BUS.hook(GuiComponent.PostDrawEvent.class, (event1) -> {
				double time = (System.currentTimeMillis() - animStart[0]) / 1000.0;

				if (time <= halfTime && !event1.getComponent().hasTag("updated")) {
					float progress = (float) time / (float) halfTime;
					float t = new CubicBezier(0.17f, 0.67f, 0.38f, 0.99f).eval(progress);

					bookIcon.BUS.hook(GuiComponent.PostDrawEvent.class, (event2) -> {
						GlStateManager.pushMatrix();
						GlStateManager.translate(getFullscreenComponents().getSize().getX() - 32, 0, 1000);
						GlStateManager.color(1f, 1f, 1f, t);
						bookIconSprite.getTex().bind();
						bookIconSprite.draw((int) event2.getPartialTicks(), 0, 0, 32, 32);
						GlStateManager.popMatrix();
					});

					for (GuiComponent<?> component : paperComponents.keySet()) {
						UUID uuid = paperComponents.get(component);
						if (uuid == null) continue;

						Vec2d origin = component.getData(Vec2d.class);
						if (origin == null) continue;

						RandUtilSeed seed = new RandUtilSeed(uuid.hashCode());
						Vec2d to = origin.add(seed.nextDouble(-50, 50), seed.nextDouble(-50, 50));
						Vec2d diff = origin.sub(to);
						Vec2d progDist = diff.mul(t);
						component.setPos(origin.add(progDist));
					}
				} else if (time < maxTime) {
					if (!event1.getComponent().hasTag("updated")) {
						animStart[0] = System.currentTimeMillis();
						event1.getComponent().addTag("updated");
					}
					float progress = (float) time / (float) maxTime;
					float t = new CubicBezier(0.17f, 0.67f, 0.38f, 0.99f).eval(progress);

					for (GuiComponent<?> component : paperComponents.keySet()) {
						if (!component.hasTag("updated")) {
							component.setData(Vec2d.class, component.getPos());
							component.addTag("updated");
						}
						Module module = getModule(component);
						if (module == null) continue;

						Vec2d origin = component.getData(Vec2d.class);
						if (origin == null) continue;

						Vec2d to = new Vec2d(getFullscreenComponents().getSize().getXi(), 0);
						to = to.sub(fakePaper.getPos());
						Vec2d diff = to.sub(origin);
						Vec2d progDist = diff.mul(t);
						component.setPos(origin.add(progDist));
					}
				} else {
					event1.getComponent().invalidate();
				}
			});
		});
		getMainComponents().add(save);

		whitelistedModifiers = new ComponentWhitelistedModifiers(this, 384, save.getPos().getYi() + save.getSize().getYi() + 8, 80, 170);
		GlMixin.INSTANCE.transform(whitelistedModifiers).setValue(new Vec3d(0, 0, 20));
		getMainComponents().add(whitelistedModifiers);
	}

	public UUID getUUID(GuiComponent component) {
		return paperComponents.get(component);
	}

	public GuiComponent getComponent(UUID uuid) {
		return paperComponents.inverse().get(uuid);
	}

	@SuppressWarnings("unchecked")
	private void compileModule(ArrayList<Module> stream, @Nullable GuiComponent component) {
		if (component == null) return;

		Module module = getModule(component);
		if (module == null) return;

		stream.add(module);
		for (Module modifier : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
			if (!(modifier instanceof ModuleModifier)) continue;
			if (component.hasData(Integer.class, modifier.getID())) {
				int x = (int) component.getData(Integer.class, modifier.getID());
				for (int i = 0; i < x; i++) {
					stream.add(modifier.copy());
				}
			}
		}

		if (!componentLinks.containsKey(getUUID(component))) return;
		UUID uuidChild = componentLinks.get(getUUID(component));

		GuiComponent childComp = getComponent(uuidChild);
		if (childComp == null) return;

		Module child = getModule(childComp);
		if (child == null) return;

		compileModule(stream, childComp);
	}

	private HashSet<GuiComponent> getHeads() {
		HashSet<GuiComponent> heads = new HashSet<>();
		for (GuiComponent component : paperComponents.keySet()) {
			if (componentLinks.containsValue(getUUID(component))) continue;
			heads.add(component);
		}

		return heads;
	}

	public int getLinksTo(GuiComponent comp) {
		UUID main = paperComponents.get(comp);
		int count = 0;
		for (UUID uuid : componentLinks.values()) {
			if (main.equals(uuid)) {
				count++;
			}
		}
		return count;
	}

	private ComponentGrid addModules(ComponentSprite parent, ModuleType type) {
		ComponentGrid grid = new ComponentGrid(0, 0, 16, 16, 3);
		parent.add(grid);

		ArrayList<GuiComponent<?>> tmp = new ArrayList<>();
		for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
			TableModule item = new TableModule(this, parent, module.copy(), false, true);
			tmp.add(item.component);
		}

		ArrayList<GuiComponent<?>> scrollable = (ArrayList<GuiComponent<?>>) Utils.getVisibleComponents(tmp, 0);
		for (GuiComponent<?> component : scrollable) {
			grid.add(component);
		}
		return grid;
	}

	@SuppressWarnings("unchecked")
	private void addScrollbar(ComponentSprite parent, ComponentGrid gridView, int x, int y, ModuleType type) {
		ComponentSprite scrollBar = new ComponentSprite(SCROLL_BAR_BAR, x, y, 5, 80);
		ComponentSprite bar = new ComponentSprite(SCROLL_BAR, 1, 0, 3, 11);
		scrollBar.BUS.hook(GuiComponent.MouseDragEvent.class, (event) -> {
			if (!event.getComponent().getMouseOver() && !parent.getMouseOver()) return;
			for (GuiComponent<?> comp : paperComponents.keySet()) if (comp.hasTag("dragging")) return;

			Vec2d mouse = event.getComponent().getParent().unTransformChildPos(event.getComponent(), event.getMousePos());
			double clamp = MathHelper.clamp(mouse.getY(), y + 5.5, y + 79 - 5.5);

			bar.setPos(new Vec2d(bar.getPos().getX(), (clamp - y - 5.5)));
			double percent = MathHelper.clamp((clamp / 79.0) - 0.3, 0, 1);

			ArrayList<GuiComponent<?>> compTmp = new ArrayList<>(gridView.getChildren());
			compTmp.forEach(gridView::remove);

			ArrayList<GuiComponent<?>> tmp = new ArrayList<>();
			for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
				TableModule item = new TableModule(this, parent, module.copy(), false, false);
				tmp.add(item.component);
			}
			ArrayList<GuiComponent<?>> scrollable = (ArrayList<GuiComponent<?>>) Utils.getVisibleComponents(tmp, percent);
			for (GuiComponent<?> component : scrollable) {
				gridView.add(component);
			}
		});
		scrollBar.BUS.hook(GuiComponent.MouseWheelEvent.class, (event) -> {
			if (!event.getComponent().getMouseOver() && !parent.getMouseOver()) return;
			for (GuiComponent<?> comp : paperComponents.keySet()) if (comp.hasTag("dragging")) return;

			int dir = event.getDirection().ydirection * -16;
			double barPos = bar.getPos().getY();
			double clamp = MathHelper.clamp(barPos + dir, 0, 79 - 11);

			bar.setPos(new Vec2d(bar.getPos().getX(), clamp));
			double percent = MathHelper.clamp(clamp / (79.0 - 11), 0, 1);

			ArrayList<GuiComponent<?>> compTmp = new ArrayList<>(gridView.getChildren());
			compTmp.forEach(gridView::remove);

			ArrayList<GuiComponent<?>> tmp = new ArrayList<>();
			for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
				TableModule item = new TableModule(this, parent, module.copy(), false, false);
				tmp.add(item.component);
			}
			ArrayList<GuiComponent<?>> scrollable = (ArrayList<GuiComponent<?>>) Utils.getVisibleComponents(tmp, percent);
			for (GuiComponent<?> component : scrollable) {
				gridView.add(component);
			}
		});
		scrollBar.add(bar);
		getMainComponents().add(scrollBar);
	}

	@Nullable
	public Module getModule(@Nullable GuiComponent component) {
		if (component == null) return null;
		for (Object object : component.getTags()) {
			if (object instanceof Module) return ((Module) object).copy();
		}
		return null;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
