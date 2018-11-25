package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextSuper;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideSuper;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_beam")
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ModuleShapeBeam implements IModuleShape, IContinuousModule {

	public static final String BEAM_OFFSET = "beam offset";
	public static final String BEAM_CAST = "beam cast";

	public static final HashMap<ItemStack, BeamTicker> beamTickMap = new HashMap<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_range", "modifier_increase_potency"};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean ignoreResultsForRendering() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRunChildren() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		ItemStack stack = event.player.getHeldItemMainhand();
		beamTickMap.keySet().removeIf(itemStack -> !ItemStack.areItemStacksEqual(itemStack, stack));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean run(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d look = spell.getData(LOOK);
		Vec3d position = spell.getOrigin();
		Entity caster = spell.getCaster();

		if (look == null || position == null || caster == null) return false;
		ItemStack stack = ((EntityLivingBase) caster).getHeldItemMainhand();
		if (stack.isEmpty()) return true;
		beamTickMap.putIfAbsent(stack, new BeamTicker());

		BeamTicker ticker = beamTickMap.get(stack);

		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);

		double beamOffset = ticker.ticks + potency;
		ticker.cast = false;

		if (beamOffset >= ConfigValues.beamTimer) {
			beamOffset %= ConfigValues.beamTimer;
			if (!spellRing.taxCaster(spell, true)) {
				ticker.ticks = beamOffset;
				return false;
			}

			IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
			overrides.onRunBeam(spell, spellRing);

			RayTraceResult trace = new RayTrace(world, look, position, range)
					.setEntityFilter(input -> input != caster)
					.setReturnLastUncollidableBlock(true)
					.setIgnoreBlocksWithoutBoundingBoxes(true)
					.trace();

			spell.processTrace(trace, look.scale(range));

			if (spellRing.getChildRing() != null)
				spellRing.getChildRing().runSpellRing(spell);

			ticker.cast = true;
			instance.sendRenderPacket(spell, spellRing);	// Is already executed via SpellRing.runSpellRing() ??? 
		}

		ticker.ticks = beamOffset;
		return true;
	}

	private NBTTagList serializeBeamTicks() {
		NBTTagList list = new NBTTagList();
		beamTickMap.forEach((stack, beamTicker) -> {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag("stack", stack.serializeNBT());
			compound.setDouble("tick", beamTicker.ticks);
			compound.setBoolean("cast", beamTicker.cast);
			list.appendTag(compound);
		});
		return list;
	}

	private void deserializeBeamTicks(NBTTagList list) {
		for (NBTBase base : list) {
			if (base instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) base;
				if (compound.hasKey("stack") && compound.hasKey("tick") && compound.hasKey("cast")) {
					ItemStack itemStack = new ItemStack((NBTTagCompound) compound.getTag("stack"));
					BeamTicker ticker = new BeamTicker();
					ticker.ticks = compound.getDouble("tick");
					ticker.cast = compound.getBoolean("cast");
					beamTickMap.put(itemStack, ticker);
				}
			}
		}
	}

	@NotNull
	@Override
	public SpellData renderVisualization(ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		World world = data.world;
		Vec3d look = data.getData(LOOK);
		Vec3d position = data.getOrigin();
		Entity caster = data.getCaster();

		if (look == null || position == null || caster == null) return previousData;

		double range = ring.getAttributeValue(AttributeRegistry.RANGE, data);

		RayTraceResult trace = new RayTrace(world, look, position, range)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(trace, look.scale(range));
		return previousData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if( overrides.onRenderBeam(spell, spellRing) )
			return;

		World world = spell.world;
		Vec3d look = spell.getData(LOOK);
		Vec3d position = spell.getOrigin();
		Entity caster = spell.getCaster();

		if (look == null || position == null || caster == null) return;
		ItemStack stack = ((EntityLivingBase) caster).getHeldItemMainhand();
		if (stack.isEmpty()) return;

		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);

		RayTraceResult trace = new RayTrace(world, look, position, range)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		Vec3d target = trace.hitVec;

		if (target == null) return;

		LibParticles.SHAPE_BEAM(world, target, spell.getOriginHand(), RandUtil.nextBoolean() ? spellRing.getPrimaryColor() : spellRing.getSecondaryColor());
	}

	public static class BeamTicker {

		boolean cast = false;
		double ticks = 0;

		BeamTicker() {
		}
	}
	
	///////////
	
	@ModuleOverride("shape_beam_render")
	public boolean onRenderBeam(SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}
	
	@ModuleOverride("shape_beam_run")
	public void onRunBeam(@ContextSuper ModuleOverrideSuper ovdSuper, SpellData data, SpellRing shape) {
		// Default implementation
	}
}
