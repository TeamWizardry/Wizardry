package me.lordsaad.wizardry.event;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "items/manaIconNoOutline"));
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "items/manaIconOutline"));
    }
}
