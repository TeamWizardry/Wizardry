package me.lordsaad.wizardry.spells.events;

import me.lordsaad.wizardry.api.IPearlable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 6/21/2016.
 */
public class EventHandler {

    // TODO
    @SubscribeEvent
    public void onSpellCast(PlayerInteractEvent event) {
        if (event.getItemStack() == null) return;
        if (event.getItemStack().getItem() instanceof IPearlable) {
            //     Wizardry.EVENT_BUS.post(new SpellCastEvent(((IPearlable) event.getItemStack().getItem()).getRecipe), event.getEntityPlayer());
        }
    }
}
