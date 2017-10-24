package com.teamwizardry.wizardry.api.util;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class Utils {

	public static boolean hasOreDictPrefix(ItemStack stack, String dict) {
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			if (OreDictionary.getOreName(id).length() >= dict.length()) {
				if (OreDictionary.getOreName(id).substring(0, dict.length()).compareTo(dict.substring(0, dict.length())) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<GuiComponent> getVisibleComponents(List<GuiComponent> objects, double scroll) {
		final int ROWS = 3;
		final int COLS = 5;
		final int SPACES = ROWS * COLS;

		if (objects.size() <= ROWS * COLS) return objects;

		int rows = MathHelper.ceil((objects.size() - SPACES) / ((double) COLS));
		double rowsScrolled = scroll * rows;

		int startIndex = ((int) rowsScrolled) * COLS;

		int endIndex = startIndex + SPACES;

		if (startIndex < 0)
			startIndex = 0;

		if (endIndex > objects.size())
			endIndex = objects.size();

		List<GuiComponent> visible = new ArrayList<>();
		visible.addAll(objects.subList(startIndex, endIndex));

		return visible;
	}
	
	public static int getSlotFor(EntityPlayer player, ItemStack stack)
	{
		for (int i = 0; i < player.inventory.mainInventory.size(); ++i)
        {
            if (!((ItemStack)player.inventory.mainInventory.get(i)).isEmpty() && stackEqualExact(stack, player.inventory.mainInventory.get(i)))
            {
                return i;
            }
        }

        return -1;
	}
	
    public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
    
    public static void boom(World worldIn, Entity entity) {
		List<Entity> entityList = worldIn.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.getPosition()).grow(32, 32, 32));
		for (Entity entity1 : entityList) {
			double dist = entity1.getDistanceToEntity(entity);
			final double upperMag = 3;
			final double scale = 0.8;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);
			Vec3d dir = entity1.getPositionVector().subtract(entity.getPositionVector()).normalize().scale(mag);

			entity1.motionX += (dir.x);
			entity1.motionY += (dir.y);
			entity1.motionZ += (dir.z);
			entity1.fallDistance = 0;
			entity1.velocityChanged = true;

			if (entity1 instanceof EntityPlayerMP)
				((EntityPlayerMP) entity1).connection.sendPacket(new SPacketEntityVelocity(entity1));
		}
	}
}
