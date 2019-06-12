package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.player.mana.CustomManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaModule;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.common.tile.TileOrbHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiPredicate;

public class TileManaNode extends TileCachable implements ITickable {

	private static ArrayList<SuckRule> suckRules = new ArrayList<>();

	static {
		addSuckRule(new SuckRule<>(0, 1, true, 1, TileOrbHolder.class, TileOrbHolder.class, (to, from) -> {
			World world = to.world;
			if (world == null) return false;
			if (!checkStructureCompat(to, from)) return false;
			if (from.isPartOfStructure()) {
				TileEntity tile = world.getTileEntity(from.getStructurePos());
				if (tile instanceof IManaGenerator && to.isPartOfStructure() && to.getStructurePos().equals(from.getStructurePos())) {
					return true;
				} else
					return !(tile instanceof IManaGenerator) || (to.isPartOfStructure() && to.getStructurePos().equals(from.getStructurePos()));
			}

			return true;
		}
		));

		addSuckRule(new SuckRule<>(0, 1, false, 1, TileOrbHolder.class, TileOrbHolder.class, (to, from) -> {
			World world = to.world;
			if (world == null) return false;
			if (!checkStructureCompat(to, from)) return false;
			if (from.isPartOfStructure()) {
				TileEntity tile = world.getTileEntity(from.getStructurePos());

				return tile instanceof IManaGenerator && (!to.isPartOfStructure() || !to.getStructurePos().equals(from.getStructurePos()));
			}

			return false;
		}
		));


		addSuckRule(new SuckRule<>(1, 1, false, 1, TileOrbHolder.class, TileManaBattery.class, TileManaNode::checkStructureCompat));

		addSuckRule(new SuckRule<>(1, 0.25, false, 4, TileCraftingPlate.class, TileOrbHolder.class, TileManaNode::checkStructureCompat));

		suckRules.sort(Comparator.comparingInt(SuckRule::getPriority));
	}

	@Module
	public ManaModule cap;

	/**
	 * If this node can suck mana from nodes not part of structures
	 */
	@Save
	private boolean canSuckFromOutside = true;

	/**
	 * If this node can give mana to nodes not part of structures
	 */
	@Save
	private boolean canGiveToOutside = true;

	/**
	 * The center of the structure this node is a part of.
	 * Null if not part of a structure.
	 */
	@Save
	@Nullable
	private BlockPos structurePos = null;

	public TileManaNode(double maxMana, double maxBurnout) {
		cap = new ManaModule(new CustomManaCapability(maxMana, maxBurnout));
	}

	public static void addSuckRule(SuckRule suckRule) {
		suckRules.add(suckRule);
	}

	@Override
	public void update() {
		if (distanceCache.isEmpty()) return;

		if (suckManaAutomatically()) suckMana(getWizardryCap());
	}

	public boolean suckManaAutomatically() {
		return true;
	}

	private static boolean checkStructureCompat(TileManaNode to, TileManaNode from) {
		return (from.isPartOfStructure() && to.isPartOfStructure() && from.canGiveToOutside() && to.canSuckFromOutside())
				|| (from.isPartOfStructure() && !to.isPartOfStructure() && from.canGiveToOutside())
				|| (from.isPartOfStructure() && to.isPartOfStructure() && from.getStructurePos().equals(to.getStructurePos()))
				|| (!from.isPartOfStructure() && !to.isPartOfStructure());
	}

	public double suckMana(IManaCapability cap) {
		double totalZucced = 0;

		if (ManaManager.isManaFull(cap)) return 0;

		for (SuckRule suckRule : suckRules) {
			if (getClass().isAssignableFrom(suckRule.thisClazz)) {

				ArrayList<TileManaNode> nodes = getNearestNodes(suckRule.fromClazz);
				nodes.sort(Comparator.comparingDouble(this::getCachedDistanceSq));

				int i = 0;
				for (TileManaNode from : nodes) {
					if (from == null) continue;

					double zucced = suckManaFrom(from, suckRule, cap);
					if (zucced > 0) {
						totalZucced += zucced;

						// Trigger events to notify
						from.onDrainedFrom(this);
						onSuckFrom(from);

						if (++i > suckRule.getNbOfConnections()) break;
					}
				}
			}
		}
		return totalZucced;
	}

	@Nullable
	public IManaCapability getWizardryCap() {
		return cap.getHandler();
	}

	public void onDrainedFrom(TileManaNode from) {

	}

	public void onSuckFrom(TileManaNode from) {

	}

	public boolean isPartOfStructure() {
		return structurePos != null;
	}

	@Nullable
	public BlockPos getStructurePos() {
		return structurePos;
	}

	public void setStructurePos(@Nullable BlockPos structurePos) {
		this.structurePos = structurePos;
	}

	@Nonnull
	public Vec3d getOffset() {
		return Vec3d.ZERO;
	}

	public boolean canSuckFromOutside() {
		return canSuckFromOutside;
	}

	public void setCanSuckFromOutside(boolean canSuckFromOutside) {
		this.canSuckFromOutside = canSuckFromOutside;
	}

	public double suckManaFrom(TileManaNode interacterFrom, SuckRule suckRule, IManaCapability cap) {

		if (cap == null || interacterFrom.getWizardryCap() == null) return 0;
		if (!suckRule.condition.test(this, interacterFrom)) return 0;

		try (ManaManager.CapManagerBuilder thisMgr = ManaManager.forObject(cap)) {
			try (ManaManager.CapManagerBuilder theirMgr = ManaManager.forObject(interacterFrom.getWizardryCap())) {

				if (thisMgr.isManaFull()) return 0;
				if (theirMgr.isManaEmpty()) return 0;

				if (suckRule.equalize && Math.abs(thisMgr.getMana() - theirMgr.getMana()) <= suckRule.idealAmount)
					return 0;

				double ratio = theirMgr.getMana() / thisMgr.getMana();

				if (suckRule.equalize && Double.isFinite(ratio) && ratio <= 1.2)
					return 0;

				double amount = interacterFrom.drainMana(suckRule.idealAmount);
				if (amount <= 0) return 0;

				ManaManager.forObject(cap).addMana(amount).close();

				if (world.isRemote)
					ClientRunnable.run(new ClientRunnable() {
						@Override
						@SideOnly(Side.CLIENT)
						public void runIfClient() {
							ParticleBuilder helix = new ParticleBuilder(200);
							helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
							helix.setAlphaFunction(new InterpFloatInOut(0.1f, 0.1f));
							ParticleSpawner.spawn(helix, world, new StaticInterp<>(new Vec3d(interacterFrom.getPos()).add(0.5, 1, 0.5)), 1, 0, (someFloat, particleBuilder) -> {
								particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
								particleBuilder.setScale(RandUtil.nextFloat(0.3f, 0.8f));
								particleBuilder.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(getPos().subtract(interacterFrom.getPos())), new Vec3d(0, 5, 0), new Vec3d(0, -5, 0)));
								particleBuilder.setLifetime(RandUtil.nextInt(50, 60));
							});
						}
					});
				return amount;
			}
		}
	}

	public double drainMana(double mana) {
		if (!world.isBlockLoaded(pos)) return -1;

		IManaCapability cap = getWizardryCap();
		if (cap == null) return -1;

		double amount = MathHelper.clamp(cap.getMana(), 0, mana);

		ManaManager.forObject(cap).removeMana(amount).close();

		return amount;
	}

	public boolean canGiveToOutside() {
		return canGiveToOutside;
	}

	public void setCanGiveToOutside(boolean canGiveToOutside) {
		this.canGiveToOutside = canGiveToOutside;
	}

	public static class SuckRule<K extends TileManaNode, T extends TileManaNode> {

		private final int priority;
		private final double idealAmount;
		private final boolean equalize;
		private final int nbOfConnections;
		private final Class<K> thisClazz;
		private final Class<T> fromClazz;
		@Nullable
		private final BiPredicate<K, T> condition;

		public SuckRule(int priority, double idealAmount, boolean equalize, int nbOfConnections, Class<K> thisClazz, Class<T> fromClazz, @Nullable BiPredicate<K, T> condition) {
			this.priority = priority;
			this.idealAmount = idealAmount;
			this.equalize = equalize;
			this.nbOfConnections = nbOfConnections;
			this.thisClazz = thisClazz;
			this.fromClazz = fromClazz;
			this.condition = condition;
		}

		public double getIdealAmount() {
			return idealAmount;
		}

		public boolean isEqualize() {
			return equalize;
		}

		public Class<K> getThisClazz() {
			return thisClazz;
		}

		public Class<T> getFromClazz() {
			return fromClazz;
		}

		@Nullable
		public BiPredicate<K, T> getCondition() {
			return condition;
		}

		public int getNbOfConnections() {
			return nbOfConnections;
		}

		public int getPriority() {
			return priority;
		}
	}
}
