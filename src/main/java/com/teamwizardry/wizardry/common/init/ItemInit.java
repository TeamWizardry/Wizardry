package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.lib.LibItemNames;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {

	public static final Item wisdomStick = new Item(defaultBuilder()) {
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            playerIn.setActiveHand(handIn);
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }

        @Override
        public UseAction getUseAction(ItemStack stack) {
            return UseAction.BOW;
        }

        @Override
        public int getUseDuration(ItemStack stack) {
            return 1000;
        }

        @Override
        public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
            if(player.getEntityWorld().isRemote)
                Wizardry.proxy.spawnParticle(player);
        }
    };


	public static Item.Properties defaultBuilder() {
		return new Item.Properties().group(ModItemGroup.INSTANCE);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(wisdomStick.setRegistryName(Wizardry.MODID, LibItemNames.WISDOM_STICK));

	}
}
