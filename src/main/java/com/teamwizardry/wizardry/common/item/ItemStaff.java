package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.IContinousSpell;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Created by Saad on 6/7/2016.
 */
public class ItemStaff extends ItemWizardry implements INacreColorable {

	public ItemStaff() {
		super("staff", "staff", "staff_pearl");
		setMaxStackSize(1);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (getItemUseAction(stack) == EnumAction.NONE) {
			if (!world.isRemote) {
				SpellStack.runModules(stack, world, player, new Vec3d(player.posX, player.posY, player.posZ));
			}
			player.swingArm(EnumHand.MAIN_HAND);
			player.getCooldownTracker().setCooldown(this, 10);
			return new ActionResult<>(EnumActionResult.PASS, stack);
		} else {
			if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			} else {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.PASS, stack);
			}
		}
	}

	@NotNull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		for (Module module : SpellStack.getAllModules(stack))
			if (module instanceof IContinousSpell || (module.getChargeUpTime() > 0))
				return EnumAction.BOW;
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		int i = 0;
		for (Module module : SpellStack.getAllModules(stack))
			if (module instanceof IContinousSpell) {
				if (module.getChargeUpTime() == 0) i += 72000;
				else i += module.getChargeUpTime();
			} else i += module.getChargeUpTime();
		return i > 0 ? i : 72000;
	}

	@NotNull
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return EnumActionResult.PASS;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		for (Module module : SpellStack.getAllModules(stack))
			if (module.getChargeUpTime() > 0) {
				if (count <= 1) {
					SpellStack.runModules(stack, ((EntityPlayer) player).world, player, new Vec3d(player.posX, player.posY, player.posZ));
					player.swingArm(EnumHand.MAIN_HAND);
					((EntityPlayer) player).getCooldownTracker().setCooldown(this, 10);
					return;
				} else return;
			}
		if (((count > 0) && (count < (getMaxItemUseDuration(stack) - 20)) && (player instanceof EntityPlayer)))
			SpellStack.runModules(stack, ((EntityPlayer) player).world, player, new Vec3d(player.posX, player.posY, player.posZ));
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote) return false;

		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}

	@NotNull
	@Override
	public String getItemStackDisplayName(@NotNull ItemStack stack) {
		String finalName = null;
		Set<Module> modules = SpellStack.getModules(stack);
		for (Module module : modules) {
			if (module != null) {
				Module tempModule = module;
				while (tempModule != null) {
					if (tempModule.getModuleType() == ModuleType.EFFECT)
						if (finalName == null) finalName = tempModule.getReadableName();
						else finalName += " & " + tempModule.getReadableName();
					tempModule = tempModule.nextModule;
				}
			}
		}
		if (finalName == null)
			return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
		else return finalName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		Set<Module> modules = SpellStack.getModules(stack);
		for (Module module : modules) {
			if (module != null) {
				tooltip.add("Final " + TextFormatting.BLUE + "Mana" + TextFormatting.GRAY + "/" + TextFormatting.RED + "Burnout" + TextFormatting.GRAY + " Cost: " + TextFormatting.BLUE + module.finalManaCost + TextFormatting.GRAY + "/" + TextFormatting.RED + module.finalBurnoutCost);
				Module tempModule = module;
				int i = 0;
				while (tempModule != null) {
					tooltip.add(new String(new char[i]).replace("\0", "-") + "> " + TextFormatting.BLUE + tempModule.getManaToConsume() + TextFormatting.GRAY + "/" + TextFormatting.RED + tempModule.getBurnoutToFill() + TextFormatting.GRAY + " - " + tempModule.getReadableName());
					for (String key : tempModule.attributes.getKeySet())
						tooltip.add(new String(new char[i]).replace("\0", "-") + "^ " + TextFormatting.YELLOW + key + TextFormatting.GRAY + " * " + TextFormatting.GREEN + tempModule.attributes.getDouble(key));
					tempModule = tempModule.nextModule;
					i++;
				}
			}
		}
	}
}
