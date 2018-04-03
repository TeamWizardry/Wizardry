package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.ManaModule;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.function.BiPredicate;

public class TileManaInteractor extends TileMod implements ITickable, IManaInteractable {

	private static Set<SuckRule> suckRules = new HashSet<>();

	private static WeakHashMap<TileManaInteractor, WeakHashMap<TileManaInteractor, Double>> MANA_INTERACTABLES = new WeakHashMap<>();

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
	@Save
	public int suckingCooldown = 0;

	public TileManaInteractor(double maxMana, double maxBurnout) {
		cap = new ManaModule(new CustomWizardryCapability(maxMana, maxBurnout));
	}

	public static void addSuckRule(SuckRule suckRule) {
		suckRules.add(suckRule);
	}

	@Override
	public void update() {
		if (world.isRemote) return;

		if (suckingCooldown > 0) {
			suckingCooldown--;
			markDirty();
		}

		MANA_INTERACTABLES.putIfAbsent(this, new WeakHashMap<>());

		WeakHashMap<TileManaInteractor, Double> distanceCache = MANA_INTERACTABLES.get(this);

		if (distanceCache.isEmpty() && MANA_INTERACTABLES.size() > 1) {

			for (TileManaInteractor key : MANA_INTERACTABLES.keySet()) {
				if (key == this) continue;

				double distance = key.getPos().distanceSq(getPos());
				if (distance <= ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance) {
					distanceCache.put(key, distance);

					WeakHashMap<TileManaInteractor, Double> otherDistanceCache = MANA_INTERACTABLES.get(key);
					if (otherDistanceCache != null) {
						otherDistanceCache.put(this, distance);
					}
				}
			}
		}

		for (SuckRule suckRule : suckRules) {
			if (getClass().isAssignableFrom(suckRule.thisClazz)) {

				ArrayList<TileManaInteractor> interactables = new ArrayList<>(getNearestInteractables(suckRule.fromClazz));
				interactables.sort(Comparator.comparingDouble(this::getCachedDistanceSq));

				int i = 0;
				for (TileManaInteractor interacter : interactables) {
					if (suckManaFrom(interacter, suckRule)) {
						suckingCooldown = 10;
						interacter.onDrainedFrom(this);
						onSuckFrom(interacter);

						interacter.markDirty();
						markDirty();

						if (++i > suckRule.getNbOfConnections()) break;
					}
				}
			}
		}
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

	public double getCachedDistanceSq(TileManaInteractor interacter) {
		WeakHashMap<TileManaInteractor, Double> map = MANA_INTERACTABLES.get(this);
		if (map == null) return Double.MAX_VALUE;
		return map.getOrDefault(interacter, Double.MAX_VALUE);
	}

	public boolean suckManaFrom(TileManaInteractor interacterFrom, SuckRule suckRule) {
		if (getWizardryCap() == null || interacterFrom.getWizardryCap() == null) return false;

		if (!isAllowOutsideSucking() && interacterFrom.isAllowOutsideSucking()) return false;
		if (!suckRule.condition.test(this, interacterFrom)) return false;

		CapManager thisManager = new CapManager(getWizardryCap());
		CapManager theirManager = new CapManager(interacterFrom.getWizardryCap());

		if (thisManager.isManaFull()) return false;
		if (theirManager.isManaEmpty()) return false;
		if (suckRule.equalize && Math.abs(thisManager.getMana() - theirManager.getMana()) <= suckRule.idealAmount)
			return false;

		Vec3d thisPos = new Vec3d(getPos()).addVector(0.5, 0.5, 0.5);
		Vec3d theirPos = new Vec3d(interacterFrom.getPos()).addVector(0.5, 0.5, 0.5);

		// DEBUG
		//ParticleBuilder helix = new ParticleBuilder(200);
		//helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		//helix.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));
		//ParticleSpawner.spawn(helix, world, new StaticInterp<>(thisPos), 1, 0, (someFloat, particleBuilder) -> {
		//	particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0xFF0000), RandUtil.nextInt(50, 200)));
		//	particleBuilder.setScale(RandUtil.nextFloat(0.3f, 0.8f));
		//	particleBuilder.disableRandom();
		//	particleBuilder.setPositionFunction(new InterpLine(Vec3d.ZERO, theirPos.subtract(thisPos)));
		//	particleBuilder.setLifetime(RandUtil.nextInt(50, 60));
		//});


		double ratio = theirManager.getMana() / thisManager.getMana();

		if (suckRule.equalize && Double.isFinite(ratio) && ratio <= 1.2)
			return false;

		if (getCachedDistanceSq(interacterFrom) > ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance)
			return false;

		if (!suckRule.ignoreTrace) {
			Vec3d thisSub = theirPos.subtract(thisPos.add(getOffset()));
			Vec3d thisNorm = thisSub.normalize();

			RayTraceResult trace = new RayTrace(world, thisNorm, thisPos.add(getOffset() == Vec3d.ZERO ? thisNorm : getOffset()), ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance)
					.setSkipEntities(true)
					.setIgnoreBlocksWithoutBoundingBoxes(true)
					.trace();

			if (!trace.getBlockPos().equals(interacterFrom.getPos())) return false;
		}

		double amount = interacterFrom.drainMana(suckRule.idealAmount);
		if (amount <= 0) return false;

		thisManager.addMana(amount);

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ParticleBuilder helix = new ParticleBuilder(200);
				helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				helix.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));
				ParticleSpawner.spawn(helix, world, new StaticInterp<>(new Vec3d(interacterFrom.getPos()).addVector(0.5, 1, 0.5)), 1, 0, (someFloat, particleBuilder) -> {
					particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
					particleBuilder.setScale(RandUtil.nextFloat(0.3f, 0.8f));
					particleBuilder.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(getPos().subtract(interacterFrom.getPos())), new Vec3d(0, 5, 0), new Vec3d(0, -5, 0)));
					particleBuilder.setLifetime(RandUtil.nextInt(50, 60));
				});
			}
		});
		return true;
	}

	public double drainMana(double mana) {
		if (!world.isBlockLoaded(pos)) return -1;

		IWizardryCapability cap = getWizardryCap();
		if (cap == null) return -1;

		double amount = MathHelper.clamp(cap.getMana(), 0, mana);

		CapManager manager = new CapManager(cap);
		manager.removeMana(amount);

		return amount;
	}

	public <T extends TileManaInteractor> Set<T> getNearestInteractables(Class<T> clazz) {
		Set<T> poses = new HashSet<>();
		for (TileManaInteractor target : MANA_INTERACTABLES.keySet()) {
			if (target == this) continue;
			if (!world.isBlockLoaded(target.getPos())) continue;
			if (getCachedDistanceSq(target) > ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance)
				continue;

			if (!(target.getClass().isAssignableFrom(clazz))) continue;
			poses.add((T) target);
		}
		return poses;
	}

	public <T extends TileManaInteractor> Set<BlockPos> getNearestInteractablesPoses(Class<T> clazz) {
		Set<BlockPos> poses = new HashSet<>();
		Set<TileManaInteractor> temp = new HashSet<>(MANA_INTERACTABLES.keySet());
		for (TileManaInteractor target : temp) {
			if (target == this) continue;
			if (!world.isBlockLoaded(target.getPos())) continue;
			if (getCachedDistanceSq(target) > ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance)
				continue;

			if (!(target.getClass().isAssignableFrom(clazz))) continue;
			poses.add(target.getPos());
		}
		return poses;
	}

	public static class SuckRule<K extends TileManaInteractor, T extends TileManaInteractor> {

		private final double idealAmount;
		private final boolean equalize;
		private final int nbOfConnections;
		private final Class<K> thisClazz;
		private final Class<T> fromClazz;
		@Nullable
		private final BiPredicate<K, T> condition;
		private boolean ignoreTrace;

		public SuckRule(double idealAmount, boolean equalize, int nbOfConnections, Class<K> thisClazz, Class<T> fromClazz, @Nullable BiPredicate<K, T> condition) {
			this.idealAmount = idealAmount;
			this.equalize = equalize;
			this.nbOfConnections = nbOfConnections;
			this.thisClazz = thisClazz;
			this.fromClazz = fromClazz;
			this.condition = condition;
			this.ignoreTrace = false;
		}

		public SuckRule(double idealAmount, boolean equalize, int nbOfConnections, Class<K> thisClazz, Class<T> fromClazz, @Nullable BiPredicate<K, T> condition, boolean ignoreTrace) {
			this.idealAmount = idealAmount;
			this.equalize = equalize;
			this.nbOfConnections = nbOfConnections;
			this.thisClazz = thisClazz;
			this.fromClazz = fromClazz;
			this.condition = condition;
			this.ignoreTrace = ignoreTrace;
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

		public boolean isIgnoreTrace() {
			return ignoreTrace;
		}

		public int getNbOfConnections() {
			return nbOfConnections;
		}
	}
}
