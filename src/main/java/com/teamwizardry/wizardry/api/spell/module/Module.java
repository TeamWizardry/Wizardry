package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Demoniaque.
 */
public abstract class Module {

	protected final List<AttributeModifier> attributes = new ArrayList<>();
	protected Map<Attribute, AttributeRange> attributeRanges = new DefaultHashMap<>(AttributeRange.BACKUP);
	protected Color primaryColor;
	protected Color secondaryColor;
	protected ItemStack itemStack;

	@Nullable
	public static Module deserialize(NBTTagString tagString) {
		return ModuleRegistry.INSTANCE.getModule(tagString.getString());
	}

	@Nullable
	public static Module deserialize(String id) {
		return ModuleRegistry.INSTANCE.getModule(id);
	}

	public final void init(ItemStack itemStack,
	                       Color primaryColor,
	                       Color secondaryColor,
	                       DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		this.itemStack = itemStack;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.attributeRanges = attributeRanges;
	}

	/**
	 * The type of module this module is.
	 *
	 * @return A ModuleType representing the type of module this is.
	 */
	@Nonnull
	public abstract ModuleType getModuleType();

	/**
	 * A lower case snake_case string id that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	@Nonnull
	public abstract String getID();

	@Override
	public final String toString() {
		return getID();
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
		return "wizardry.spell." + getID() + ".name";
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public final String getDescription() {
		return LibrarianLib.PROXY.translate(getDescriptionKey());
	}
	
	@Nonnull
	public List<String> getDetailedInfo()
	{
		List<String> detailedInfo = new ArrayList<>();
		for (Attribute attribute : attributeRanges.keySet())
		{
			if (attribute.hasDetailedText())
				detailedInfo.addAll(getDetailedInfo(attribute));
		}
		return detailedInfo;
	}
	
	@Nonnull
	public final List<String> getDetailedInfo(Attribute attribute)
	{
		List<String> detailedInfo = new ArrayList<>();
		String infoKey = getDescriptionKey() + ".";
		String rangeKey = "wizardry.misc.attribute_range";
		detailedInfo.add(LibrarianLib.PROXY.translate(infoKey + attribute.getShortName()));
		detailedInfo.add("    " + LibrarianLib.PROXY.translate(rangeKey) + attributeRanges.get(attribute));
		return detailedInfo;
	}

	/**
	 * Specify all applicable modifiers that can be applied to this module.
	 *
	 * @return Any set with applicable ModuleModifiers.
	 */
	@Nullable
	public ModuleModifier[] applicableModifiers() {
		return null;
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public final String getDescriptionKey() {
		return "wizardry.spell." + getID() + ".desc";
	}
	
	@Nonnull
	public final Color getPrimaryColor() {
		return primaryColor;
	}

	public final double getBurnoutFill() {
		return attributeRanges.get(AttributeRegistry.BURNOUT).base;
	}

	public final int getCooldownTime() {
		return (int) attributeRanges.get(AttributeRegistry.COOLDOWN).base;
	}

	public final int getChargeupTime() {
		return (int) attributeRanges.get(AttributeRegistry.CHARGEUP).base;
	}

	public final ItemStack getItemStack() {
		return itemStack;
	}

	public final double getManaDrain() {
		return attributeRanges.get(AttributeRegistry.MANA).base;
	}

	public final double getPowerMultiplier() {
		return attributeRanges.get(AttributeRegistry.POWER_MULTI).base;
	}

	public final double getManaMultiplier() {
		return attributeRanges.get(AttributeRegistry.MANA_MULTI).base;
	}

	public final double getBurnoutMultiplier() {
		return attributeRanges.get(AttributeRegistry.BURNOUT_MULTI).base;
	}

	@Nonnull
	public final Color getSecondaryColor() {
		return secondaryColor;
	}

	public List<AttributeModifier> getAttributes() {
		return attributes;
	}

	public Map<Attribute, AttributeRange> getAttributeRanges() {
		return attributeRanges;
	}

	public final void addAttribute(AttributeModifier attribute) {
		this.attributes.add(attribute);
	}
	
	public final void addAttributeRange(Attribute attribute, AttributeRange range)
	{
		this.attributeRanges.put(attribute, range);
	}

	public boolean ignoreResult() {
		return false;
	}

	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	public abstract boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing);

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing);

	/**
	 * Use this to run the module properly without rendering.
	 *
	 * @param spell      The spellData associated with it.
	 * @param spellRing The SpellRing made with this.
	 * @return If the spellData has succeeded.
	 */
	public final boolean castSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (spell.world.isRemote) return true;

		if (this instanceof ILingeringModule) {
			boolean alreadyLingering = false;
			for (SpellTicker.LingeringObject lingeringObject : SpellTicker.getStorageMap()) {
				if (lingeringObject.getSpellRing() == spellRing
						|| lingeringObject.getSpellData() == spell) {
					alreadyLingering = true;
					break;
				}
			}
			if (!alreadyLingering)
				SpellTicker.addLingerSpell(spellRing, spell, ((ILingeringModule) this).getLingeringTime(spell, spellRing));
		}

		SpellCastEvent event = new SpellCastEvent(spellRing, spell);
		MinecraftForge.EVENT_BUS.post(event);

		return !event.isCanceled() && run(spell, spellRing);
	}

	public final void sendRenderPacket(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d target = spell.getTargetWithFallback();

		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(spell, spellRing),
					new NetworkRegistry.TargetPoint(spell.world.provider.getDimension(), target.x, target.y, target.z, 60));
	}

	@Nonnull
	public final NBTTagString serialize() {
		return new NBTTagString(getID());
	}
}
