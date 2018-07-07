package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.teamwizardry.wizardry.api.util.PosUtils.getPerpendicularFacings;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectSubstitution extends ModuleEffect implements IBlockSelectable {

	@Nonnull
	@Override
	public String getID() {
		return "effect_substitution";
	}


	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseAOE()};
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim();
		Entity caster = spell.getCaster();
		BlockPos targetBlock = spell.getTargetPos();

		if (caster == null) return false;

		if (targetEntity instanceof EntityLivingBase) {
			if (!spellRing.taxCaster(spell)) return false;

			Vec3d posTarget = new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ),
					posCaster = new Vec3d(caster.posX, caster.posY, caster.posZ);
			float yawTarget = targetEntity.rotationYaw,
					pitchTarget = targetEntity.rotationPitch,
					yawCaster = caster.rotationYaw,
					pitchCaster = caster.rotationPitch;

			targetEntity.rotationYaw = yawCaster;
			targetEntity.rotationPitch = pitchCaster;
			targetEntity.setPositionAndUpdate(posCaster.x, posCaster.y, posCaster.z);

			caster.rotationYaw = yawTarget;
			caster.rotationPitch = pitchTarget;
			caster.setPositionAndUpdate(posTarget.x, posTarget.y, posTarget.z);
			spell.world.playSound(null, caster.getPosition(), ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());
			spell.world.playSound(null, targetEntity.getPosition(), ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());

			return true;

		} else if (targetBlock != null && caster instanceof EntityPlayer) {
			ItemStack hand = ((EntityPlayer) caster).getHeldItemMainhand();
			if (hand.isEmpty()) return false;

			spell.world.playSound(null, targetBlock, ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());
			if (ItemNBTHelper.verifyExistence(hand, "selected")) {

				NBTTagCompound compound = ItemNBTHelper.getCompound(hand, "selected");
				if (compound == null) return false;

				IBlockState state = NBTUtil.readBlockState(compound);
				IBlockState touchedBlock = spell.world.getBlockState(targetBlock);

				if (touchedBlock.getBlock() == state.getBlock()) return false;

				double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

				ItemStack stackBlock = null;
				for (ItemStack stack : ((EntityPlayer) caster).inventory.mainInventory) {
					if (stack.isEmpty()) continue;
					if (!(stack.getItem() instanceof ItemBlock)) continue;
					Block block = ((ItemBlock) stack.getItem()).getBlock();
					if (block != state.getBlock()) continue;
					stackBlock = stack;
					break;
				}

				if (stackBlock == null) return false;

				HashSet<BlockPos> blocks = new HashSet<>();
				HashSet<BlockPos> branch = new HashSet<>();
				branch.add(targetBlock);
				blocks.add(targetBlock);
				getBlocks(spell.world, touchedBlock.getBlock(), (int) area, branch, blocks);

				if (blocks.isEmpty()) return true;

				for (@SuppressWarnings("unused") BlockPos ignored : blocks) {
					if (stackBlock.isEmpty()) return true;
					if (!spellRing.taxCaster(spell)) return false;
					BlockPos nearest = null;
					for (BlockPos pos : blocks) {
						if (spell.world.isAirBlock(pos)) continue;
						if (spell.world.getBlockState(pos).getBlock() == state.getBlock()) continue;

						if (nearest == null) {
							nearest = pos;
							continue;
						}
						if (pos.distanceSq(targetBlock) < nearest.distanceSq(targetBlock)) nearest = pos;
					}
					if (nearest == null) return true;

					stackBlock.shrink(1);

					IBlockState oldState = spell.world.getBlockState(nearest);
					BlockUtils.placeBlock(spell.world, nearest, state, (EntityPlayerMP) caster);
					((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, spell.world.rand, 0)));
				}
			}
			return true;
		}

		return false;
	}

	private void getBlocks(World world, Block block, int maxBlocks, HashSet<BlockPos> branch, HashSet<BlockPos> allBlocks) {
		if (allBlocks.size() >= maxBlocks) return;

		HashSet<BlockPos> newBranch = new HashSet<>();

		for (BlockPos branchPos : branch) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos posAdj = branchPos.offset(facing);
				IBlockState state = world.getBlockState(posAdj);

				if (!world.isBlockLoaded(posAdj)) continue;
				if (allBlocks.contains(posAdj)) continue;
				if (state.getBlock() != block) continue;

				boolean sideSolid = false;
				for (EnumFacing dir : PosUtils.symmetricFacingValues) {
					BlockPos adjPos = branchPos.offset(dir);
					IBlockState adjState = world.getBlockState(adjPos);
					if (!adjState.isSideSolid(world, adjPos, dir.getOpposite())) {
						sideSolid = true;
						break;
					}
				}
				if (!sideSolid) continue;

				if (allBlocks.size() >= maxBlocks) return;

				newBranch.add(posAdj);
				allBlocks.add(posAdj);
			}
		}
		boolean mismatched = false;
		for (BlockPos pos : branch) if (!newBranch.contains(pos)) mismatched = true;
		if (mismatched)
			getBlocks(world, block, maxBlocks, newBranch, allBlocks);
	}

	@Override
	@SuppressWarnings("unused")
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity caster = spell.getCaster();
		BlockPos targetBlock = spell.getTargetPos();
		Entity targetEntity = spell.getVictim();

		if (targetEntity != null && caster != null) {

			ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(20, 30));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ)), 50, RandUtil.nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
				glitter.setLifetime(RandUtil.nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 2, 0),
						0.5f, 0f, 1, RandUtil.nextFloat()
				));
			});

			glitter.setColorFunction(new InterpColorHSV(getSecondaryColor(), getPrimaryColor()));
			ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(new Vec3d(caster.posX, caster.posY, caster.posZ)), 50, RandUtil.nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
				glitter.setLifetime(RandUtil.nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 4, 0),
						1f, 0f, 1, RandUtil.nextFloat()
				));
			});
		} else if (targetBlock != null) {
			ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(20, 30));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(new Vec3d(targetBlock).addVector(0.5, 0.5, 0.5)), 20, 0, (aFloat, particleBuilder) -> {
				glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
				glitter.setLifetime(RandUtil.nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setMotion(new Vec3d(
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.1, 0.1)
				));
			});
		}
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		Entity caster = data.getCaster();
		BlockPos targetBlock = data.getTargetPos();

		if (!(caster instanceof EntityLivingBase)) return previousData;
		ItemStack hand = ((EntityLivingBase) caster).getHeldItemMainhand();

		if (hand.isEmpty()) return previousData;

		if (targetBlock != null && caster instanceof EntityPlayer) {
			if (ItemNBTHelper.verifyExistence(hand, "selected")) {
				NBTTagCompound compound = ItemNBTHelper.getCompound(hand, "selected");
				if (compound == null) return previousData;

				IBlockState state = NBTUtil.readBlockState(compound);
				IBlockState targetState = getCachableBlockstate(data.world, targetBlock, previousData);
				if (targetState.getBlock() == state.getBlock()) return previousData;

				double area = ring.getAttributeValue(AttributeRegistry.AREA, data);

				ItemStack stackBlock = null;
				for (ItemStack stack : ((EntityPlayer) caster).inventory.mainInventory) {
					if (stack.isEmpty()) continue;
					if (!(stack.getItem() instanceof ItemBlock)) continue;
					Block block = ((ItemBlock) stack.getItem()).getBlock();
					if (block != state.getBlock()) continue;
					stackBlock = stack;
					break;
				}

				if (stackBlock == null) return previousData;
				stackBlock = stackBlock.copy();

				HashSet<BlockPos> blocks = new HashSet<>();
				HashSet<BlockPos> branch = new HashSet<>();
				branch.add(targetBlock);
				blocks.add(targetBlock);
				getBlocks(data.world, targetState.getBlock(), (int) area, branch, blocks);

				if (blocks.isEmpty()) return previousData;

				HashMap<BlockPos, IBlockState> blockStateCache = new HashMap<>();
				for (BlockPos pos : blocks) {
					blockStateCache.put(pos, data.world.getBlockState(pos));
				}

				HashMap<BlockPos, IBlockState> tmpCache = new HashMap<>(blockStateCache);

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

				for (Map.Entry<BlockPos, IBlockState> entry : tmpCache.entrySet()) {

					if (BlockUtils.isAnyAir(entry.getValue())) continue;

					BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(entry.getKey());
					for (EnumFacing facing : EnumFacing.VALUES) {

						Vec3d directionOffsetVec = new Vec3d(facing.getDirectionVec()).scale(0.5);
						Vec3d adjPos = new Vec3d(mutable).addVector(0.5, 0.5, 0.5).add(directionOffsetVec);

						mutable.move(facing);

						IBlockState adjState;
						if (!blockStateCache.containsKey(mutable)) {
							adjState = data.world.getBlockState(mutable);
							blockStateCache.put(mutable.toImmutable(), adjState);
						} else adjState = blockStateCache.get(mutable);

						if (adjState.getBlock() != targetState.getBlock() || !blocks.contains(mutable)) {

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
						}
						mutable.move(facing.getOpposite());
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
		}

		return previousData;
	}
}
