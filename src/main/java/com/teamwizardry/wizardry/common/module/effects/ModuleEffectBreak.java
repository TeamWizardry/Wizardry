package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeZone;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "effect_break")
public class ModuleEffectBreak implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe", "modifier_increase_potency"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {

		BlockPos targetPos = spell.getData(BLOCK_HIT);
		EnumFacing facing = spell.getData(FACE_HIT);
		Entity targetEntity = spell.getVictim(world);
		Entity caster = spell.getCaster(world);

		double range = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);
		double strength = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);

		if (targetEntity instanceof EntityLivingBase)
			for (ItemStack stack : targetEntity.getArmorInventoryList())
				stack.damageItem((int) strength, (EntityLivingBase) targetEntity);

		if (targetPos == null || facing == null) return false;
		Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, (int) range, (int) ((Math.sqrt(range) + 1) / 2), pos -> {
			if (world.isAirBlock(pos)) return true;
			if (!world.isAirBlock(pos.offset(facing))) return true;
			IBlockState state = world.getBlockState(pos);
			float hardness = state.getBlockHardness(world, pos);
			return hardness < 0 || hardness > strength;
		});
		for (BlockPos pos : blocks) {
			if (!spellRing.taxCaster(world, spell, 1 / range, false)) return false;
			IBlockState state = world.getBlockState(pos);
			BlockUtils.breakBlock(world, pos, state, BlockUtils.makeBreaker(world, pos, caster));
			world.playEvent(2001, pos, Block.getStateId(state));
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		LibParticles.EXPLODE(world, position, instance.getPrimaryColor(), instance.getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceEffect instance, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		if (ring.getParentRing() != null
				&& ring.getParentRing().getModule() != null
				&& ring.getParentRing().getModule().getModuleClass() instanceof ModuleShapeZone)
			return data;


		BlockPos targetPos = data.getData(BLOCK_HIT);
		EnumFacing facing = data.getData(FACE_HIT);

		double range = ring.getAttributeValue(world, AttributeRegistry.AREA, data);
		double strength = ring.getAttributeValue(world, AttributeRegistry.POTENCY, data);

		if (targetPos == null || facing == null) return data;
		Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, (int) range, (int) ((Math.sqrt(range) + 1) / 2), pos ->
		{
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
			if (!world.isAirBlock(mutable.offset(facing))) return true;
			IBlockState state = world.getBlockState(pos);
			if (BlockUtils.isAnyAir(state)) return true;

			float hardness = state.getBlockHardness(world, pos);
			return hardness < 0 || hardness > strength;
		});
		if (blocks.isEmpty()) return data;
		for (BlockPos pos : blocks) {
			IBlockState state = world.getBlockState(pos);
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
			for (EnumFacing face : EnumFacing.VALUES) {
				mutable.move(face);
				IBlockState adjStat = instance.getCachableBlockstate(world, mutable, data);
				if (adjStat.getBlock() != state.getBlock() || !blocks.contains(mutable)) {
					RenderUtils.drawFaceOutline(mutable, face.getOpposite());
				}
				mutable.move(face.getOpposite());
			}
		}
		return data;
	}
}
