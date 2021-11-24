//package com.teamwizardry.wizardry.client.gui;
//
//import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
//import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
//import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
//import com.teamwizardry.librarianlib.facade.pastry.layers.ScrollPane;
//import com.teamwizardry.librarianlib.math.Rect2d;
//import com.teamwizardry.librarianlib.math.Vec2d;
//import net.minecraft.util.math.MathHelper;
//
//import static com.teamwizardry.wizardry.client.gui.WorktableGUI.SPRITE_BUTTON_PRESSED;
//import static com.teamwizardry.wizardry.client.gui.WorktableGUI.SPRITE_SCROLL_BUTTON;
//
//public class WScrollPane extends GuiLayer {
//
//	private static final int scrollBarWidth = 12;
//	private final ScrollPane scrollPane = new ScrollPane();
//	private final SpriteLayer horizontalBackground = new SpriteLayer(SPRITE_BUTTON_PRESSED);
//	private final SpriteLayer horizontalHandleBackground = new SpriteLayer(SPRITE_SCROLL_BUTTON);
//	private final SpriteLayer horizontalHandleDashes = new SpriteLayer(SPRITE_SCROLL_BUTTON);
//
//	public WScrollPane(int x, int y, int width, int height) {
//		super(x, y, width, height);
//		this.add(horizontalBackground);
//		this.add(scrollPane, scrollPane.getHorizontalScrollBar());
//
//		scrollPane.getHorizontalScrollBar().getHandle().add(horizontalHandleBackground);
//		horizontalHandleDashes.setAnchor(new Vec2d(0.5, 0.5));
//
//		scrollPane.getHorizontalScrollBar().getHandle().BUS.hook(GuiLayerEvents.LayoutChildren.class, this::layoutHorizontalHandle);
//		scrollPane.BUS.hook(GuiLayerEvents.LayoutChildren.class, layoutChildren -> {
//			layoutChildren();
//		});
//	}
//
//	private void layoutHorizontalHandle(GuiLayerEvents.LayoutChildren event) {
//		GuiLayer handle = scrollPane.getHorizontalScrollBar().getHandle();
//		horizontalHandleBackground.setFrame(handle.getBounds());
//
//		int dashWidth = MathHelper.clamp(((handle.getWidthi() - 4) / 3), 0, 5) * 3;
//		horizontalHandleDashes.setFrame(new Rect2d((handle.getWidth() - dashWidth) / 2, 1, dashWidth, handle.getHeight() - 2));
//	}
//
//	@Override
//	public void layoutChildren() {
//		super.layoutChildren();
//
//		Vec2d contentSize = scrollPane.getContent().getFrame().getSize();
//
//		boolean needsHorizontal = true; //contentSize.getX() > this.getWidth() - 2;
//
//		Vec2d contentAreaSize = new Vec2d(this.getWidth(), this.getHeight());
//
//		scrollPane.setFrame(new Rect2d(0, 0, contentAreaSize.getX(), contentAreaSize.getY()));
//
//		scrollPane.getHorizontalScrollBar().setVisible(needsHorizontal);
//		horizontalBackground.setVisible(needsHorizontal);
//
//		horizontalBackground.setFrame(new Rect2d(0, this.getHeight() - scrollBarWidth, contentAreaSize.getX(), scrollBarWidth));
//		scrollPane.getHorizontalScrollBar().setFrame(horizontalBackground.getFrame().shrink(1.0));
//	}
//
//	public GuiLayer getContent() {
//		return scrollPane.getContent();
//	}
//}
