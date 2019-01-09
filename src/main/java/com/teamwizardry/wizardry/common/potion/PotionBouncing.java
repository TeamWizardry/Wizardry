package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.SlimeBounceHandler;
import com.teamwizardry.wizardry.common.network.PacketBounce;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionBouncing extends PotionBase {

	public PotionBouncing() {
		super("bouncing", false, 0xABFCF0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	/**
	 * Code "borrowed" from Tinker's construct slime boots
	 * https://github.com/SlimeKnights/TinkersConstruct/blob/23034cb63e98bba06faf1cdc4074009daf93be1f/src/main/java/slimeknights/tconstruct/gadgets/item/ItemSlimeBoots.java
	 * <p>
	 * I don't feel like re-inventing the wheel. Shut up.
	 */
	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.isPotionActive(this)) return;

		boolean isClient = entity.getEntityWorld().isRemote;
		if (event.getDistance() > 2) {
			event.setDamageMultiplier(0);
			entity.fallDistance = 0;
			if (isClient) {
				entity.motionY *= -0.9;
				entity.isAirBorne = true;
				entity.onGround = false;
				double f = 0.91d + 0.04d;
				entity.motionX /= f;
				entity.motionZ /= f;
				PacketHandler.NETWORK.sendToServer(new PacketBounce());
			}

			entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
			SlimeBounceHandler.addBounceHandler(entity, entity.motionY);
		} else if (!isClient && entity.isSneaking()) {
			event.setDamageMultiplier(0.2f);
		}
	}
}
