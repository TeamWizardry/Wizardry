package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCKSTATE_CACHE;
import static com.teamwizardry.wizardry.api.util.PosUtils.getPerpendicularFacings;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Demoniaque.
 */
public abstract class ModuleInstance {

	protected final String subModuleID;
	protected final ModuleFactory createdByFactory;
	protected final IModule moduleClass;
	protected final ResourceLocation icon;
	protected final List<AttributeModifier> attributes = new ArrayList<>();
	protected Map<Attribute, AttributeRange> attributeRanges = new DefaultHashMap<>(AttributeRange.BACKUP);
	protected Color primaryColor;
	protected Color secondaryColor;
	protected ItemStack itemStack;
	protected ModuleInstanceModifier [] applicableModifiers = null;

	@Nullable
	public static ModuleInstance deserialize(NBTTagString tagString) {
		return ModuleRegistry.INSTANCE.getModule(tagString.getString());
	}

	@Nullable
	public static ModuleInstance deserialize(String id) {
		return ModuleRegistry.INSTANCE.getModule(id);
	}
	
	static ModuleInstance createInstance(IModule moduleClass,
			ModuleFactory createdByFactory,
			String subModuleID,
			ResourceLocation icon,
			ItemStack itemStack,
            Color primaryColor,
            Color secondaryColor,
            DefaultHashMap<Attribute, AttributeRange> attributeRanges) {
		if( moduleClass instanceof IModuleEffect )
			return new ModuleInstanceEffect((IModuleEffect)moduleClass, createdByFactory, subModuleID, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
		else if( moduleClass instanceof IModuleModifier )
			return new ModuleInstanceModifier((IModuleModifier)moduleClass, createdByFactory, subModuleID, icon, itemStack, primaryColor, secondaryColor, attributeRanges);
		else if( moduleClass instanceof IModuleEvent )
			return new ModuleInstanceEvent((IModuleEvent)moduleClass, createdByFactory, itemStack, subModuleID, icon, primaryColor, secondaryColor, attributeRanges);
		else if( moduleClass instanceof IModuleShape )
			return new ModuleInstanceShape((IModuleShape)moduleClass, createdByFactory, itemStack, subModuleID, icon, primaryColor, secondaryColor, attributeRanges);
		else
			throw new UnsupportedOperationException("Unknown module type.");
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
		this.subModuleID = moduleName;
		this.icon = icon;
		this.itemStack = itemStack;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.attributeRanges = attributeRanges;
	}
	
	public final IModule getModuleClass() {
		return this.moduleClass;
	}
	
	/**
	 * Will render whatever GL code is specified here while the spell is being held by the
	 * player's hand.
	 */
	@Nonnull
	@SideOnly(Side.CLIENT)
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		return standardRenderVisualization(data, ring, previousData);
	}

	public final SpellData standardRenderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		return new SpellData(data.world);
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
		if (ItemNBTHelper.verifyExistence(hand, "selected")) {

			NBTTagCompound compound = ItemNBTHelper.getCompound(hand, "selected");
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
	public final String getSubModuleID() {
		return subModuleID;
	}
	
	public final String getReferenceModuleID() {
		return createdByFactory.getReferenceModuleID();
	}

	@Override
	public final String toString() {
		return getSubModuleID();
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
		return "wizardry.spell." + subModuleID + ".name";
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
	public final IBlockState getCachableBlockstate(@Nonnull World world, @Nonnull BlockPos targetBlock, @Nonnull SpellData previousData) {
		HashMap<BlockPos, IBlockState> cache = previousData.getData(SpellData.DefaultKeys.BLOCKSTATE_CACHE);

		IBlockState state;
		if (cache != null) {
			if (cache.containsKey(targetBlock)) {
				return cache.get(targetBlock);
			} else {
				cache.put(targetBlock, state = world.getBlockState(targetBlock));
				previousData.addData(BLOCKSTATE_CACHE, cache);
			}
		} else {
			cache = new HashMap<>();
			cache.put(targetBlock, state = world.getBlockState(targetBlock));
			previousData.addData(BLOCKSTATE_CACHE, cache);
		}

		return state;
	}

	/**
	 * Convenience method for renderVisualization
	 */
	@SideOnly(Side.CLIENT)
	public final void drawCubeOutline(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		GlStateManager.pushMatrix();

		GlStateManager.disableDepth();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorMaterial();

		int color = Color.HSBtoRGB(ClientTickHandler.getTicks() % 200 / 200F, 0.6F, 1F);
		Color colorRGB = new Color(color);

		GL11.glLineWidth(1f);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 255);

		RenderUtils.renderBlockOutline(state.getSelectedBoundingBox(world, pos));

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();

		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	/**
	 * Convenience method for renderVisualization
	 */
	@SideOnly(Side.CLIENT)
	public final void drawFaceOutline(@Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
		GlStateManager.pushMatrix();

		GlStateManager.disableDepth();

		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorMaterial();

		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		int color = Color.HSBtoRGB(ClientTickHandler.getTicks() % 200 / 200F, 0.6F, 1F);
		Color colorRGB = new Color(color);

		Vec3d directionOffsetVec = new Vec3d(facing.getDirectionVec()).scale(0.5);
		Vec3d adjPos = new Vec3d(pos).add(0.5, 0.5, 0.5).add(directionOffsetVec);

		GL11.glLineWidth(1f);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 255);

		for (EnumFacing facing1 : getPerpendicularFacings(facing)) {
			for (EnumFacing facing2 : getPerpendicularFacings(facing)) {
				if (facing1 == facing2 || facing1.getOpposite() == facing2 || facing2.getOpposite() == facing1)
					continue;

				Vec3d p1 = new Vec3d(facing1.getDirectionVec()).scale(0.5);
				Vec3d p2 = new Vec3d(facing2.getDirectionVec()).scale(0.5);
				Vec3d edge = adjPos.add(p1.add(p2));

				tessellator.getBuffer().pos(edge.x, edge.y, edge.z).endVertex();
			}
		}

		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();

		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
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
		if( applicableModifiers == null ) {
			LinkedList<ModuleInstanceModifier> applicableModifiersList = new LinkedList<>();
			// TODO: Replace applicable modifier list with more dynamic system
			String[] modifierNames = moduleClass.compatibleModifierClasses();
			if( modifierNames != null ) {
				for( ModuleInstance mod : ModuleRegistry.INSTANCE.modules ) {
					for( String modifier : modifierNames ) {
						if( mod.getSubModuleID().equals(modifier) ) {
							if( !(mod instanceof ModuleInstanceModifier) ) {
								// TODO: Log it!
								continue;
							}
							applicableModifiersList.add((ModuleInstanceModifier)mod);	// Expected to be of type ModuleModifier
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
		return "wizardry.spell." + subModuleID + ".desc";
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

	public final void addAttributeRange(Attribute attribute, AttributeRange range) {
		this.attributeRanges.put(attribute, range);
	}
	
	public final boolean ignoreResultForRendering() {
		return moduleClass.ignoreResultForRendering();
	}
	
	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
	}

	/**
	 * Use this to run the module properly without rendering.
	 *
	 * @param spell     The spellData associated with it.
	 * @param spellRing The SpellRing made with this.
	 * @return If the spellData has succeeded.
	 */
	public final boolean castSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (spell.world.isRemote) return true;

		if (moduleClass instanceof ILingeringModule) {
			boolean alreadyLingering = false;

			WizardryWorld worldCap = WizardryWorldCapability.get(spell.world);
			for (SpellTicker.LingeringObject lingeringObject : worldCap.getLingeringObjects()) {
				if (lingeringObject.getSpellRing() == spellRing
						|| lingeringObject.getSpellData() == spell) {
					alreadyLingering = true;
					break;
				}
			}
			if (!alreadyLingering)
				worldCap.addLingerSpell(spellRing, spell, ((ILingeringModule) moduleClass).getLingeringTime(spell, spellRing));
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
		return new NBTTagString(getSubModuleID());
	}

	public ResourceLocation getIconLocation() {
		if( icon == null )
			return new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + getSubModuleID() + ".png");
		return icon;
	}
}
