package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.common.entity.EntityStaffFakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import static com.teamwizardry.wizardry.api.spell.SpellStack.getModules;

/**
 * Created by LordSaad.
 */
public class PacketRenderSpell extends PacketBase {

	@Save
	private ItemStack stack;
	@Save
	private float rotationPitch;
	@Save
	private float rotationYaw;
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

	public PacketRenderSpell(ItemStack stack, int casterID, @NotNull Vec3d pos, float rotationPitch, float rotationYaw) {
		this.stack = stack;
		this.rotationPitch = rotationPitch;
		this.rotationYaw = rotationYaw;
		this.pos = pos;
		this.casterID = casterID;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;
		World world = Minecraft.getMinecraft().player.world;

		if (casterID == -1) {
			FakePlayer caster = new EntityStaffFakePlayer((WorldServer) world);
			caster.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
			caster.rotationYaw = rotationYaw;
			caster.rotationPitch = rotationPitch;

			for (Module module : getModules(stack)) {
				if (module != null) {
					Module tempModule = module;
					while (tempModule != null) {
						tempModule.runClient(world, stack, caster, pos);
						tempModule = tempModule.nextModule;
					}
				}
			}

		} else {
			EntityLivingBase caster = (EntityLivingBase) world.getEntityByID(casterID);

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
}
