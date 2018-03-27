package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.*;
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.gui.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.network.PacketSendSpellToBook;
import com.teamwizardry.wizardry.common.network.PacketWorktableUpdate;
import com.teamwizardry.wizardry.common.tile.TileMagiciansWorktable;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Demoniaque on 6/17/2016.
 */
public class WorktableGui extends GuiBase {

	private static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table.png"));
	private static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 480, 224);
	private static final Sprite SCROLL_BAR = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/scroll_bar.png"));
	private static final Sprite SCROLL_BAR_GRIP = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/scroll_bar_bar.png"));
	private static final Sprite SIDE_BAR = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar.png"));
	private static final Sprite SIDE_BAR_LONG = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar_long.png"));
	private static final Sprite BUTTON_NORMAL = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button.png"));
	private static final Sprite BUTTON_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_highlighted.png"));
	private static final Sprite BUTTON_PRESSED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_pressed.png"));
	public BlockPos worktablePos;
	ComponentVoid paper;
	GuiComponent selectedComponent;
	BiMap<GuiComponent, UUID> paperComponents = HashBiMap.create();
	HashMap<UUID, UUID> componentLinks = new HashMap<>();
	ComponentWhitelistedModifiers whitelistedModifiers;

	public WorktableGui(BlockPos worktablePos) {
		super(480, 224);
		this.worktablePos = worktablePos;

		ComponentRect rect = new ComponentRect(0, 0, 40000, 40000);
		rect.getColor().setValue(new Color(0x80000000, true));
		GlMixin.INSTANCE.transform(rect).setValue(new Vec3d(0, 0, -20));
		getFullscreenComponents().add(rect);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE, 0, 0);
		background.getTransform().setTranslateZ(20);
		getMainComponents().add(background);

		paper = new ComponentVoid(180, 19, 180, 188);
		getMainComponents().add(paper);

		TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(worktablePos);
		if (tile != null && tile instanceof TileMagiciansWorktable) {
			for (Map.Entry<SpellRing, UUID> entrySet : ((TileMagiciansWorktable) tile).paperComponents.entrySet()) {
				Module module = entrySet.getKey().getModule();
				if (module == null) continue;

				TableModule tableModule = new TableModule(this, null, module, true, false);

				if (entrySet.getKey().getInformationTag().hasKey("worktable_x") && entrySet.getKey().getInformationTag().hasKey("worktable_y")) {
					double x = entrySet.getKey().getInformationTag().getDouble("worktable_x");
					double y = entrySet.getKey().getInformationTag().getDouble("worktable_y");
					tableModule.component.setPos(new Vec2d(x, y));
				}

				DragMixin drag = new DragMixin(tableModule.component, vec2d -> vec2d);
				drag.setDragOffset(new Vec2d(6, 6));

				paperComponents.put(tableModule.component, entrySet.getValue());
				paper.add(tableModule.component);
			}

			for (Map.Entry<UUID, UUID> entrySet : ((TileMagiciansWorktable) tile).componentLinks.entrySet()) {
				componentLinks.put(entrySet.getKey(), entrySet.getValue());
			}
		}

		ComponentSprite shapes = new ComponentSprite(SIDE_BAR, 29, 31, 48, 80);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollShapes = addModules(shapes, ModuleType.SHAPE);
		scrollShapes.getTransform().setTranslateZ(10);
		//	addScrollbar(shapes, scrollShapes, 77, 31, ModuleType.SHAPE, 80);
		getMainComponents().add(shapes);

		ComponentSprite effects = new ComponentSprite(SIDE_BAR_LONG, 93, 37, 48, 160);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollEffects = addModules(effects, ModuleType.EFFECT);
		scrollEffects.getTransform().setTranslateZ(10);
		//	addScrollbar(effects, scrollEffects, 140, 37, ModuleType.EFFECT, 160);
		getMainComponents().add(effects);

		ComponentSprite events = new ComponentSprite(SIDE_BAR, 29, 123, 48, 80);
		GlMixin.INSTANCE.transform(shapes).setValue(new Vec3d(0, 0, -15));
		ComponentGrid scrollEvents = addModules(events, ModuleType.EVENT);
		scrollEvents.getTransform().setTranslateZ(10);
		//	addScrollbar(events, scrollEvents, 77, 123, ModuleType.EVENT, 80);
		getMainComponents().add(events);

		ComponentSprite save = new ComponentSprite(BUTTON_NORMAL, 395, 30, (int) (88 / 1.5), (int) (24 / 1.5));
		save.getTransform().setTranslateZ(20);
		String saveStr = LibrarianLib.PROXY.translate("wizardry.misc.save");
		int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(saveStr);
		int height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		ComponentText textSave = new ComponentText((save.getSize().getXi() / 2) - width / 2, (save.getSize().getYi() / 2) - height / 2);
		textSave.getText().setValue(saveStr);
		save.add(textSave);

		save.render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();

			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				txt.add(TextFormatting.RED + LibrarianLib.PROXY.translate("wizardry.misc.save_error"));
			}
			return txt;
		});

		save.BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else save.setSprite(BUTTON_HIGHLIGHTED);
		});
		save.BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else save.setSprite(BUTTON_NORMAL);
		});
		save.BUS.hook(GuiComponentEvents.MouseDownEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else if (event.component.getMouseOver()) {
				save.setSprite(BUTTON_PRESSED);
			}
		});
		save.BUS.hook(GuiComponentEvents.MouseUpEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else if (event.component.getMouseOver()) {
				save.setSprite(BUTTON_HIGHLIGHTED);
			}
		});

		save.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {

			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) return;

			HashSet<GuiComponent> heads = getHeads();
			if (heads.isEmpty()) return;

			List<List<Module>> compiledSpell = new ArrayList<>();

			for (GuiComponent component : heads) {
				ArrayList<Module> stream = new ArrayList<>();
				compileModule(stream, component);

				compiledSpell.add(stream);
			}

			SpellBuilder builder = new SpellBuilder(compiledSpell);

			for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
				if (stack.getItem() == ModItems.BOOK) {
					int slot = Minecraft.getMinecraft().player.inventory.getSlotFor(stack);
					PacketHandler.NETWORK.sendToServer(new PacketSendSpellToBook(slot, builder.getSpell()));
				}
			}

			BiMap<GuiComponent, UUID> paperComponents = HashBiMap.create();

			ComponentVoid fakePaper = new ComponentVoid(180, 19, 180, 188);
			fakePaper.getTransform().setTranslateZ(100);
			getMainComponents().add(fakePaper);

			ComponentVoid bookIconMask = new ComponentVoid(0, -100, 180, 100);
			ComponentSprite bookIcon = new ComponentSprite(new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/book.png")), (int) ((bookIconMask.getSize().getX() / 2.0) - 16), (int) (bookIconMask.getSize().getY() + 50), 32, 32);
			{
				bookIcon.getTransform().setTranslateZ(200);
				bookIconMask.clipping.setClipToBounds(true);
				bookIconMask.getTransform().setTranslateZ(10);
				fakePaper.add(bookIconMask);

				bookIconMask.add(bookIcon);

				final Vec2d originalPos = bookIcon.getPos();
				KeyframeAnimation<ComponentSprite> anim = new KeyframeAnimation<>(bookIcon, "pos.y");
				anim.setDuration(100);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, originalPos.getY(), Easing.linear),
						new Keyframe(0.4f, (bookIconMask.getSize().getY() / 2.0) - 25, Easing.easeInBack),
						new Keyframe(0.5f, (bookIconMask.getSize().getY() / 2.0) - 10, Easing.easeOutBack),
						new Keyframe(0.8f, (bookIconMask.getSize().getY() / 2.0) - 10, Easing.easeInBack),
						new Keyframe(1f, originalPos.getY(), Easing.easeInBack)
				});

				ScheduledEventAnimation scheduled = new ScheduledEventAnimation(100, fakePaper::invalidate);

				bookIcon.add(anim, scheduled);
			}

			for (GuiComponent component : this.paperComponents.keySet()) {
				Module module = getModule(component);
				if (module == null) continue;

				ComponentSprite plate = new ComponentSprite(TableModule.plate, component.getPos().getXi(), component.getPos().getYi(), component.getSize().getXi(), component.getSize().getYi());
				fakePaper.add(plate);
				plate.setData(Vec2d.class, "", plate.getPos());
				plate.addTag(module);

				Sprite icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + module.getID() + ".png"));
				ComponentSprite iconComp = new ComponentSprite(icon, 2, 2, 12, 12);
				plate.add(iconComp);

				paperComponents.put(plate, this.paperComponents.get(component));

				//--- RENDER WIRE ---//
				plate.BUS.hook(GuiComponentEvents.PreDrawEvent.class, (event1) -> {

					UUID linkedUuid = componentLinks.get(paperComponents.get(event1.component));

					GuiComponent component1 = paperComponents.inverse().get(linkedUuid);
					if (component1 == null) return;
					if (linkedUuid == paperComponents.get(event1.component)) return;

					Vec2d toPos = component1.thisPosToOtherContext(event1.component, new Vec2d(8, 8));

					Vec2d fromPos = new Vec2d(8, 8);

					Module module1 = getModule(component1);
					if (module1 == null) return;

					Module module2 = getModule(event1.component);
					if (module2 == null) return;

					TableModule.drawWire(fromPos, toPos, TableModule.getColorForModule(module2.getModuleType()), TableModule.getColorForModule(module1.getModuleType()));
				});
				//--- RENDER WIRE ---//

				Vec2d random = plate.getPos().add(RandUtil.nextDouble(-50, 50), RandUtil.nextDouble(-50, 50));

				float delay = RandUtil.nextFloat(0, 0.3f);

				ScheduledEventAnimation scheduled = new ScheduledEventAnimation(80, plate::invalidate);

				KeyframeAnimation<ComponentSprite> animX = new KeyframeAnimation<>(plate, "pos.x");
				animX.setDuration(80);
				animX.setKeyframes(new Keyframe[]{
						new Keyframe(delay, plate.getPos().getX(), Easing.linear),
						new Keyframe(0.45f, random.getX(), Easing.easeOutQuart),
						new Keyframe(0.55f, random.getX(), Easing.linear),
						new Keyframe(1f, (bookIconMask.getSize().getX() / 2.0) - 8, Easing.easeInOutQuint)

				});

				KeyframeAnimation<ComponentSprite> animY = new KeyframeAnimation<>(plate, "pos.y");
				animY.setDuration(80);
				animY.setKeyframes(new Keyframe[]{
						new Keyframe(delay, plate.getPos().getY(), Easing.linear),
						new Keyframe(0.45f, random.getY(), Easing.easeOutQuart),
						new Keyframe(0.55f, random.getY(), Easing.linear),
						new Keyframe(1f, -(bookIconMask.getSize().getY() / 2.0) - 6, Easing.easeInOutQuint)

				});
				plate.add(scheduled, animX, animY);
			}
		});
		getMainComponents().add(save);

		whitelistedModifiers = new ComponentWhitelistedModifiers(this, 384, save.getPos().getYi() + save.getSize().getYi() + 8, 80, 170);
		whitelistedModifiers.getTransform().setTranslateZ(20);
		getMainComponents().add(whitelistedModifiers);
	}

	public void sync() {
		if (!Minecraft.getMinecraft().world.isBlockLoaded(worktablePos)) return;
		IBlockState state = Minecraft.getMinecraft().world.getBlockState(worktablePos);

		if (state.getBlock() != ModBlocks.MAGICIANS_WORKTABLE) return;

		TileEntity table = Minecraft.getMinecraft().world.getTileEntity(worktablePos);
		if (!(table instanceof TileMagiciansWorktable)) return;

		HashMap<SpellRing, UUID> convertComponents = new HashMap<>();

		for (Map.Entry<GuiComponent, UUID> entrySet : paperComponents.entrySet()) {
			Module module1 = getModule(entrySet.getKey());
			if (module1 == null) continue;
			SpellRing ring = new SpellRing(module1);

			ring.getInformationTag().setDouble("worktable_x", entrySet.getKey().getPos().getX());
			ring.getInformationTag().setDouble("worktable_y", entrySet.getKey().getPos().getY());
			convertComponents.put(ring, entrySet.getValue());
		}

		((TileMagiciansWorktable) table).componentLinks = componentLinks;
		((TileMagiciansWorktable) table).paperComponents = convertComponents;

		PacketHandler.NETWORK.sendToServer(new PacketWorktableUpdate(Minecraft.getMinecraft().world.provider.getDimension(), worktablePos, convertComponents, componentLinks));
	}

	public UUID getUUID(GuiComponent component) {
		return paperComponents.get(component);
	}

	public GuiComponent getComponent(UUID uuid) {
		return paperComponents.inverse().get(uuid);
	}

	private void compileModule(ArrayList<Module> stream, @Nullable GuiComponent component) {
		if (component == null) return;

		Module module = getModule(component);
		if (module == null) return;

		stream.add(module);
		for (Module modifier : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
			if (!(modifier instanceof ModuleModifier)) continue;
			if (component.hasData(Integer.class, modifier.getID())) {
				int x = component.getData(Integer.class, modifier.getID());
				for (int i = 0; i < x; i++) {
					stream.add(modifier);
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

		ArrayList<GuiComponent> tmp = new ArrayList<>();
		for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
			TableModule item = new TableModule(this, parent, module, false, true);
			tmp.add(item.component);
		}

		//ArrayList<GuiComponent> scrollable = (ArrayList<GuiComponent>) Utils.getVisibleComponents(tmp, 0);
		for (GuiComponent component : tmp) {
			grid.add(component);
		}
		return grid;
	}

	private void addScrollbar(ComponentSprite parent, ComponentGrid gridView, int x, int y, ModuleType type, int trackSize) {
		ComponentSprite scrollBar = new ComponentSprite(SCROLL_BAR_GRIP, x, y, 5, 80);
		ComponentSprite bar = new ComponentSprite(SCROLL_BAR, 1, 0, 3, 11);

		int moduleCount = ModuleRegistry.INSTANCE.getModules(type).size();

		scrollBar.BUS.hook(GuiComponentEvents.MouseDragEvent.class, (event) -> {
			if (!event.component.getMouseOver() && !parent.getMouseOver()) return;
			for (GuiComponent comp : paperComponents.keySet()) if (comp.hasTag("dragging")) return;


			float contentSize = (float) ((moduleCount / 3.0) * 16.0);
			float windowSize = trackSize;
			float windowContentRatio = windowSize / contentSize;
			float gripSize = MathHelper.clamp(trackSize * windowContentRatio, SCROLL_BAR_GRIP.getHeight(), gridView.getSize().getYi());
			float windowScrollAreaSize = contentSize - windowSize;
			float windowPosition = 100;
			float windowPositionRatio = windowPosition / windowScrollAreaSize;
			float trackScrollAreaSize = trackSize - gripSize;
			float gripPositionOnTrack = trackScrollAreaSize * windowPositionRatio;
			float mousePositionDelta = event.getMousePos().getYi();
			float newGripPosition = MathHelper.clamp(gripPositionOnTrack + mousePositionDelta, 0, trackScrollAreaSize);
			float newGripPositionRatio = newGripPosition / trackScrollAreaSize;
			windowPosition = newGripPositionRatio * windowScrollAreaSize;

			float percent = windowPosition / contentSize;

			ArrayList<GuiComponent> compTmp = new ArrayList<>(gridView.getChildren());
			compTmp.forEach(gridView.relationships::remove);

			ArrayList<GuiComponent> tmp = new ArrayList<>();
			for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
				TableModule item = new TableModule(this, parent, module, false, false);
				tmp.add(item.component);
			}
			ArrayList<GuiComponent> scrollable = (ArrayList<GuiComponent>) Utils.getVisibleComponents(tmp, percent);
			for (GuiComponent component : scrollable) {
				gridView.add(component);
			}
		});
		scrollBar.BUS.hook(GuiComponentEvents.MouseWheelEvent.class, (event) -> {
			if (!event.component.getMouseOver() && !parent.getMouseOver()) return;
			for (GuiComponent comp : paperComponents.keySet()) if (comp.hasTag("dragging")) return;

			int dir = event.getDirection().ydirection * -16;
			double barPos = bar.getPos().getY();
			double clamp = MathHelper.clamp(barPos + dir, 0, (gridView.getSize().getY() - 1) - 11);

			bar.setPos(new Vec2d(bar.getPos().getX(), clamp));
			double percent = MathHelper.clamp(clamp / ((gridView.getSize().getY() - 1) - 11), 0, 1);

			ArrayList<GuiComponent> compTmp = new ArrayList<>(gridView.getChildren());
			compTmp.forEach(gridView.relationships::remove);

			ArrayList<GuiComponent> tmp = new ArrayList<>();
			for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
				TableModule item = new TableModule(this, parent, module, false, false);
				tmp.add(item.component);
			}
			ArrayList<GuiComponent> scrollable = (ArrayList<GuiComponent>) Utils.getVisibleComponents(tmp, percent);
			for (GuiComponent component : scrollable) {
				gridView.add(component);
			}
		});
		scrollBar.add(bar);
		getMainComponents().add(scrollBar);
	}

	@Nullable
	public Module getModule(@Nullable GuiComponent component) {
		if (component == null) return null;
		for (Object object : component.getTagList()) {
			if (object instanceof Module) return ((Module) object);
		}
		return null;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
