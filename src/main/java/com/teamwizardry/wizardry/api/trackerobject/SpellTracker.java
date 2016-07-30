package com.teamwizardry.wizardry.api.trackerobject;

import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.LinkedList;
import java.util.Queue;

public class SpellTracker {
    private static Queue<Entity> spellEntities;
    private static Queue<EntityPlayer> spellCasters;
    private static Queue<NBTTagCompound> spellData;

    private static Queue<Entity> addedEntities;
    private static Queue<EntityPlayer> addedCasters;
    private static Queue<NBTTagCompound> addedData;

    public static void init() {
        spellEntities = new LinkedList<>();
        spellCasters = new LinkedList<>();
        spellData = new LinkedList<>();
        addedEntities = new LinkedList<>();
        addedCasters = new LinkedList<>();
        addedData = new LinkedList<>();
    }

    public static boolean addSpell(EntityPlayer caster, Entity source, NBTTagCompound spell) {
        if (caster == null) return false;
        if (source == null) return false;
        if (spell == null) return false;

        addedEntities.add(source);
        addedCasters.add(caster);
        addedData.add(spell);
        return true;
    }

    @SubscribeEvent
    public static void onUpdateTick(WorldTickEvent event) {
        while (!spellEntities.isEmpty() && !spellCasters.isEmpty() && !spellData.isEmpty()) {
            SpellCastEvent cast = new SpellCastEvent(spellData.remove(), spellEntities.remove(), spellCasters.remove());
            MinecraftForge.EVENT_BUS.post(cast);
        }
        spellEntities.clear();
        spellCasters.clear();
        spellData.clear();
        while (!addedEntities.isEmpty()) {
            spellEntities.add(addedEntities.remove());
        }
        while (!addedCasters.isEmpty()) {
            spellCasters.add(addedCasters.remove());
        }
        while (!addedData.isEmpty()) {
            spellData.add(addedData.remove());
        }
    }
}
