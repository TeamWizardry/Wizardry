package com.teamwizardry.wizardry.api.item.halo;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class HaloInfusionItem {

	private final ItemStack stack;
	private final String nbtName;

	public HaloInfusionItem(ItemStack stack) {
		this.stack = stack;
		this.nbtName = stack.getUnlocalizedName();
	}

	public HaloInfusionItem(Item item) {
		this(item, 1);
	}

	public HaloInfusionItem(Item item, int count) {
		this(new ItemStack(item, count));
	}

	public ItemStack getStack() {
		return stack;
	}

	public String getNbtName() {
		return nbtName;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public BiConsumer<Vec3d, World> getRenderer() {
		return HaloInfusionItemRenderers.getHaloRenderer(this);
	}

	@SideOnly(Side.CLIENT)
	public void render(World world, Vec3d pos) {
		if (getRenderer() != null)
			getRenderer().accept(pos, world);
	}
}
