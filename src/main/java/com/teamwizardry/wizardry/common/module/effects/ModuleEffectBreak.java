package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.IModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
@RegisterModule
public class ModuleEffectBreak implements IModuleEffect {

	@Nonnull
	@Override
	public String getClassID() {
		return "effect_break";
	}

	@Override
	public IModuleModifier[] applicableModifiers() {
		return new IModuleModifier[]{new ModuleModifierIncreaseAOE(), new ModuleModifierIncreasePotency()};
	}

	@Override
	public boolean run(ModuleEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		EnumFacing facing = spell.getData(FACE_HIT);
		Entity targetEntity = spell.getVictim();
		Entity caster = spell.getCaster();

		double range = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
		double strength = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);

		if (targetEntity instanceof EntityLivingBase)
			for (ItemStack stack : targetEntity.getArmorInventoryList())
				stack.damageItem((int) strength, (EntityLivingBase) targetEntity);
		
		if (targetPos == null || facing == null) return false;
		Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, (int) range, (int) ((Math.sqrt(range)+1)/2), pos ->
		{
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
			if (!world.isAirBlock(mutable.offset(facing))) return true;
			IBlockState state = world.getBlockState(pos);
			if (BlockUtils.isAnyAir(state)) return true;
			
			float hardness = state.getBlockHardness(world, pos);
			return hardness < 0 || hardness > strength;
		});
		for (BlockPos pos : blocks)
		{
			if (!spellRing.taxCaster(spell, 1 / range, false)) continue;
			BlockUtils.breakBlock(world, pos, null, caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null, true);
		}
		
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		LibParticles.EXPLODE(world, position, instance.getPrimaryColor(), instance.getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}

	@NotNull
	@Override
	public SpellData renderVisualization(ModuleEffect instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		if (ring.getParentRing() != null
				&& ring.getParentRing().getModule() != null
				&& ring.getParentRing().getModule() == ModuleRegistry.INSTANCE.getModule("event_collide_entity"))
			return previousData;

		World world = data.world;
		BlockPos targetPos = data.getData(BLOCK_HIT);
		EnumFacing facing = data.getData(FACE_HIT);

		double range = ring.getAttributeValue(AttributeRegistry.AREA, data);
		double strength = ring.getAttributeValue(AttributeRegistry.POTENCY, data);

		if (targetPos == null || facing == null) return previousData;
		Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, (int) range, (int) ((Math.sqrt(range)+1)/2), pos ->
		{
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
			if (!world.isAirBlock(mutable.offset(facing))) return true;
			IBlockState state = world.getBlockState(pos);
			if (BlockUtils.isAnyAir(state)) return true;
			
			float hardness = state.getBlockHardness(world, pos);
			return hardness < 0 || hardness > strength;
		});
		if (blocks.isEmpty()) return previousData;
		for (BlockPos pos : blocks)
		{
			IBlockState state = world.getBlockState(pos);
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
			for (EnumFacing face : EnumFacing.VALUES) {
					mutable.move(face);
					IBlockState adjStat = instance.getCachableBlockstate(data.world, mutable, previousData);
					if (adjStat.getBlock() != state.getBlock() || !blocks.contains(mutable)) {
						instance.drawFaceOutline(mutable, face.getOpposite());
				}
				mutable.move(face.getOpposite());
			}
		}
		return previousData;
	}
}
