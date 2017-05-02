package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.IParticleDanger;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellStack.getAllModules;
import static com.teamwizardry.wizardry.api.spell.SpellStack.getModules;

/**
 * Created by LordSaad.
 */
public class PacketRenderSpell extends PacketBase {

	private Module module;
	@Save
	private ItemStack stack;
	@Save
	private NBTTagCompound data;

	public PacketRenderSpell() {
	}

	public PacketRenderSpell(ItemStack stack, SpellData data) {
		this.stack = stack;
		this.data = data.serializeNBT();
	}

	public PacketRenderSpell(Module module, SpellData data) {
		this.module = module;
		this.data = data.serializeNBT();
	}

	@SaveMethodGetter(saveName = "module_saver")
	public NBTTagCompound getter() {
		if (module != null)
			return module.serializeNBT();
		return null;
	}

	@SaveMethodSetter(saveName = "module_saver")
	public void setter(NBTTagCompound compound) {
		if (compound != null) {
			this.module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module != null)
				this.module.deserializeNBT(compound);
		}
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;
		World world = Minecraft.getMinecraft().player.world;

		SpellData data = new SpellData(world);
		data.deserializeNBT(this.data);

		if (stack != null) {
			for (Module module : getModules(stack)) {
				if (module != null) {
					Module tempModule = module;
					while (tempModule != null) {

						int chance = -1;
						if (tempModule instanceof IParticleDanger) {
							if (((IParticleDanger) tempModule).chanceOfParticles() > chance)
								chance = ((IParticleDanger) tempModule).chanceOfParticles();
						}

						if (chance <= 0)
							tempModule.runClient(stack, data);
						else if (ThreadLocalRandom.current().nextInt(chance) == 0)
							tempModule.runClient(stack, data);

						tempModule = tempModule.nextModule;
					}
				}
			}
		} else if (module != null) {
			for (Module module : getAllModules(module)) {
				if (module != null) {
					Module tempModule = module;
					while (tempModule != null) {

						int chance = -1;
						if (tempModule instanceof IParticleDanger) {
							if (((IParticleDanger) tempModule).chanceOfParticles() > chance)
								chance = ((IParticleDanger) tempModule).chanceOfParticles();
						}

						if (chance <= 0)
							tempModule.runClient(null, data);
						else if (ThreadLocalRandom.current().nextInt(chance) == 0)
							tempModule.runClient(null, data);

						tempModule = tempModule.nextModule;
					}
				}
			}
		}
	}
}
