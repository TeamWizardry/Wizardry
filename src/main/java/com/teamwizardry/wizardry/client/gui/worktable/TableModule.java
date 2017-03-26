package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.lib.LibSprites;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableModule {

	private static Set<ComponentSprite> modules = new HashSet<>();

	public ComponentSprite component;
	private int i = 0;

	public TableModule(WorktableGui table, Module module, boolean draggable) {
		ComponentSprite sprite = new ComponentSprite(LibSprites.Worktable.MODULE_DEFAULT, 0, 0, 12, 12);
		sprite.addTag(module.getID());

		ComponentSprite glow = new ComponentSprite(LibSprites.Worktable.MODULE_DEFAULT_GLOW, 0, 0, 12, 12);
		glow.setVisible(false);
		sprite.add(glow);

		// TODO: The thing's icon here
		ComponentSprite icon = new ComponentSprite(LibSprites.Worktable.MODULE_DEFAULT, 2, 2, 8, 8);
		sprite.add(icon);

		sprite.BUS.hook(GuiComponent.MouseInEvent.class, (event) -> {
			glow.setVisible(true);
		});

		sprite.BUS.hook(GuiComponent.MouseOutEvent.class, (event) -> {
			glow.setVisible(false);
		});

		sprite.BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!draggable && sprite.getMouseOver()) {
					TableModule item = new TableModule(table, module, true);
					item.component.addTag("on_paper");
					table.paper.add(item.component);

					DragMixin<ComponentSprite> drag = new DragMixin<>(item.component, vec2d -> vec2d);
					drag.setClickPos(new Vec2d(6, 6));
					drag.setMouseDown(event.getButton());
					event.cancel();
				}
			}
		});

		sprite.BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.RIGHT) {

				ComponentModuleLine line = new ComponentModuleLine(sprite.getPos(), event.getMousePos());
				line.setEnabled(false);

				DragMixin<ComponentModuleLine> drag = new DragMixin<>(line, vec2d -> vec2d);
				drag.setClickPos(new Vec2d(6, 6));
				drag.setMouseDown(event.getButton());

				line.BUS.hook(DragMixin.DragMoveEvent.class, (event2) -> {
					line.set(sprite.getPos(), event2.getMousePos());
				});

				line.BUS.hook(DragMixin.DragDropEvent.class, (event2) -> {
					for (ComponentSprite comp : modules) {
						if (comp.getMouseOver()) {
							if (!sprite.hasData(ComponentModuleLine.class, comp.hashCode() + "")
									&& !comp.hasData(ComponentModuleLine.class, sprite.hashCode() + "")) {
								ComponentModuleLine line2 = new ComponentModuleLine(sprite.getPos(), comp.getPos());
								line2.setEnabled(false);
								sprite.setData(ComponentModuleLine.class, comp.hashCode() + "", line2);
								table.paper.add(line2);

							} else {
								if (sprite.hasData(ComponentModuleLine.class, comp.hashCode() + "")) {
									ComponentModuleLine remove = sprite.getData(ComponentModuleLine.class, comp.hashCode() + "");
									if (remove != null) {
										table.paper.remove(remove);
										remove.invalidate();
										sprite.removeData(ComponentModuleLine.class, comp.hashCode() + "");
									}
								}
								if (comp.hasData(ComponentModuleLine.class, sprite.hashCode() + "")) {
									ComponentModuleLine remove = comp.getData(ComponentModuleLine.class, sprite.hashCode() + "");
									if (remove != null) {
										table.paper.remove(remove);
										remove.invalidate();
										sprite.removeData(ComponentModuleLine.class, comp.hashCode() + "");
									}
								}

							}

							break;
						}
					}

					line.invalidate();
					sprite.removeData(ComponentModuleLine.class, "dragging");
				});

				sprite.setData(ComponentModuleLine.class, "dragging", line);
				table.paper.add(line);

				event.cancel();
			}
		});

		sprite.BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				Vec2d size = table.paper.getSize();
				Vec2d pos = event.getComponent().getPos();
				boolean b = pos.getX() >= 0 && pos.getX() <= size.getX() && pos.getY() >= 0 && pos.getY() <= size.getY();
				if (!b) sprite.invalidate();

			} else if (event.getButton() == EnumMouseButton.RIGHT) {
			}
		});

		/*sprite.BUS.hook(DragMixin.DragMoveEvent.class, (event) -> {
			if (event.getComponent().getMouseOver()) {
				if (event.getButton() == EnumMouseButton.RIGHT) {
					ComponentModuleLine line = sprite.getData(ComponentModuleLine.class, "dragging");
					if (line != null)
						line.set(event.getComponent().getPos(), event.getMousePos());
				}
			}
		});*/

		sprite.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
			if (event.getComponent().getMouseOver()) {
				List<String> txt = new ArrayList<>();
				txt.add(TextFormatting.GOLD + module.getReadableName());
				event.getComponent().setTooltip(txt);
			}
		});

		component = sprite;

		modules.add(sprite);
	}
}
