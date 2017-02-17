package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.spell.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.teamwizardry.wizardry.api.spell.SpellStack.getModules;

/**
 * Created by LordSaad.
 */
public class PacketRenderSpell extends PacketBase {

	@Save
	private ItemStack stack;
	@Save
	private int casterID;
	@Save
	private Vec3d pos;

	public PacketRenderSpell() {
	}

	public PacketRenderSpell(ItemStack stack, int casterID, Vec3d pos) {
		this.stack = stack;
		this.casterID = casterID;
		this.pos = pos;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;
		World world = Minecraft.getMinecraft().player.world;

		EntityLivingBase caster;
		if (casterID != -1) caster = (EntityLivingBase) world.getEntityByID(casterID);
		else caster = null;

		for (Module module : getModules(stack)) {
			if (module != null) {
				Module tempModule = module;
				while (tempModule != null) {
					tempModule.runClient(world, stack, caster, pos);
					tempModule = tempModule.nextModule;
				}
			}
		}
	}
}
