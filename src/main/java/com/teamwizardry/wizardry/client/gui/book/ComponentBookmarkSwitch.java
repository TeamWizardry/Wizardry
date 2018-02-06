package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.mixin.ScissorMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.client.renderer.GlStateManager;

import java.util.HashSet;

import static com.teamwizardry.wizardry.client.gui.book.BookGui.*;

public class ComponentBookmarkSwitch extends GuiComponent {

	private static HashSet<ComponentBookmarkSwitch> bookmarks = new HashSet<>();
	public float tickX = -56;
	boolean hardSwitch = false;

	public ComponentBookmarkSwitch(Vec2d pos, BookGui bookGui, GuiComponent parent, GuiComponent link, int listIndex, String title, boolean switchSoftly, boolean defaultActive, boolean isParentHidable, boolean isLinkHidable) {
		super(pos.getXi(), pos.getYi(), 200, 300);

		bookmarks.add(this);

		if (switchSoftly) {
			parent.add(link);
		} else {
			bookGui.componentBook.add(link);
		}

		if (!defaultActive) {
			link.setVisible(false);
		} else {
			bookGui.activeComponent = link;
			if (switchSoftly) {
				parent.addTag("switched");
			} else {
				hardSwitch = true;
			}

			KeyframeAnimation<ComponentBookmarkSwitch> anim = new KeyframeAnimation<>(this, "tickX");
			anim.setDuration(5);
			anim.setKeyframes(new Keyframe[]{
					new Keyframe(0, -56, Easing.linear),
					new Keyframe(1f, 0, Easing.easeOutQuart)
			});
			add(anim);
		}

		ComponentVoid bookmark = new ComponentVoid(453, 15 + listIndex * 25, BOOKMARK_EXTENDED_SWITCH.getWidth(), BOOKMARK_EXTENDED_SWITCH.getHeight());
		add(bookmark);

		ComponentText bookmarkText = new ComponentText((int) (BOOKMARK_SWITCH.getWidth() / 2.0), (int) (BOOKMARK_SWITCH.getHeight() / 2.0), ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE);
		bookmarkText.getTransform().setScale(2);
		bookmarkText.getText().setValue(title);
		bookmarkText.getTransform().setTranslateZ(100);
		bookmark.add(bookmarkText);

		ScissorMixin.INSTANCE.scissor(bookmark);
		bookmark.BUS.hook(GuiComponentEvents.PostDrawEvent.class, (event) -> {
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);

			if (switchSoftly) {
				BOOKMARK_EXTENDED_SWITCH.getTex().bind();
				BOOKMARK_EXTENDED_SWITCH.draw((int) event.getPartialTicks(), tickX, 0);
			} else {
				BOOKMARK_EXTENDED.getTex().bind();
				BOOKMARK_EXTENDED.draw((int) event.getPartialTicks(), tickX, 0);
			}
			GlStateManager.popMatrix();
		});

		bookmark.BUS.hook(GuiComponentEvents.MouseInEvent.class, (event) -> {
			if (bookGui.activeComponent != link && !parent.hasTag("switched")) {
				KeyframeAnimation<ComponentBookmarkSwitch> anim = new KeyframeAnimation<>(this, "tickX");
				anim.setDuration(5);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, tickX, Easing.linear),
						new Keyframe(1f, -40, Easing.easeOutQuart)
				});
				add(anim);
			}
		});

		bookmark.BUS.hook(GuiComponentEvents.MouseOutEvent.class, (event) -> {
			if (bookGui.activeComponent != link && !parent.hasTag("switched")) {
				KeyframeAnimation<ComponentBookmarkSwitch> anim = new KeyframeAnimation<>(this, "tickX");
				anim.setDuration(5);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, tickX, Easing.linear),
						new Keyframe(1f, -56, Easing.easeOutQuart)
				});
				add(anim);
			}
		});

		bookmark.BUS.hook(GuiComponentEvents.AddTagEvent.class, (event) -> {
			for (ComponentBookmarkSwitch comp : bookmarks) {

				if (bookGui.activeComponent != event.component) {
					if (comp.hasTag("hardSwitched")) {
						comp.removeTag("hardSwitched");
					}
					if (comp.hardSwitch) {
						comp.hardSwitch = false;
					}
					if (comp.hasTag("switched")) {
						comp.removeTag("switched");
					}
				}

				KeyframeAnimation<ComponentBookmarkSwitch> anim = new KeyframeAnimation<>(comp, "tickX");
				anim.setDuration(5);
				anim.setKeyframes(new Keyframe[]{
						new Keyframe(0, comp.tickX, Easing.linear),
						new Keyframe(1f, -56, Easing.easeOutQuart)
				});
				add(anim);
			}
		});

		bookmark.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
			if (event.component.getMouseOver()) {

				int status = 0;

				if (switchSoftly) {
					if (parent.hasTag("switched")) {
						if (isLinkHidable) link.setVisible(false);
						if (isParentHidable) parent.removeTag("switched");
						status = -1;
					} else {
						if (isParentHidable) parent.addTag("switched");
						if (isLinkHidable) link.setVisible(true);
						status = 1;
					}
				} else {
					if (bookGui.activeComponent != link) {
						if (isParentHidable) parent.setVisible(false);
						if (isLinkHidable) link.setVisible(true);
						if (bookGui.activeComponent != null) bookGui.activeComponent.setVisible(false);
						bookGui.activeComponent = link;
						hardSwitch = true;
						event.component.addTag("hardSwitched");
						status = 1;
					}
				}

				if (status != 0) {
					bookGui.componentBookSearch.invalidate();
				}

				if (status == 1) {
					KeyframeAnimation<ComponentBookmarkSwitch> anim = new KeyframeAnimation<>(this, "tickX");
					anim.setDuration(5);
					anim.setKeyframes(new Keyframe[]{
							new Keyframe(0, tickX, Easing.linear),
							new Keyframe(1f, 0, Easing.easeOutQuart)
					});
					add(anim);
				} else if (status == -1) {
					KeyframeAnimation<ComponentBookmarkSwitch> anim = new KeyframeAnimation<>(this, "tickX");
					anim.setDuration(5);
					anim.setKeyframes(new Keyframe[]{
							new Keyframe(0, tickX, Easing.linear),
							new Keyframe(1f, -40, Easing.easeOutQuart)
					});
					add(anim);
				}
			}
		});
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
