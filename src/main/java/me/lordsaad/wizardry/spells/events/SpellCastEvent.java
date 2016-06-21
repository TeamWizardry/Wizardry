package me.lordsaad.wizardry.spells.events;

import me.lordsaad.wizardry.api.modules.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;

/**
 * Created by Saad on 6/21/2016.
 */
public class SpellCastEvent extends Event {

    private ArrayList<IModule> recipe;
    private EntityPlayer player;

    public SpellCastEvent(ArrayList<IModule> recipe, EntityPlayer player) {
        this.recipe = recipe;
        this.player = player;
    }
}
