package com.teamwizardry.wizardry.client.gui.worktable;

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
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.CommonWorktableModule;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketSendSpellToBook;
import com.teamwizardry.wizardry.common.network.PacketSyncWorktable;
import com.teamwizardry.wizardry.common.tile.TileMagiciansWorktable;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
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
	static final Sprite BUTTON_SHORT_NORMAL = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_short.png"));
	static final Sprite BUTTON_SHORT_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_short_highlighted.png"));
	static final Sprite BUTTON_SHORT_PRESSED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_short_pressed.png"));
	static final Sprite PLATE = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate.png"));
	static final Sprite PLATE_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate_highlighted.png"));
	static final Sprite PLATE_HIGHLIGHTED_ERROR = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate_highlighted_error.png"));
	static final Sprite STREAK = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/streak.png"), 16, 16);
	static final Sprite BOOK_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/book.png"));
	static final Sprite SAVE_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/save.png"));
	static final Sprite BROOM_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/broom.png"));
	static final Sprite BOOK_COVER_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/book_cover.png"));

	final ComponentModifiers modifiers;
	final ComponentVoid paper;
	final ComponentText toast;
	@Nullable
	public TableModule selectedModule = null;
	public float backgroundAlpha = 0f;
	public boolean animationPlaying = false;
	private ComponentSprite tableComponent;
	private boolean hadBook = false, bookWarnRevised = false;
	protected Set<CommonWorktableModule> commonModules = new HashSet<>();
	private List<List<ModuleInstance>> chains = new ArrayList<>();
	private boolean canBeSaved = false;

	@NotNull
	private final BlockPos pos;

	public WorktableGui(@Nonnull BlockPos pos) {
		super(480, 224);
		this.pos = pos;

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
		toast = new ComponentText(384, 56, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		toast.setSize(new Vec2d(80, 63).mul(2));
		toast.clipping.setClipToBounds(true);
		toast.getTransform().setScale(0.5f);
		toast.getWrap().setValue(160);
		tableComponent.add(toast);

		setCodexToastMessage();
		// --- TOAST BOX --- //

		toast.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			if (!bookWarnRevised && !hadBook && Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				toast.getColor().setValue(Color.GREEN);
				toast.getText().setValue(LibrarianLib.PROXY.translate("wizardry.table.codex_found"));
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

		int menuX = 384;
		int menuY = 20;
		int menuWidth = 80;
		int buttonWidth = 20;
		int buttonHeight = 16;
		int buttonX = 390;
		int buttonY = 25;
		int spacing = 5;
		int iconSize = 12;
		int iconSizeHovered = 16;
		// --- SAVE BUTTON --- //
		{
			ComponentSprite save = new ComponentSprite(BUTTON_SHORT_NORMAL, menuX + (menuWidth / 2) - buttonWidth - (buttonWidth / 2) - spacing, spacing + menuY, buttonWidth, buttonHeight);

			ComponentSprite sprite = new ComponentSprite(SAVE_ICON, (buttonWidth / 2) - (iconSize / 2), (buttonHeight / 2) - (iconSize / 2), iconSize, iconSize);
			save.add(sprite);

			save.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				if (!animationPlaying) {
					txt.add(TextFormatting.GOLD + LibrarianLib.PROXY.translate("wizardry.table.save"));
					txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.table.save_desc"));

					if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
						txt.add(TextFormatting.RED + LibrarianLib.PROXY.translate("wizardry.table.save_error"));
					}

					if (!canBeSaved) {
						txt.add(TextFormatting.RED + "Invalid Spell!");
					}
				}
				return txt;
			});

			save.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
				if (!canBeSaved || animationPlaying || !Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
					save.setSprite(BUTTON_SHORT_PRESSED);
				} else {
					if (event.component.getMouseOver())
						save.setSprite(BUTTON_SHORT_HIGHLIGHTED);
					else save.setSprite(BUTTON_SHORT_NORMAL);
				}
			});

			save.BUS.hook(GuiComponentEvents.MouseDownEvent.class, event -> {
				if (!animationPlaying && event.component.getMouseOver())
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_IN, 1f, 1f);
			});

			save.BUS.hook(GuiComponentEvents.MouseUpEvent.class, event -> {
				if (!animationPlaying && event.component.getMouseOver())
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_OUT, 1f, 1f);
			});

			paper.BUS.hook(TableModule.ModuleUpdateEvent.class, event -> {
				commonModules.clear();
				chains.clear();

				for (TableModule head : getSpellHeads()) {
					List<ModuleInstance> chain = new ArrayList<>();

					CommonWorktableModule lastCommonModule = new CommonWorktableModule(head.hashCode(), head.getModule(), head.getPos(), null, new HashMap<>());
					commonModules.add(lastCommonModule);
					chain.add(head.getModule());

					TableModule linksTo = head.getLinksTo();
					while (linksTo != null) {
						if (linksTo.isInvalid()) continue;
						chain.add(linksTo.getModule());

						CommonWorktableModule commonModule = new CommonWorktableModule(linksTo.hashCode(), linksTo.getModule(), linksTo.getPos(), null, new HashMap<>());
						lastCommonModule.setLinksTo(commonModule);
						lastCommonModule = commonModule;

						for (ModuleInstance module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
							if (!(module instanceof ModuleInstanceModifier)) continue;
							if (!linksTo.hasData(Integer.class, module.getNBTKey())) continue;

							int count = linksTo.getData(Integer.class, module.getNBTKey());

							for (int i = 0; i < count; i++) {
								chain.add(module);
							}

							lastCommonModule.addModifier((ModuleInstanceModifier) module, count);
						}

						linksTo = linksTo.getLinksTo();
					}

					chains.add(chain);
				}

				canBeSaved = true;

				primary:
				for (GuiComponent paperComponent : paper.getChildren()) {
					if (!(paperComponent instanceof TableModule)) continue;
					TableModule module = (TableModule) paperComponent;
					if (module.isInvalid()) continue;

					if (module.getLinksTo() == null) {
						for (GuiComponent subPaperComponent : paper.getChildren()) {
							if (!(subPaperComponent instanceof TableModule)) continue;
							TableModule subModule = (TableModule) subPaperComponent;
							if (subModule.isInvalid()) continue;
							if (subModule == module) continue;

							if (subModule.getLinksTo() == module) {
								continue primary;
							}
						}

						module.setErrored(true);
						TableModule.select(module);
						canBeSaved = false;
					}
				}

				if (!canBeSaved) {
					setToastMessage("Component is not linked to anything! Link it to something to make it function properly.", Color.RED);
					return;
				}

				primary:
				for (GuiComponent paperComponent : paper.getChildren()) {
					if (!(paperComponent instanceof TableModule)) continue;
					TableModule module = (TableModule) paperComponent;
					if (module.isInvalid()) continue;

					if (module.getModule().getModuleType() != ModuleType.SHAPE) {

						for (GuiComponent subPaperComponent : paper.getChildren()) {
							if (!(subPaperComponent instanceof TableModule)) continue;
							TableModule subModule = (TableModule) subPaperComponent;
							if (subModule.isInvalid()) continue;
							if (subModule == module) continue;

							if (subModule.getLinksTo() == module) continue primary;
						}

						module.setErrored(true);
						TableModule.select(module);
						canBeSaved = false;
					}
				}
				if (!canBeSaved) {
					setToastMessage("Spell chain starts without a Shape component! Your spell needs to start with a shape to run properly.", Color.RED);
					return;
				}

				boolean shapeLinkedFromNothing = false;
				for (CommonWorktableModule module : commonModules) {

					CommonWorktableModule lastModule = module;

					while (lastModule != null) {

						if (lastModule.module.getModuleType() == ModuleType.SHAPE) {
							if (lastModule.linksTo == null) {
								canBeSaved = false;
								setToastMessage("Shape component is not linked to anything! It needs to link to something so you can run your spell properly.", Color.RED);

							} else {
								boolean isLinkedFrom = false;
								for (CommonWorktableModule linkedFrom : commonModules) {
									if (linkedFrom.linksTo == lastModule) {
										isLinkedFrom = true;
										break;
									}
								}
								if (!isLinkedFrom)
									shapeLinkedFromNothing = true;
							}
						} else if (lastModule.module.getModuleType() == ModuleType.EVENT) {
							if (lastModule.linksTo == null) {
								canBeSaved = false;
								setToastMessage("Event is linked to nothing. Link it to an effect to use it properly.", Color.RED);
							}
						}

						lastModule = lastModule.linksTo;
					}
				}

				if (!canBeSaved) return;

				if (!shapeLinkedFromNothing) {
					canBeSaved = false;
					setToastMessage("No spell starting with a shape found. Start a spell with a shape to be able to run it properly.", Color.RED);
				}

				if (canBeSaved) {
					setToastMessage("Spell is valid!", Color.GREEN);
				}
			});

			save.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
				if (!canBeSaved || animationPlaying || !Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK)))
					return;

				NBTTagList commonList = new NBTTagList();
				for (CommonWorktableModule commonModule : commonModules) {
					commonList.appendTag(commonModule.serializeNBT());
				}
				//	for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
				//		if (stack.getItem() == ModItems.BOOK) {
				//			NBTHelper.setList(stack, "common_modules", commonList);
				//		}
				//	}

				PacketHandler.NETWORK.sendToServer(new PacketSendSpellToBook(chains, commonModules));

				playSaveAnimation(null);
			});
			getMainComponents().add(save);
		}
		// --- SAVE BUTTON --- //

		// --- LOAD BUTTON --- //
		{
			ComponentSprite load = new ComponentSprite(BUTTON_SHORT_NORMAL, menuX + (menuWidth / 2) - (buttonWidth / 2), spacing + menuY, buttonWidth, buttonHeight);

			String saveStr = LibrarianLib.PROXY.translate("wizardry.table.load");
			int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(saveStr);
			int fitWidth = load.getSize().getXi() - 16;

			ComponentText textSave = new ComponentText(16 + (int) (fitWidth / 2.0 - stringWidth / 2.0), (load.getSize().getYi() / 2), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
			textSave.getText().setValue(saveStr);
			//	load.add(textSave);

			ComponentSprite sprite = new ComponentSprite(BOOK_COVER_ICON, (buttonWidth / 2) - (iconSize / 2), (buttonHeight / 2) - (iconSize / 2), iconSize, iconSize);
			load.add(sprite);

			load.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				if (!animationPlaying) {
					txt.add(TextFormatting.GOLD + LibrarianLib.PROXY.translate("wizardry.table.load"));
					txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.table.load_desc"));
				}
				return txt;
			});

			load.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
				if (animationPlaying) {
					load.setSprite(BUTTON_SHORT_PRESSED);
				} else {
					if (event.component.getMouseOver())
						load.setSprite(BUTTON_SHORT_HIGHLIGHTED);
					else load.setSprite(BUTTON_SHORT_NORMAL);
				}
			});

			load.BUS.hook(GuiComponentEvents.MouseDownEvent.class, event -> {
				if (!animationPlaying && event.component.getMouseOver())
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_IN, 1f, 1f);
			});

			load.BUS.hook(GuiComponentEvents.MouseUpEvent.class, event -> {
				if (!animationPlaying && event.component.getMouseOver())
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_OUT, 1f, 1f);
			});

			load.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
				if (animationPlaying) return;

				NBTTagList commonList = null;
				for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
					if (stack.getItem() == ModItems.BOOK) {

						NBTTagList list = NBTHelper.getList(stack, "common_modules", NBTTagCompound.class);
						if (list == null || list.isEmpty()) continue;

						commonList = list;

					}
				}
				if (commonList == null) return;

				Set<CommonWorktableModule> commonModules = new HashSet<>();

				for (NBTBase base : commonList) {
					if (base instanceof NBTTagCompound) {
						NBTTagCompound compound = (NBTTagCompound) base;

						CommonWorktableModule commonModule = CommonWorktableModule.deserailize(compound);
						commonModules.add(commonModule);
					}
				}

				boolean isEmpty = true;
				for (GuiComponent child : paper.getChildren()) {
					if (child instanceof TableModule) {
						isEmpty = false;
						break;
					}
				}


				if (isEmpty) {
					playLoadAnimation(commonModules, () -> {
						syncToServer();
						animationPlaying = false;
					});
				} else playClearAnimation(() -> playLoadAnimation(commonModules, () -> {
					syncToServer();
					animationPlaying = false;
				}));

				setToastMessageNoHeader(LibrarianLib.PROXY.translate("wizardry.table.spell_loaded"), Color.GREEN);
			});
			getMainComponents().add(load);
		}
		// --- LOAD BUTTON --- //

		// --- CLEAR BUTTON --- //
		{
			ComponentSprite clear = new ComponentSprite(BUTTON_SHORT_NORMAL, menuX + (menuWidth / 2) + (buttonWidth / 2) + spacing, spacing + menuY, buttonWidth, buttonHeight);

			String saveStr = LibrarianLib.PROXY.translate("wizardry.table.clear");
			int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(saveStr);
			int fitWidth = clear.getSize().getXi() - 16;

			ComponentText textSave = new ComponentText(16 + (int) (fitWidth / 2.0 - stringWidth / 2.0), (clear.getSize().getYi() / 2), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
			textSave.getText().setValue(saveStr);
			//	clear.add(textSave);

			ComponentSprite sprite = new ComponentSprite(BROOM_ICON, (buttonWidth / 2) - (iconSize / 2), (buttonHeight / 2) - (iconSize / 2), iconSize, iconSize);
			clear.add(sprite);

			clear.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				if (!animationPlaying) {
					txt.add(TextFormatting.GOLD + LibrarianLib.PROXY.translate("wizardry.table.clear"));
					txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.table.clear_desc"));
				}
				return txt;
			});

			clear.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
				if (animationPlaying) {
					clear.setSprite(BUTTON_SHORT_PRESSED);
				} else {
					if (event.component.getMouseOver())
						clear.setSprite(BUTTON_SHORT_HIGHLIGHTED);
					else clear.setSprite(BUTTON_SHORT_NORMAL);
				}
			});

			clear.BUS.hook(GuiComponentEvents.MouseDownEvent.class, event -> {
				if (!animationPlaying && event.component.getMouseOver())
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_IN, 1f, 1f);
			});

			clear.BUS.hook(GuiComponentEvents.MouseUpEvent.class, event -> {
				if (!animationPlaying && event.component.getMouseOver())
					Minecraft.getMinecraft().player.playSound(ModSounds.BUTTON_CLICK_OUT, 1f, 1f);
			});

			clear.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
				if (animationPlaying) return;

				playClearAnimation(() -> {
					syncToServer();
					animationPlaying = false;
				});

				setToastMessageNoHeader(LibrarianLib.PROXY.translate("wizardry.table.paper_cleared"), Color.GREEN);
			});
			getMainComponents().add(clear);
		}
		// --- CLEAR BUTTON --- //

		load();
		paper.BUS.fire(new TableModule.ModuleUpdateEvent());
	}

	public void load() {
		TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
		if (tile instanceof TileMagiciansWorktable) {
			if (((TileMagiciansWorktable) tile).commonModules != null) {
				Set<CommonWorktableModule> commonModules = ((TileMagiciansWorktable) tile).getCommonModules();

				for (CommonWorktableModule commonHead : commonModules) {
					CommonWorktableModule commonModule = commonHead;
					TableModule lastModule = new TableModule(this, commonHead.module, true, false);
					lastModule.setPos(commonHead.pos);

					while (commonModule != null) {

						lastModule.radius = 10;
						lastModule.textRadius = 0;
						paper.add(lastModule);
						DragMixin drag = new DragMixin(lastModule, vec2d -> vec2d);
						drag.setDragOffset(new Vec2d(6, 6));

						for (ModuleInstanceModifier modifier : commonModule.modifiers.keySet()) {
							lastModule.setData(Integer.class, modifier.getNBTKey(), commonModule.modifiers.get(modifier));
						}

						if (commonModule.linksTo != null) {
							TableModule childModule = new TableModule(this, commonModule.linksTo.module, true, false);
							childModule.setPos(commonModule.linksTo.pos);
							lastModule.setLinksTo(childModule);
							lastModule = childModule;
						}

						commonModule = commonModule.linksTo;
					}
				}
			}
		}
	}

	public void syncToServer() {
		Set<CommonWorktableModule> commonModules = new HashSet<>();
		for (TableModule head : getSpellHeads()) {

			TableModule lastModule = head;
			CommonWorktableModule lastCommonModule = new CommonWorktableModule(lastModule.hashCode(), lastModule.getModule(), lastModule.getPos(), null, new HashMap<>());
			commonModules.add(lastCommonModule);

			while (lastModule != null) {

				if (lastModule != head) {
					CommonWorktableModule commonModule = new CommonWorktableModule(lastModule.hashCode(), lastModule.getModule(), lastModule.getPos(), null, new HashMap<>());
					lastCommonModule.setLinksTo(commonModule);
					lastCommonModule = commonModule;
				}

				for (ModuleInstance module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
					if (!(module instanceof ModuleInstanceModifier)) continue;
					if (!lastModule.hasData(Integer.class, module.getNBTKey())) continue;

					int count = lastModule.getData(Integer.class, module.getNBTKey());

					lastCommonModule.addModifier((ModuleInstanceModifier) module, count);
				}

				lastModule = lastModule.getLinksTo();
			}
		}

		int dim = Minecraft.getMinecraft().world.provider.getDimension();
		PacketHandler.NETWORK.sendToServer(new PacketSyncWorktable(dim, pos, commonModules));
	}

	public void setCodexToastMessage() {
		if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
			hadBook = false;
			toast.getColor().setValue(Color.RED);
			toast.getText().setValue(LibrarianLib.PROXY.translate("wizardry.table.no_codex_found"));
		} else {
			hadBook = true;
			toast.getColor().setValue(Color.GREEN);
			toast.getText().setValue(LibrarianLib.PROXY.translate("wizardry.table.codex_found"));
		}
	}

	public String getToastHeader() {
		return TextFormatting.YELLOW.toString() + getSpellName() + TextFormatting.RESET + "\n\n";
	}

	public String getSpellName() {
		List<List<ModuleInstance>> chains = new ArrayList<>();
		for (TableModule head : getSpellHeads()) {
			List<ModuleInstance> chain = new ArrayList<>();

			TableModule lastModule = head;

			while (lastModule != null) {
				chain.add(lastModule.getModule());

				for (ModuleInstance module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
					if (!(module instanceof ModuleInstanceModifier)) continue;
					if (!lastModule.hasData(Integer.class, module.getNBTKey())) continue;

					int count = lastModule.getData(Integer.class, module.getNBTKey());

					for (int i = 0; i < count; i++) {
						chain.add(module);
					}
				}

				lastModule = lastModule.getLinksTo();
			}

			chains.add(chain);
		}

		SpellBuilder builder = new SpellBuilder(SpellUtils.getSpellItems(chains));
		StringBuilder spellNameBuilder = new StringBuilder();

		SpellRing lastRing = null;
		for (SpellRing ring : builder.getSpell()) {
			if (lastRing == null) lastRing = ring;
			if (ring != null) {
				if (ring != lastRing) spellNameBuilder.append(TextFormatting.GRAY).append(" | ");
				SpellRing tmpRing = ring;
				while (tmpRing != null) {
					spellNameBuilder
							.append(TextFormatting.YELLOW)
							.append(tmpRing.getModuleReadableName())
							.append(TextFormatting.GRAY)
							.append("(")
							.append(TextFormatting.BLUE)
							.append(Math.round(tmpRing.getManaDrain(null) * tmpRing.getManaMultiplier()))
							.append(TextFormatting.GRAY)
							.append("/")
							.append(TextFormatting.RED)
							.append(Math.round(tmpRing.getBurnoutFill(null) * tmpRing.getBurnoutMultiplier()))
							.append(TextFormatting.GRAY)
							.append(")");
					tmpRing = tmpRing.getChildRing();

					if (tmpRing != null) {
						spellNameBuilder.append(TextFormatting.GRAY).append(" > ");
					}
				}
			}
		}

		return spellNameBuilder.toString();
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

	public void setToastMessageNoHeader(String text, Color color) {
		toast.getText().setValue(text);
		toast.getColor().setValue(color);
	}

	private Set<TableModule> getSpellHeads() {
		Set<TableModule> set = new HashSet<>();

		for (GuiComponent child : paper.getChildren()) {
			if (!(child instanceof TableModule)) continue;
			TableModule childModule = (TableModule) child;

			if (childModule.isInvalid()) continue;

			boolean linkedToSomehow = false;
			for (GuiComponent subChild : paper.getChildren()) {
				if (subChild.isInvalid()) continue;
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

		return set;
	}

	private void addModules(ComponentSprite parent, ModuleType type) {
		int column = 0, row = 0;
		for (ModuleInstance module : ModuleRegistry.INSTANCE.getModules(type)) {
			TableModule tableModule = new TableModule(this, module, false, false);
			tableModule.setPos(new Vec2d(row * 16, column * 16));
			parent.add(tableModule);

			if (++row >= 3) {
				row = 0;
				column++;
			}
		}
	}

	public void playSaveAnimation(@Nullable Runnable finish) {
		animationPlaying = true;

		resetSelectedModule();

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

					if (finish == null)
						animationPlaying = false;
					else {
						finish.run();
					}
				});

				ScheduledEventAnimation animSound = new ScheduledEventAnimation(120 * 0.5f, () -> Minecraft.getMinecraft().player.playSound(ModSounds.SCRIBBLING, 1f, 1f));

				bookIcon.add(anim, animSound);

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

					for (ModuleInstance module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
						if (tableModule.hasData(Integer.class, module.getNBTKey())) {
							fakeModule.setData(Integer.class, module.getNBTKey(), tableModule.getData(Integer.class, module.getNBTKey()));
						}
					}

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

					Vec2d random = fakeModule.getPos().add(RandUtil.nextDouble(-20, 20), RandUtil.nextDouble(-20, 20));

					float delay = RandUtil.nextFloat(0.2f, 0.3f);
					float dur = RandUtil.nextFloat(70, 100);


					ScheduledEventAnimation animSound1 = new ScheduledEventAnimation(dur * delay, () -> Minecraft.getMinecraft().player.playSound(ModSounds.POP, 1f, 1f));

					ScheduledEventAnimation animSound2 = new ScheduledEventAnimation(dur * 0.75f, () -> Minecraft.getMinecraft().player.playSound(ModSounds.WHOOSH, 1f, 1f));

					KeyframeAnimation<TableModule> animX = new KeyframeAnimation<>(fakeModule, "pos.x");
					animX.setDuration(dur);
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(delay, fakeModule.getPos().getX(), Easing.easeOutQuint),
							new Keyframe(0.45f, random.getX(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getX(), Easing.easeOutQuint),
							new Keyframe(1f, (bookIconMask.getSize().getX() / 2.0) - 8, Easing.easeInOutQuint)
					});

					KeyframeAnimation<TableModule> animY = new KeyframeAnimation<>(fakeModule, "pos.y");
					animY.setDuration(dur);
					animY.setKeyframes(new Keyframe[]{
							new Keyframe(delay, fakeModule.getPos().getY(), Easing.easeOutQuint),
							new Keyframe(0.45f, random.getY(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getY(), Easing.easeOutQuint),
							new Keyframe(1f, -(bookIconMask.getSize().getY() / 2.0) - 4, Easing.easeInOutQuint)
					});

					BasicAnimation<TableModule> animRadius = new BasicAnimation<>(fakeModule, "radius");
					animRadius.setDuration(20);
					animRadius.setEasing(Easing.easeOutCubic);
					animRadius.setTo(0);

					BasicAnimation<TableModule> animText = new BasicAnimation<>(fakeModule, "textRadius");
					animText.setDuration(40);
					animText.setEasing(Easing.easeOutCubic);
					animText.setTo(0);

					animY.setCompletion(fakeModule::invalidate);

					fakeModule.add(animX, animY, animSound1, animSound2, animRadius, animText);
				}
			}
		};

		if (selectedModule != null) {
			selectedModule = null;
			getMainComponents().add(new ScheduledEventAnimation(5, runnable));
		} else runnable.run();
	}

	private void resetSelectedModule() {
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
	}

	public void playClearAnimation(@Nullable Runnable finish) {
		animationPlaying = true;

		resetSelectedModule();

		Runnable runnable = () -> {
			// PAPER ITEMS ANIMATION
			{
				ScheduledEventAnimation animFinish = new ScheduledEventAnimation(110, () -> {
					if (finish == null) {
						animationPlaying = false;
						syncToServer();
					} else finish.run();
				});
				paper.add(animFinish);

				for (GuiComponent component : paper.getChildren()) {
					if (!(component instanceof TableModule)) continue;
					TableModule module = (TableModule) component;

					Vec2d random = module.getPos().add(RandUtil.nextDouble(-20, 20), RandUtil.nextDouble(-20, 20));

					float delay = RandUtil.nextFloat(0.2f, 0.3f);
					float dur = RandUtil.nextFloat(70, 100);

					BasicAnimation<TableModule> animRadius = new BasicAnimation<>(module, "radius");
					animRadius.setDuration(20);
					animRadius.setEasing(Easing.easeOutCubic);
					animRadius.setTo(0);

					BasicAnimation<TableModule> animText = new BasicAnimation<>(module, "textRadius");
					animText.setDuration(40);
					animText.setEasing(Easing.easeOutCubic);
					animText.setTo(0);

					ScheduledEventAnimation animSound1 = new ScheduledEventAnimation(dur * delay, () -> Minecraft.getMinecraft().player.playSound(ModSounds.POP, 1f, 1f));

					KeyframeAnimation<TableModule> animX = new KeyframeAnimation<>(module, "pos.x");
					animX.setDuration(dur);
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(delay, module.getPos().getX(), Easing.easeOutQuint),
							new Keyframe(0.45f, random.getX(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getX(), Easing.easeOutQuint),
							new Keyframe(1f, random.getX() + RandUtil.nextDouble(-10, 10), Easing.easeInOutQuint)
					});

					KeyframeAnimation<TableModule> animY = new KeyframeAnimation<>(module, "pos.y");
					animY.setDuration(dur);
					animY.setKeyframes(new Keyframe[]{
							new Keyframe(delay, module.getPos().getY(), Easing.easeOutQuint),
							new Keyframe(0.45f, random.getY(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getY(), Easing.easeOutQuint),
							new Keyframe(1f, height + 100, Easing.easeInOutQuint)
					});

					animY.setCompletion(() -> {
						module.invalidate();
						Minecraft.getMinecraft().player.playSound(ModSounds.ZOOM, 1f, 1f);
					});

					module.add(animX, animY, animSound1, animRadius, animText);
				}
			}
		};

		if (selectedModule != null) {
			selectedModule = null;
			getMainComponents().add(new ScheduledEventAnimation(5, runnable));
		} else runnable.run();
	}

	public void playLoadAnimation(Set<CommonWorktableModule> commonModules, @Nullable Runnable finish) {
		animationPlaying = true;

		resetSelectedModule();

		ComponentVoid bookIconMask = new ComponentVoid(0, -100, 180, 100);
		paper.add(bookIconMask);

		Vec2d bookOrigin = new Vec2d((bookIconMask.getSize().getX() / 2.0) - 8, -(bookIconMask.getSize().getY() / 2.0) - 4);

		Runnable itemsRunnable = () -> {
			// PAPER ITEMS ANIMATION
			{
				for (CommonWorktableModule commonHead : commonModules) {
					CommonWorktableModule commonModule = commonHead;
					TableModule lastModule = new TableModule(this, commonHead.module, true, false);
					lastModule.setPos(commonHead.pos);

					while (commonModule != null) {

						paper.add(lastModule);
						DragMixin drag = new DragMixin(lastModule, vec2d -> vec2d);
						drag.setDragOffset(new Vec2d(6, 6));

						lastModule.setData(Vec2d.class, "true_pos", commonModule.pos);

						for (ModuleInstanceModifier modifier : commonModule.modifiers.keySet()) {
							lastModule.setData(Integer.class, modifier.getNBTKey(), commonModule.modifiers.get(modifier));
						}

						lastModule.radius = 0;
						lastModule.textRadius = 0;

						if (commonModule.linksTo != null) {
							TableModule childModule = new TableModule(this, commonModule.linksTo.module, true, false);
							childModule.setPos(bookOrigin);
							lastModule.setLinksTo(childModule);
							lastModule = childModule;
						}

						commonModule = commonModule.linksTo;
					}
				}

				for (GuiComponent component : paper.getChildren()) {
					if (!(component instanceof TableModule)) continue;
					TableModule module = (TableModule) component;

					Vec2d target = module.getData(Vec2d.class, "true_pos");
					if (target == null) {
						module.invalidate();
						continue;
					}

					Vec2d randGen = new Vec2d(RandUtil.nextDouble(-100, 100), RandUtil.nextDouble(-100, 100));
					Vec2d random = bookOrigin.add(randGen);

					float delay = RandUtil.nextFloat(0.2f, 0.3f);
					float dur = RandUtil.nextFloat(70, 100);

					ScheduledEventAnimation animSound1 = new ScheduledEventAnimation(dur * delay, () -> Minecraft.getMinecraft().player.playSound(ModSounds.WHOOSH, 1f, 1f));

					KeyframeAnimation<TableModule> animX = new KeyframeAnimation<>(module, "pos.x");
					animX.setDuration(dur);
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(delay, bookOrigin.getX(), Easing.easeOutQuint),
							new Keyframe(0.5f, random.getX(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getX(), Easing.easeOutQuint),
							new Keyframe(1f, target.getX(), Easing.easeOutQuint)
					});

					KeyframeAnimation<TableModule> animY = new KeyframeAnimation<>(module, "pos.y");
					animY.setDuration(dur);
					animY.setKeyframes(new Keyframe[]{
							new Keyframe(delay, bookOrigin.getY(), Easing.easeOutQuint),
							new Keyframe(0.5f, random.getY(), Easing.easeOutQuint),
							new Keyframe(0.6f, random.getY(), Easing.easeOutQuint),
							new Keyframe(1f, target.getY(), Easing.easeOutQuint)
					});

					animY.setCompletion(() -> {
						BasicAnimation<TableModule> animRadius = new BasicAnimation<>(module, "radius");
						animRadius.setDuration(20);
						animRadius.setEasing(Easing.easeOutCubic);
						animRadius.setTo(10);
						module.add(animRadius);

						BasicAnimation<TableModule> animText = new BasicAnimation<>(module, "textRadius");
						animText.setDuration(40);
						animText.setEasing(Easing.easeOutCubic);
						animText.setTo(0);
						module.add(animText);
					});

					module.add(animX, animY, animSound1);
				}
			}
		};

		Runnable runnable = () -> {

			// GRAY BACKGROUND
			{
				ComponentRect grayBackground = new ComponentRect(0, 0, tableComponent.getSize().getXi(), tableComponent.getSize().getYi());
				grayBackground.getColor().setValue(new Color(0.05f, 0.05f, 0.05f, 0f));
				grayBackground.getTransform().setTranslateZ(200);
				grayBackground.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
					grayBackground.getColor().setValue(new Color(0.05f, 0.05f, 0.05f, backgroundAlpha));
				});
				//	tableComponent.add(grayBackground);

				KeyframeAnimation<WorktableGui> anim = new KeyframeAnimation<>(this, "backgroundAlpha");
				anim.setDuration(100);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, 0f, Easing.easeInOutQuint),
						new Keyframe(0.2f, 0.65f, Easing.easeInOutQuint),
						new Keyframe(0.7f, 0.65f, Easing.easeInOutQuint),
						new Keyframe(1f, 0f, Easing.easeInOutQuint)
				});
				anim.setCompletion(grayBackground::invalidate);
				//	getMainComponents().add(anim);
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

				ScheduledEventAnimation animSound = new ScheduledEventAnimation(120 * 0.4f, () -> {
					Minecraft.getMinecraft().player.playSound(ModSounds.WHOOSH, 1f, 1f);
					itemsRunnable.run();

					ScheduledEventAnimation animFinish = new ScheduledEventAnimation(100, () -> {
						bookIcon.invalidate();
						if (finish == null)
							animationPlaying = false;
						else {
							finish.run();
						}
					});
					getMainComponents().add(animFinish);
				});

				bookIcon.add(anim, animSound);
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
