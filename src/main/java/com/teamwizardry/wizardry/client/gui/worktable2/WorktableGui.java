package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketSendSpellToBook;
import com.teamwizardry.wizardry.init.ModItems;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

	static final Sprite TABLE_SPRITE = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table.png"));
	static final Sprite SIDE_BAR_SHORT = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar.png"));
	static final Sprite SIDE_BAR_LONG = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar_long.png"));
	static final Sprite BUTTON_NORMAL = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button.png"));
	static final Sprite BUTTON_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_highlighted.png"));
	static final Sprite BUTTON_PRESSED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_pressed.png"));
	static final Sprite PLATE = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate.png"));
	static final Sprite PLATE_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate_highlighted.png"));
	static final Sprite STREAK = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/streak.png"));
	static final Sprite BOOK_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/book.png"));
	final ComponentModifiers modifiers;
	@Nullable
	public TableModule selectedModule = null;
	final ComponentVoid paper;
	final ComponentText toast;
	public float backgroundAlpha = 0f;
	private ComponentSprite tableComponent;
	public boolean animationPlaying = false;
	private boolean hadBook = false, bookWarnRevised = false;

	public WorktableGui() {
		super(480, 224);

		// GRAY OUT BACKGROUND
		ComponentRect grayBackground = new ComponentRect(0, 0, 40000, 40000);
		grayBackground.getColor().setValue(new Color(0.05f, 0.05f, 0.05f, 0.8f));
		grayBackground.getTransform().setTranslateZ(-20);
		getFullscreenComponents().add(grayBackground);

		// TABLE
		tableComponent = new ComponentSprite(TABLE_SPRITE, 0, 0, 480, 224);
		getMainComponents().add(tableComponent);

		// PAPER PLATE
		paper = new ComponentVoid(181, 22, 180, 184);
		tableComponent.add(paper);

		// --- TOAST BOX --- //
		toast = new ComponentText(384, 139, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		toast.setSize(new Vec2d(80, 69).mul(2));
		toast.clipping.setClipToBounds(true);
		toast.getTransform().setScale(0.5f);
		toast.getWrap().setValue(160);
		tableComponent.add(toast);

		setCodexToastMessage();
		// --- TOAST BOX --- //

		toast.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			if (!bookWarnRevised && !hadBook && Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				toast.getColor().setValue(Color.GREEN);
				toast.getText().setValue("Codex found, you can save spells.");
				bookWarnRevised = true;
			}
		});

		// --- SIDEBAR MODULES --- //
		ComponentSprite shapes = new ComponentSprite(SIDE_BAR_SHORT, 29, 31, 48, 80);
		addModules(shapes, ModuleType.SHAPE);
		tableComponent.add(shapes);

		ComponentSprite effects = new ComponentSprite(SIDE_BAR_LONG, 93, 37, 48, 160);
		addModules(effects, ModuleType.EFFECT);
		tableComponent.add(effects);

		ComponentSprite events = new ComponentSprite(SIDE_BAR_SHORT, 29, 123, 48, 80);
		addModules(events, ModuleType.EVENT);
		tableComponent.add(events);
		// --- SIDEBAR MODULES --- //

		modifiers = new ComponentModifiers(this);
		tableComponent.add(modifiers);

		// --- SAVE BUTTON --- //
		{
			ComponentSprite save = new ComponentSprite(BUTTON_NORMAL, 395, 30, (int) (88 / 1.5), (int) (24 / 1.5));

			// Button rendering
			{
				String saveStr = LibrarianLib.PROXY.translate("wizardry.misc.save");
				int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(saveStr);
				int fitWidth = save.getSize().getXi() - 16;

				ComponentText textSave = new ComponentText(16 + (int) (fitWidth / 2.0 - stringWidth / 2.0), (save.getSize().getYi() / 2), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
				textSave.getText().setValue(saveStr);
				save.add(textSave);

				ComponentSprite sprite = new ComponentSprite(BOOK_ICON, 2, 0);
				save.add(sprite);
			}

			save.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				if (!animationPlaying && !Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
					txt.add(TextFormatting.RED + LibrarianLib.PROXY.translate("wizardry.misc.save_error"));
				}
				return txt;
			});

			save.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
				if (animationPlaying || !Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
					save.setSprite(BUTTON_PRESSED);
				} else {
					if (event.component.getMouseOver())
						save.setSprite(BUTTON_HIGHLIGHTED);
					else save.setSprite(BUTTON_NORMAL);
				}
			});

			save.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
				if (animationPlaying || !Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK)))
					return;

				List<List<Module>> chains = new ArrayList<>();
				for (TableModule head : getSpellHeads()) {
					List<Module> chain = new ArrayList<>();

					TableModule lastModule = head;

					while (lastModule != null) {
						chain.add(lastModule.getModule());

						for (Module module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
							if (!(module instanceof ModuleModifier)) continue;
							if (!lastModule.hasData(Integer.class, module.getID())) continue;

							int count = lastModule.getData(Integer.class, module.getID());

							for (int i = 0; i < count; i++) {
								chain.add(module);
							}
						}

						lastModule = lastModule.getLinksTo();
					}

					chains.add(chain);
				}

				for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
					if (stack.getItem() == ModItems.BOOK) {
						int slot = Minecraft.getMinecraft().player.inventory.getSlotFor(stack);
						PacketHandler.NETWORK.sendToServer(new PacketSendSpellToBook(slot, chains));
					}
				}

				setToastMessage("Spell saved to codex successfully! Open the book and check the new spell recipe tab.", Color.GREEN);
				playAnimation();
			});
			getMainComponents().add(save);
		}
		// --- SAVE BUTTON --- //
	}

	public void setCodexToastMessage() {
		if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
			hadBook = false;
			toast.getColor().setValue(Color.RED);
			toast.getText().setValue("You do not have a codex in your inventory! Once you have one, you can save your spell into the book.");
		} else {
			hadBook = true;
			toast.getColor().setValue(Color.GREEN);
			toast.getText().setValue("Codex found, you can save spells.");
		}
	}

	public String getToastHeader() {
		return TextFormatting.YELLOW.toString() + getSpellName() + TextFormatting.RESET + "\n\n";
	}

	public String getSpellName() {
		StringBuilder builder = new StringBuilder();
		Deque<TableModule> heads = new ArrayDeque<>(getSpellHeads());

		while (!heads.isEmpty()) {

			TableModule lastModule = heads.pop();

			while (lastModule != null) {
				builder.append(lastModule.getModule().getReadableName());
				lastModule = lastModule.getLinksTo();

				if (lastModule != null)
					builder.append(" > ");
			}

			if (!heads.isEmpty()) {
				builder.append(" | ");
			}
		}
		return builder.toString();
	}

	public Pair<String, Color> getToastMessage() {
		String text = toast.getText().getValue(toast);
		Color color = toast.getColor().getValue(toast);
		return new Pair<>(text, color);
	}

	public void setToastMessage(String text, Color color) {
		toast.getText().setValue(getToastHeader() + text);
		toast.getColor().setValue(color);
	}

	private Set<TableModule> getSpellHeads() {
		Set<TableModule> set = new HashSet<>();

		for (GuiComponent child : paper.getChildren()) {
			if (!(child instanceof TableModule)) continue;
			TableModule childModule = (TableModule) child;

			if (childModule.getLinksTo() != null) {
				boolean linkedToSomehow = false;
				for (GuiComponent subChild : paper.getChildren()) {
					if (!(subChild instanceof TableModule)) continue;
					TableModule subChildModule = (TableModule) subChild;
					if (subChildModule == childModule) continue;

					if (subChildModule.getLinksTo() == childModule) {
						linkedToSomehow = true;
						break;
					}
				}
				if (!linkedToSomehow) {
					set.add(childModule);
				}
			}
		}

		return set;
	}

	private void addModules(ComponentSprite parent, ModuleType type) {
		int column = 0, row = 0;
		for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
			TableModule tableModule = new TableModule(this, module, false, false);
			tableModule.setPos(new Vec2d(row * 16, column * 16));
			parent.add(tableModule);

			if (++row >= 3) {
				row = 0;
				column++;
			}
		}
	}

	public void playAnimation() {
		animationPlaying = true;

		if (selectedModule != null) {
			Vec2d toSize = new Vec2d(16, 16);
			BasicAnimation<TableModule> animSize = new BasicAnimation<>(selectedModule, "size");
			animSize.setDuration(5);
			animSize.setEasing(Easing.easeOutCubic);
			animSize.setTo(toSize);
			selectedModule.add(animSize);

			BasicAnimation<TableModule> animPos = new BasicAnimation<>(selectedModule, "pos");
			animPos.setDuration(5);
			animPos.setEasing(Easing.easeOutCubic);
			animPos.setTo(selectedModule.getPos().add((selectedModule.getSize().sub(toSize)).mul(0.5f)));
			selectedModule.add(animPos);
		}

		Runnable runnable = () -> {
			ComponentVoid fakePaper = new ComponentVoid(180, 19, 180, 188);
			fakePaper.getTransform().setTranslateZ(100);
			getMainComponents().add(fakePaper);

			ComponentVoid bookIconMask = new ComponentVoid(0, -100, 180, 100);
			fakePaper.add(bookIconMask);

			// GRAY BACKGROUND
			{
				ComponentRect grayBackground = new ComponentRect(0, 0, tableComponent.getSize().getXi(), tableComponent.getSize().getYi());
				grayBackground.getColor().setValue(new Color(0.05f, 0.05f, 0.05f, 0f));
				grayBackground.getTransform().setTranslateZ(200);
				grayBackground.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
					grayBackground.getColor().setValue(new Color(0.05f, 0.05f, 0.05f, backgroundAlpha));
				});
				tableComponent.add(grayBackground);

				KeyframeAnimation<WorktableGui> anim = new KeyframeAnimation<>(this, "backgroundAlpha");
				anim.setDuration(100);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, 0f, Easing.easeInOutQuint),
						new Keyframe(0.2f, 0.65f, Easing.easeInOutQuint),
						new Keyframe(0.7f, 0.65f, Easing.easeInOutQuint),
						new Keyframe(1f, 0f, Easing.easeInOutQuint)
				});
				anim.setCompletion(grayBackground::invalidate);
				getMainComponents().add(anim);
			}

			// BOOK PEAK ANIMATION
			{
				ComponentSprite bookIcon = new ComponentSprite(BOOK_ICON, (int) ((bookIconMask.getSize().getX() / 2.0) - 16), (int) (bookIconMask.getSize().getY() + 50), 32, 32);
				bookIconMask.add(bookIcon);

				bookIcon.getTransform().setTranslateZ(200);
				bookIconMask.clipping.setClipToBounds(true);
				bookIconMask.getTransform().setTranslateZ(250);

				final Vec2d originalPos = bookIcon.getPos();
				KeyframeAnimation<ComponentSprite> anim = new KeyframeAnimation<>(bookIcon, "pos.y");
				anim.setDuration(120);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, originalPos.getY(), Easing.linear),
						new Keyframe(0.4f, (bookIconMask.getSize().getY() / 2.0) - 25, Easing.easeInBack),
						new Keyframe(0.5f, (bookIconMask.getSize().getY() / 2.0) - 10, Easing.easeOutBack),
						new Keyframe(0.8f, (bookIconMask.getSize().getY() / 2.0) - 10, Easing.easeInBack),
						new Keyframe(1f, originalPos.getY(), Easing.easeInBack)
				});

				anim.setCompletion(() -> {
					fakePaper.invalidate();
					animationPlaying = false;
				});

				bookIcon.add(anim);

			}

			// PAPER ITEMS ANIMATION
			{

				HashMap<TableModule, UUID> links = new HashMap<>();

				for (GuiComponent component : paper.getChildren()) {
					if (!(component instanceof TableModule)) continue;
					TableModule tableModule = (TableModule) component;

					TableModule fakeModule = new TableModule(this, tableModule.getModule(), false, true);
					fakeModule.setPos(tableModule.getPos());
					for (Object tag : tableModule.getTagList()) fakeModule.addTag(tag);
					fakeModule.getTransform().setTranslateZ(230);
					fakePaper.add(fakeModule);

					UUID uuid = tableModule.getData(UUID.class, "uuid");
					if (uuid != null)
						fakeModule.setData(UUID.class, "uuid", uuid);

					TableModule linkedModule = tableModule.getLinksTo();
					if (linkedModule == null) continue;

					UUID linkTo = linkedModule.getData(UUID.class, "uuid");
					if (linkTo != null)
						links.put(fakeModule, linkTo);
				}

				for (TableModule module : links.keySet()) {
					if (!links.containsKey(module)) continue;

					UUID linkTo = links.get(module);

					if (linkTo == null) continue;

					for (GuiComponent child : fakePaper.getChildren()) {
						UUID uuid = child.getData(UUID.class, "uuid");
						if (uuid == null) continue;

						if (!linkTo.equals(uuid)) continue;

						if (!(child instanceof TableModule)) continue;
						TableModule reverseUUIDLink = (TableModule) child;

						module.setLinksTo(reverseUUIDLink);
						break;
					}
				}

				for (GuiComponent component : fakePaper.getChildren()) {
					if (!(component instanceof TableModule)) continue;
					TableModule fakeModule = (TableModule) component;

					Vec2d random = fakeModule.getPos().add(RandUtil.nextDouble(-10, 10), RandUtil.nextDouble(-10, 10));

					float delay = RandUtil.nextFloat(0.2f, 0.3f);

					KeyframeAnimation<TableModule> animX = new KeyframeAnimation<>(fakeModule, "pos.x");
					animX.setDuration(100);
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(delay, fakeModule.getPos().getX() + 1, Easing.easeOutQuint),
							new Keyframe(0.45f, random.getX(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getX(), Easing.easeOutQuint),
							new Keyframe(1f, (bookIconMask.getSize().getX() / 2.0) - 8, Easing.easeInOutQuint)

					});

					KeyframeAnimation<TableModule> animY = new KeyframeAnimation<>(fakeModule, "pos.y");
					animY.setDuration(100);
					animY.setKeyframes(new Keyframe[]{
							new Keyframe(delay, fakeModule.getPos().getY() + 3, Easing.easeOutQuint),
							new Keyframe(0.45f, random.getY(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getY(), Easing.easeOutQuint),
							new Keyframe(1f, -(bookIconMask.getSize().getY() / 2.0) - 4, Easing.easeInOutQuint)

					});

					animY.setCompletion(fakeModule::invalidate);

					fakeModule.add(animX, animY);
				}
			}
		};

		if (selectedModule != null) {
			selectedModule = null;
			getMainComponents().add(new ScheduledEventAnimation(5, runnable));
		} else runnable.run();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
