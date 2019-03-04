package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellRingCache;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.TeleportUtil;
import com.teamwizardry.wizardry.common.network.PacketSyncModules;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.teamwizardry.wizardry.common.core.EventHandler.fallResetter;


/**
 * Created by Demoniaque.
 */
public class CommandWizardry extends CommandBase {

	@Nonnull
	@Override
	public String getName() {
		return "wizardry";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender) {
		return "wizardry.command.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
		if (args.length < 1) throw new WrongUsageException(getUsage(sender));
		if (args[0].equalsIgnoreCase("help")){
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "All commands are prefixed with /wizardry");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "tpunderworld:");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Teleports you to the Underworld, but requires that you be standing on Bedrock");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "tptorikki:");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Teleports you to Torikki");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "genpearl:");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Generates a new pearl based on the arguments passed in the order <>, <>, <>");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "genstaff");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "See genpearl, but pops it in a " + TextFormatting.ITALIC +"BRAND NEW STAFF");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "reload");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Reloads all Wizardry Spell Modules");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "reset");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Resets all Wizardry Spell Modules");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "debug");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Outputs all information about a spell module");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "listmodules");
			notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Outputs a list of all Spell Modules");

		}
		else if (args[0].equalsIgnoreCase("tpunderworld")) {
			Entity entity = sender.getCommandSenderEntity();
			if (entity instanceof EntityPlayerMP) {
				EntityPlayer player = ((EntityPlayer) entity);

				BlockPos location = player.getPosition();
				BlockPos bedrock = PosUtils.checkNeighborBlocksThoroughly(player.getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					fallResetter.add(player.getUniqueID());
					TeleportUtil.teleportToDimension(player, Wizardry.underWorld.getId(), 0, 300, 0);
					player.addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));
				}
				else{
					notifyCommandListener(sender,this,TextFormatting.LIGHT_PURPLE + "Error: You are not standing on Bedrock!");
				}
			}
		}

			else if (args[0].equalsIgnoreCase("tptorikki")) {
			Entity entity = sender.getCommandSenderEntity();
				if (entity instanceof EntityPlayerMP) {
					EntityPlayer player = ((EntityPlayer) entity);
					BlockPos location = player.getPosition();
					fallResetter.add(player.getUniqueID());
					TeleportUtil.teleportToDimension(player, Wizardry.torikki.getId(), 0, 300, 0);
				}

			}

			else if (args[0].equalsIgnoreCase("genpearl") || args[0].equalsIgnoreCase("genstaff")) {
			Entity entity = sender.getCommandSenderEntity();
			if (entity instanceof EntityPlayerMP) {

				ItemStack item;
				if (args[0].equalsIgnoreCase("genstaff")) {
					item = new ItemStack(ModItems.STAFF);
					item.setItemDamage(1);
				} else item = new ItemStack(ModItems.PEARL_NACRE);

				List<ItemStack> recipe = new ArrayList<>();
				recipe.add(ModuleRegistry.INSTANCE.getModules(ModuleType.SHAPE).get(RandUtil.nextInt(ModuleRegistry.INSTANCE.getModules(ModuleType.SHAPE).size() - 1)).getItemStack());
				recipe.add(ModuleRegistry.INSTANCE.getModules(ModuleType.EFFECT).get(RandUtil.nextInt(ModuleRegistry.INSTANCE.getModules(ModuleType.EFFECT).size() - 1)).getItemStack());
				recipe.add(new ItemStack(ModItems.DEVIL_DUST));

				SpellBuilder builder = new SpellBuilder(recipe, 1);

				NBTTagList list = new NBTTagList();
				for (SpellRing spellRing : builder.getSpell()) {
					list.appendTag(spellRing.serializeNBT());
				}
				ItemNBTHelper.setList(item, Constants.NBT.SPELL, list);
				ItemNBTHelper.setBoolean(item, "infused", true);

				((EntityPlayerMP) entity).addItemStackToInventory(item);
				((EntityPlayerMP) entity).openContainer.detectAndSendChanges();

			} else {
				notifyCommandListener(sender, this, "TODO: You're not a player.");
			}

		} else if (args[0].equalsIgnoreCase("reload")) {
			SpellRingCache.INSTANCE.clear();
			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.loadOverrideDefaults();
			ModuleRegistry.INSTANCE.copyMissingModules(CommonProxy.directory);
			ModuleRegistry.INSTANCE.loadModules(CommonProxy.directory);

			if (server.isDedicatedServer()) {
				PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));
			}

			notifyCommandListener(sender, this, "wizardry.command.success");

		} else if (args[0].equalsIgnoreCase("reset")) {
			notifyCommandListener(sender, this, "wizardry.command.reset");

			File moduleDirectory = new File(CommonProxy.directory, "modules");
			if (moduleDirectory.exists()) {

				File[] files = moduleDirectory.listFiles();
				if (files != null)
					for (File file : files) {
						String name = file.getName();
						if (!file.delete()) {
							throw new CommandException("wizardry.command.fail", name);
						} else {
							notifyCommandListener(sender, this, "wizardry.command.success_delete", name);
						}
					}

				if (!moduleDirectory.delete()) {
					throw new CommandException("wizardry.command.fail_dir_delete");

				}
			}
			if (!moduleDirectory.exists())
				if (!moduleDirectory.mkdirs()) {
					throw new CommandException("wizardry.command.fail_dir_create");
				}

			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.copyMissingModules(CommonProxy.directory);
			ModuleRegistry.INSTANCE.loadModules(CommonProxy.directory);

			if (server.isDedicatedServer()) {
				PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));
			}

			notifyCommandListener(sender, this, "wizardry.command.success");

		} else if (args[0].equalsIgnoreCase("listModules")) {

			notifyCommandListener(sender, this, TextFormatting.YELLOW + " ________________________________________________\\\\");
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " | " + TextFormatting.GRAY + "Module List");
			for (ModuleInstance module : ModuleRegistry.INSTANCE.modules) {
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " | |_ " + TextFormatting.GREEN + module.getSubModuleID() + TextFormatting.RESET + ": " + TextFormatting.GRAY + module.getReadableName());
			}
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |________________________________________________//");

		} else if (args[0].equalsIgnoreCase("debug")) {
			if (args.length < 2) throw new WrongUsageException(getUsage(sender));

			ModuleInstance module = ModuleRegistry.INSTANCE.getModule(args[1]);

			if (module == null) {
				notifyCommandListener(sender, this, "Module not found.");
				return;
			}

			notifyCommandListener(sender, this, TextFormatting.YELLOW + " ________________________________________________\\\\");
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " | " + TextFormatting.GRAY + "Module " + module.getReadableName() + ":");
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Description           " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getDescription());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Item Stack            " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getItemStack().getDisplayName());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Burnout Fill          " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getBurnoutFill());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_ " + TextFormatting.DARK_GREEN + "Burnout Multiplier" + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getBurnoutMultiplier());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Mana Drain           " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getManaDrain());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_" + TextFormatting.DARK_GREEN + "Mana Multiplier     " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getManaMultiplier());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Power Multiplier     " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getPowerMultiplier());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Charge Up Time      " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getChargeupTime());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Cooldown Time        " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getCooldownTime());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Primary Color        " + TextFormatting.GRAY + " | " + TextFormatting.RED + module.getPrimaryColor().getRed() + TextFormatting.GRAY + ", " + TextFormatting.GREEN + module.getPrimaryColor().getGreen() + TextFormatting.GRAY + ", " + TextFormatting.BLUE + module.getPrimaryColor().getBlue());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Secondary Color    " + TextFormatting.GRAY + " | " + TextFormatting.RED + module.getSecondaryColor().getRed() + TextFormatting.GRAY + ", " + TextFormatting.GREEN + module.getSecondaryColor().getGreen() + TextFormatting.GRAY + ", " + TextFormatting.BLUE + module.getSecondaryColor().getBlue());

			if (!module.getAttributes().isEmpty())
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Default AttributeRegistry");
			for (AttributeModifier attributeModifier : module.getAttributes())
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_ " + TextFormatting.GRAY + attributeModifier.toString());

			ModuleInstanceModifier[] modifierList = module.applicableModifiers();
			if (modifierList != null) {
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Applicable Modifiers ");
				for (ModuleInstanceModifier modifier : modifierList)
					notifyCommandListener(sender, this, TextFormatting.YELLOW + " |     |_ " + TextFormatting.DARK_GREEN + modifier.getSubModuleID());
			}
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |________________________________________________//");
		} else {
			throw new WrongUsageException(getUsage(sender));
		}
	}
}
