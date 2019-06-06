package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.SpellObjectManager;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellDataTypes.BlockStateCache;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCKSTATE_CACHE;

/**
 * Created by Demoniaque.
 */
public abstract class ModuleInstance {

	protected final String moduleNBTKey;
	protected final ModuleFactory createdByFactory;
	protected final IModule moduleClass;
	protected final ResourceLocation icon;
	protected final List<AttributeModifier> attributeModifiers = new ArrayList<>();
	protected Map<Attribute, AttributeRange> attributeRanges;
	protected Color primaryColor;
	protected Color secondaryColor;
	protected ItemStack itemStack;
	protected ModuleInstanceModifier[] applicableModifiers = null;

	@Nullable
	public static ModuleInstance deserialize(NBTTagString tagString) {
		return ModuleRegistry.INSTANCE.getModule(tagString.getString());
	}

	@Nullable
	public static ModuleInstance deserialize(String id) {
		return ModuleRegistry.INSTANCE.getModule(id);
	}

	protected ModuleInstance(IModule moduleClass,
	                         ModuleFactory createdByFactory,
	                         String moduleName,
	                         ResourceLocation icon,
	                         ItemStack itemStack,
	                         Color primaryColor,
	                         Color secondaryColor,
	                         DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		this.moduleClass = moduleClass;
		this.createdByFactory = createdByFactory;
		this.moduleNBTKey = moduleName;
		this.icon = icon;
		this.itemStack = itemStack;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.attributeRanges = attributeRanges;
	}

	static ModuleInstance createInstance(IModule moduleClass,
	                                     ModuleFactory createdByFactory,
	                                     String subModuleID,
	                                     ResourceLocation icon,
	                                     ItemStack itemStack,
	                                     Color primaryColor,
	                                     Color secondaryColor,
	                                     DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		if (moduleClass instanceof IModuleEffect)
			return new ModuleInstanceEffect((IModuleEffect) moduleClass, createdByFactory, subModuleID, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
		else if (moduleClass instanceof IModuleModifier)
			return new ModuleInstanceModifier((IModuleModifier) moduleClass, createdByFactory, subModuleID, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
		else if (moduleClass instanceof IModuleEvent)
			return new ModuleInstanceEvent((IModuleEvent) moduleClass, createdByFactory, itemStack, subModuleID, icon, primaryColor, secondaryColor, attributeRanges);
		else if (moduleClass instanceof IModuleShape)
			return new ModuleInstanceShape((IModuleShape) moduleClass, createdByFactory, itemStack, subModuleID, icon, primaryColor, secondaryColor, attributeRanges);
		else
			throw new UnsupportedOperationException("Unknown module type.");
	}

	public final IModule getModuleClass() {
		return this.moduleClass;
	}

	public final ModuleFactory getFactory() {
		return this.createdByFactory;
	}

	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	@Nonnull
	@SideOnly(Side.CLIENT)
	public SpellData renderVisualization(@Nonnull World world, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		return standardRenderVisualization(data, ring, partialTicks);
	}

	public final SpellData standardRenderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		return data;
	}

	@Nullable
	public final ItemStack getAvailableStack(Collection<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) continue;
			return stack;
		}
		return null;
	}

	/**
	 * Will return the blockstate if one is selected when shift right clicking on a block in the stack.
	 *
	 * @param caster gets the caster's held item for convenience.
	 */
	@Nullable
	public final IBlockState getSelectedBlockState(EntityLivingBase caster) {
		ItemStack hand = caster.getHeldItemMainhand();
		if (hand.isEmpty()) return null;
		if (NBTHelper.hasNBTEntry(hand, "selected")) {

			NBTTagCompound compound = NBTHelper.getCompound(hand, "selected");
			if (compound == null) return null;

			return NBTUtil.readBlockState(compound);
		}

		return null;
	}

	/**
	 * Gets all of the itemstacks in the player's inventory that match a given itemstack
	 * Modified to take a blockstate instead for convenience.
	 */
	public final List<ItemStack> getAllOfStackFromInventory(@Nonnull EntityPlayer player, @Nonnull IBlockState state) {
		List<ItemStack> stacks = new ArrayList<>();

		World world = player.world;
		ItemStack search = state.getBlock().getItem(world, null, state);

		if (search.isEmpty()) return stacks;

		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack == null || stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ItemBlock)) continue;

			if (!ItemStack.areItemsEqual(stack, search)) continue;

			stacks.add(stack);
		}

		return stacks;
	}

	/**
	 * Gets all of the itemstacks in the player's inventory that match a given itemstack
	 */
	public final List<ItemStack> getAllOfStackFromInventory(@Nonnull EntityPlayer player, @Nonnull ItemStack search) {
		List<ItemStack> stacks = new ArrayList<>();

		if (search.isEmpty()) return stacks;

		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack == null || stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ItemBlock)) continue;

			if (!ItemStack.areItemsEqual(stack, search)) continue;

			stacks.add(stack);
		}

		return stacks;
	}

	/**
	 * Convenience method for Module#getAllOfStackFromInventory
	 * Modified to take a blockstate instead for convenience.
	 */
	public final int getCountOfStackFromInventory(@Nonnull EntityPlayer player, @Nonnull IBlockState state) {
		World world = player.world;
		ItemStack search = state.getBlock().getItem(world, null, state);

		List<ItemStack> stacks = getAllOfStackFromInventory(player, search);
		int count = 0;
		for (ItemStack stack : stacks) {
			count += stack.getCount();
		}

		return count;
	}

	/**
	 * Convenience method for Module#getAllOfStackFromInventory
	 */
	public final int getCountOfStackFromInventory(@Nonnull EntityPlayer player, @Nonnull ItemStack search) {
		List<ItemStack> stacks = getAllOfStackFromInventory(player, search);
		int count = 0;
		for (ItemStack stack : stacks) {
			count += stack.getCount();
		}

		return count;
	}

	/**
	 * Convenience method for Module#getAllOfStackFromInventory
	 */
	public final int getCountOfStacks(@Nonnull Collection<ItemStack> stacks) {
		int count = 0;
		for (ItemStack stack : stacks) {
			count += stack.getCount();
		}

		return count;
	}

	/**
	 * The type of module this module is.
	 *
	 * @return A ModuleType representing the type of module this is.
	 */
	public abstract ModuleType getModuleType();

	/**
	 * A lower case snake_case string id that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	public final String getNBTKey() {
		return moduleNBTKey;
	}

	public final String getReferenceModuleID() {
		return createdByFactory.getReferenceModuleID();
	}

	@Override
	public final String toString() {
		return getNBTKey();
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 */
	@Nonnull
	public final String getReadableName() {
		return LibrarianLib.PROXY.translate(getNameKey());
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 */
	@Nonnull
	public final String getNameKey() {
		return "wizardry.spell." + moduleNBTKey + ".name";
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public final String getDescription() {
		return LibrarianLib.PROXY.translate(getDescriptionKey());
	}

	/**
	 * Convenience method for renderVisualization
	 */
	@Nonnull
	public final IBlockState getCachableBlockstate(@Nonnull World world, @Nonnull BlockPos targetBlock, @Nonnull SpellData data) {
		BlockStateCache cacheData = data.getData(SpellData.DefaultKeys.BLOCKSTATE_CACHE);
		Map<BlockPos, IBlockState> cache;

		IBlockState state;
		if (cacheData != null && cacheData.getBlockStateCache() != null) {

			cache = cacheData.getBlockStateCache();
			if (cache.containsKey(targetBlock)) {
				return cache.get(targetBlock);
			} else {
				cache.put(targetBlock, state = world.getBlockState(targetBlock));
				data.addData(BLOCKSTATE_CACHE, new BlockStateCache(cache));
			}
		} else {
			cache = new HashMap<>();
			cache.put(targetBlock, state = world.getBlockState(targetBlock));
			data.addData(BLOCKSTATE_CACHE, new BlockStateCache(cache));
		}

		return state;
	}

	public List<String> getDetailedInfo() {
		List<String> detailedInfo = new ArrayList<>();
		for (Attribute attribute : this.attributeRanges.keySet()) {
			if (attribute.hasDetailedText())
				detailedInfo.addAll(getDetailedInfo(attribute));
		}
		return detailedInfo;
	}

	@Nonnull
	public final List<String> getDetailedInfo(Attribute attribute) {
		List<String> detailedInfo = new ArrayList<>();
		String infoKey = getDescriptionKey() + ".";
		String rangeKey = "wizardry.misc.attribute_range";
		detailedInfo.add(LibrarianLib.PROXY.translate(infoKey + attribute.getShortName()));
		detailedInfo.add("    " + LibrarianLib.PROXY.translate(rangeKey) + attributeRanges.get(attribute));
		return detailedInfo;
	}

	/**
	 * Specify all applicable modifiers that can be applied to this module. <br />
	 * <b>NOTE</b>: Don't call this method during initialization phase, as it is not guaranteed
	 * that all modules could have been parsed.
	 *
	 * @return Any set with applicable ModuleModifiers.
	 */
	@Nullable
	public ModuleInstanceModifier[] applicableModifiers() {
		// Find all registered compatible modifiers and return them. Results are cached.
		if (applicableModifiers == null) {
			LinkedList<ModuleInstanceModifier> applicableModifiersList = new LinkedList<>();
			// TODO: Replace applicable modifier list with more dynamic system
			String[] modifierNames = moduleClass.compatibleModifiers();
			if (modifierNames != null) {
				for (ModuleInstance mod : ModuleRegistry.INSTANCE.modules) {
					for (String modifier : modifierNames) {
						if (mod.getNBTKey().equals(modifier)) {
							if (!(mod instanceof ModuleInstanceModifier)) {
								// TODO: Log it!
								continue;
							}
							applicableModifiersList.add((ModuleInstanceModifier) mod);    // Expected to be of type ModuleModifier
							break;
						}
					}
				}
			}

			applicableModifiers = applicableModifiersList.toArray(new ModuleInstanceModifier[applicableModifiersList.size()]);
		}
		return applicableModifiers;
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public final String getDescriptionKey() {
		return "wizardry.spell." + moduleNBTKey + ".desc";
	}

	@Nonnull
	public final Color getPrimaryColor() {
		return primaryColor;
	}

	public final double getBurnoutFill() {
		return attributeRanges.get(AttributeRegistry.BURNOUT).min;
	}

	public final int getCooldownTime() {
		return (int) attributeRanges.get(AttributeRegistry.COOLDOWN).min;
	}

	public final int getChargeupTime() {
		return (int) attributeRanges.get(AttributeRegistry.CHARGEUP).min;
	}

	public final ItemStack getItemStack() {
		return itemStack;
	}

	public final double getManaDrain() {
		return attributeRanges.get(AttributeRegistry.MANA).min;
	}

	public final double getPowerMultiplier() {
		return attributeRanges.get(AttributeRegistry.POWER_MULTI).min;
	}

	public final double getManaMultiplier() {
		return attributeRanges.get(AttributeRegistry.MANA_MULTI).min;
	}

	public final double getBurnoutMultiplier() {
		return attributeRanges.get(AttributeRegistry.BURNOUT_MULTI).min;
	}

	@Nonnull
	public final Color getSecondaryColor() {
		return secondaryColor;
	}

	public List<AttributeModifier> getAttributeModifiers() {
		return attributeModifiers;
	}

	public Map<Attribute, AttributeRange> getAttributeRanges() {
		return attributeRanges;
	}

	public final void addAttribute(AttributeModifier attribute) {
		this.attributeModifiers.add(attribute);
	}

	public final void addAttributeRange(Attribute attribute, AttributeRange range) {
		this.attributeRanges.put(attribute, range);
	}

	/**
	 * If module shouldn't be rendered when executing.
	 *
	 * @return <code>true</code> iff yes.
	 */
	public boolean ignoreResultsForRendering() {
		return false;
	}

	/**
	 * If children shouldn't be traversed after execution.
	 *
	 * @return <code>true</code> iff yes.
	 */
	public boolean shouldRunChildren() {
		return true;
	}

	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	public boolean run(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
	}

	/**
	 * Use this to run the module properly.
	 *
	 * @param data    The spellData associated with it.
	 * @param ring    The SpellRing made with this.
	 * @return If the spellData has succeeded.
	 */
	public final boolean castSpell(@Nonnull World world, @Nonnull SpellData data, @Nonnull SpellRing ring) {
		if (world.isRemote) return true;

		boolean success;

		boolean ranOnce = false;
		NBTTagList list = data.getDataWithFallback(SpellData.DefaultKeys.TAG_LIST, new NBTTagList());
		for (NBTBase base : list) {
			if (base instanceof NBTTagString) {
				if (((NBTTagString) base).getString().equals(ring.getUniqueID().toString())) {
					ranOnce = true;
					break;
				}
			}
		}
		if (moduleClass instanceof ILingeringModule && !ranOnce) {
			WizardryWorld worldCap = WizardryWorldCapability.get(world);

			list.appendTag(new NBTTagString(ring.getUniqueID().toString()));
			data.addData(SpellData.DefaultKeys.TAG_LIST, list);
			success = internalCastSpell(world, data, ring) && ((ILingeringModule) moduleClass).runOnStart(world, data, ring);

			if (success) {
				worldCap.getSpellObjectManager().addLingering(new SpellObjectManager.LingeringObject(world, data, ring), ((ILingeringModule) moduleClass).getLingeringTime(world, data, ring));
			}

		} else {
			success = internalCastSpell(world, data, ring);
		}

		if (success || ignoreResultsForRendering()) {
			sendRenderPacket(world, data, ring);
		}

		return success;
	}

	private boolean internalCastSpell(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		SpellCastEvent event = new SpellCastEvent(world, spellRing, spell);
		MinecraftForge.EVENT_BUS.post(event);

		return !event.isCanceled() && run(world, spell, spellRing);
	}

	public final void sendRenderPacket(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d target = spell.getTargetWithFallback(world);

		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(spell, spellRing),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), target.x, target.y, target.z, 256));
	}

	@Nonnull
	public final NBTTagString serialize() {
		return new NBTTagString(getNBTKey());
	}

	public ResourceLocation getIconLocation() {
		if (icon == null)
			return new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + getNBTKey() + ".png");
		return icon;
	}
}
