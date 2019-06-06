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
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.capability.mana.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.ManaModule;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

public class TileManaInteractor extends TileCachable implements ITickable {

	private static Set<SuckRule> suckRules = new HashSet<>();

	static {
		addSuckRule(new SuckRule<>(1, true, 1, TilePearlHolder.class, TilePearlHolder.class, (tilePearlHolder, tilePearlHolder2) ->
				(tilePearlHolder.structurePos == null && tilePearlHolder2.structurePos == null)
						|| (tilePearlHolder.structurePos != null && tilePearlHolder2.structurePos != null && tilePearlHolder.structurePos.equals(tilePearlHolder2.structurePos))
						|| (tilePearlHolder.structurePos != null && tilePearlHolder2.structurePos == null)));

		addSuckRule(new SuckRule<>(1, false, 1, TilePearlHolder.class, TileManaBattery.class, (tilePearlHolder, tileManaBattery) ->
				tilePearlHolder.structurePos == null || !tilePearlHolder.structurePos.equals(tileManaBattery.getPos())));

		addSuckRule(new SuckRule<>(1, false, 20, TileManaBattery.class, TilePearlHolder.class, (tileManaBattery, tilePearlHolder) ->
				tilePearlHolder.structurePos != null && tilePearlHolder.structurePos.equals(tileManaBattery.getPos()),
				true));

		addSuckRule(new SuckRule<>(0.25, false, 4, TileCraftingPlate.class, TilePearlHolder.class, (tileCraftingPlate, tilePearlHolder) ->
				tilePearlHolder.structurePos != null && tilePearlHolder.structurePos.equals(tileCraftingPlate.getPos())));
	}

	@Module
	public ManaModule cap;
	@Save
	public boolean allowOutsideSucking = true;

	public TileManaInteractor(double maxMana, double maxBurnout) {
		cap = new ManaModule(new CustomWizardryCapability(maxMana, maxBurnout));
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

	public double suckMana(IWizardryCapability cap) {
		double totalZucced = 0;

		for (SuckRule suckRule : suckRules) {
			if (getClass().isAssignableFrom(suckRule.thisClazz)) {

				ArrayList<TileManaInteractor> interactables = new ArrayList<TileManaInteractor>(getNearestTiles(suckRule.fromClazz));
				interactables.sort(Comparator.comparingDouble(this::getCachedDistanceSq));

				int i = 0;
				for (TileManaInteractor interacter : interactables) {
					double zucced = suckManaFrom(interacter, suckRule, cap);
					if (zucced > 0) {
						totalZucced += zucced;

						// Trigger events to notify
						interacter.onDrainedFrom(this);
						onSuckFrom(interacter);

						if (++i > suckRule.getNbOfConnections()) break;
					}
				}
			}
		}
		return totalZucced;
	}

	@Nullable
	public IWizardryCapability getWizardryCap() {
		return cap.getHandler();
	}

	public void onDrainedFrom(TileManaInteractor from) {

	}

	public void onSuckFrom(TileManaInteractor from) {

	}

	@Nonnull
	public Vec3d getOffset() {
		return Vec3d.ZERO;
	}

	public boolean isAllowOutsideSucking() {
		return allowOutsideSucking;
	}

	public void setAllowOutsideSucking(boolean allowOutsideSucking) {
		this.allowOutsideSucking = allowOutsideSucking;
	}

	public double suckManaFrom(TileManaInteractor interacterFrom, SuckRule suckRule, IWizardryCapability cap) {

		if (cap == null || interacterFrom.getWizardryCap() == null) return 0;
		if (!isAllowOutsideSucking() && interacterFrom.isAllowOutsideSucking()) return 0;
		if (!suckRule.condition.test(this, interacterFrom)) return 0;

		try (CapManager.CapManagerBuilder thisMgr = CapManager.forObject(cap)) {
			try (CapManager.CapManagerBuilder theirMgr = CapManager.forObject(interacterFrom.getWizardryCap())) {

				if (thisMgr.isManaFull()) return 0;
				if (theirMgr.isManaEmpty()) return 0;

				if (suckRule.equalize && Math.abs(thisMgr.getMana() - theirMgr.getMana()) <= suckRule.idealAmount)
					return 0;

				double ratio = theirMgr.getMana() / thisMgr.getMana();

				if (suckRule.equalize && Double.isFinite(ratio) && ratio <= 1.2)
					return 0;

				double amount = interacterFrom.drainMana(suckRule.idealAmount);
				if (amount <= 0) return 0;

				CapManager.forObject(cap).addMana(amount).close();

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

		IWizardryCapability cap = getWizardryCap();
		if (cap == null) return -1;

		double amount = MathHelper.clamp(cap.getMana(), 0, mana);

		CapManager.forObject(cap).removeMana(amount).close();

		return amount;
	}

	public static class SuckRule<K extends TileManaInteractor, T extends TileManaInteractor> {

		private final double idealAmount;
		private final boolean equalize;
		private final int nbOfConnections;
		private final Class<K> thisClazz;
		private final Class<T> fromClazz;
		@Nullable
		private final BiPredicate<K, T> condition;

		public SuckRule(double idealAmount, boolean equalize, int nbOfConnections, Class<K> thisClazz, Class<T> fromClazz, @Nullable BiPredicate<K, T> condition) {
			this.idealAmount = idealAmount;
			this.equalize = equalize;
			this.nbOfConnections = nbOfConnections;
			this.thisClazz = thisClazz;
			this.fromClazz = fromClazz;
			this.condition = condition;
		}

		public SuckRule(double idealAmount, boolean equalize, int nbOfConnections, Class<K> thisClazz, Class<T> fromClazz, @Nullable BiPredicate<K, T> condition, boolean ignoreTrace) {
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
	}
}
