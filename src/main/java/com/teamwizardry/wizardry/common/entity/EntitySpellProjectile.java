package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.common.module.events.ModuleEventAlongPath;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by LordSaad.
 */
public class EntitySpellProjectile extends EntityThrowable {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.VARINT);
	public SpellData spell;
	public Module module;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;
		applyColor(Color.WHITE);
	}

	public EntitySpellProjectile(World world, Module module, SpellData spell) {
		super(world);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;

		this.module = module;
		this.spell = spell;

		if (module != null && module.getColor() != null) applyColor(module.getColor());
		else applyColor(Color.WHITE);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(DATA_COLOR, 0);
	}

	private void applyColor(Color color) {
		this.getDataManager().set(DATA_COLOR, color.getRGB());
		this.getDataManager().setDirty(DATA_COLOR);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 1000) {
			setDead();
			return;
		}

		if (world.isRemote) return;
		if (module == null) return;

		if (module.nextModule != null) {
			Module nextModule = module.nextModule;
			if (nextModule instanceof ModuleEventAlongPath) {
				nextModule.run(spell);
			}
		}
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (module != null && module.nextModule != null) {
			Module nextModule = module.nextModule;
			SpellData newSpell = spell.copy();
			if (result.typeOfHit == RayTraceResult.Type.ENTITY)
				newSpell.crunchData(result.entityHit, false);
			else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
				newSpell.addData(SpellData.DefaultKeys.BLOCK_HIT, result.getBlockPos());
				newSpell.addData(SpellData.DefaultKeys.TARGET_HIT, result.hitVec);
			}
			nextModule.run(newSpell);
			setDead();
			LibParticles.FAIRY_EXPLODE(world, result.hitVec, module.getColor() == null ? Color.WHITE : module.getColor());
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		NBTTagCompound moduleCompound = compound.getCompoundTag("module");
		Module module = ModuleRegistry.INSTANCE.getModule(moduleCompound.getString("id"));
		if (module != null) {
			module.deserializeNBT(compound);
			Module.process(module);
			this.module = module;
		}

		spell = new SpellData(world);
		spell.deserializeNBT(compound.getCompoundTag("spell_data"));
		applyColor(new Color(compound.getInteger("color")));
	}

	@Override
	public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
	}
}
