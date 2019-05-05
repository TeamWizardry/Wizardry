package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandGenPearl extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "genpearl";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "wizardry.command." + getName() + ".usage";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) {
		Entity entity = sender.getCommandSenderEntity();
		if (entity instanceof EntityPlayerMP) {
			ItemStack item = new ItemStack(ModItems.PEARL_NACRE);


			List<ItemStack> recipe = new ArrayList<>();
			recipe.add(ModuleRegistry.INSTANCE.getModules(ModuleType.SHAPE).get(RandUtil.nextInt(ModuleRegistry.INSTANCE.getModules(ModuleType.SHAPE).size() - 1)).getItemStack());
			recipe.add(ModuleRegistry.INSTANCE.getModules(ModuleType.EFFECT).get(RandUtil.nextInt(ModuleRegistry.INSTANCE.getModules(ModuleType.EFFECT).size() - 1)).getItemStack());
			recipe.add(new ItemStack(ModItems.DEVIL_DUST));

			SpellBuilder builder = new SpellBuilder(recipe, 1);

			NBTTagList list = new NBTTagList();
			for (SpellRing spellRing : builder.getSpell())
				list.appendTag(spellRing.serializeNBT());
			ItemNBTHelper.setList(item, Constants.NBT.SPELL, list);
			ItemNBTHelper.setBoolean(item, "infused", true);

			((EntityPlayerMP) entity).addItemStackToInventory(item);
			((EntityPlayerMP) entity).openContainer.detectAndSendChanges();
		} else notifyCommandListener(sender, this, "wizardry.command.notplayer");
	}
}
