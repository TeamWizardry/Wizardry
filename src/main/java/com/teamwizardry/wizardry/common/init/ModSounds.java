package com.teamwizardry.wizardry.common.init;


import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.spell.component.Interactor;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public final class ModSounds {

    public static SoundEvent GLASS_BREAK = makeSound("glassbreak");
    public static SoundEvent FIZZING_LOOP = makeSound("fizzingloop");
    public static SoundEvent FRYING_SIZZLE = makeSound("firesizzleloop");
    public static SoundEvent BUBBLING = makeSound("bubbling");
    public static SoundEvent HARP1 = makeSound("harp1");
    public static SoundEvent HARP2 = makeSound("harp2");
    public static SoundEvent BELL = makeSound("bell");
    public static SoundEvent HALLOWED_SPIRIT = makeSound("hallowed_spirit_shriek");
    public static SoundEvent EXPLOSION_BOOM = makeSound("expl_boom");
    public static SoundEvent PROJECTILE_LAUNCH = makeSound("proj_launch");
    public static SoundEvent BASS_BOOM = makeSound("bass_boom");
    public static SoundEvent CHAINY_ZAP = makeSound("chainy_zap");
    public static SoundEvent CHORUS_GOOD = makeSound("chorus_good");
    public static SoundEvent COLD_WIND = makeSound("cold_wind");
    public static SoundEvent ELECTRIC_BLAST = makeSound("electric_blast");
    public static SoundEvent ETHEREAL_PASS_BY = makeSound("ethereal_pass_by");
    public static SoundEvent FAIRY = makeSound("fairy_1");
    public static SoundEvent FIRE = makeSound("fire");
    public static SoundEvent FIREBALL = makeSound("fireball");
    public static SoundEvent FLY = makeSound("fly");
    public static SoundEvent FROST_FORM = makeSound("frost_form");
    public static SoundEvent HEAL = makeSound("heal");
    public static SoundEvent LIGHTNING = makeSound("lightning");
    public static SoundEvent SLOW_MOTION_IN = makeSound("slow_motion_in");
    public static SoundEvent SLOW_MOTION_OUT = makeSound("slow_motion_out");
    public static SoundEvent SMOKE_BLAST = makeSound("smoke_blast");
    public static SoundEvent TELEPORT = makeSound("teleport");
    public static SoundEvent THUNDERBLAST = makeSound("thunder_blast");
    public static SoundEvent WIND = makeSound("wind");
    public static SoundEvent ZAP = makeSound("zap");
    public static SoundEvent ELECTRIC_WHITE_NOISE = makeSound("electric_white_noise");
    public static SoundEvent SPARKLE = makeSound("sparkle");
    public static SoundEvent POP = makeSound("pop");
    public static SoundEvent BELL_TING = makeSound("bell_ting");
    public static SoundEvent BUTTON_CLICK_IN = makeSound("button_click_in");
    public static SoundEvent BUTTON_CLICK_OUT = makeSound("button_click_out");
    public static SoundEvent ETHEREAL = makeSound("ethereal");
    public static SoundEvent WHOOSH = makeSound("whoosh");
    public static SoundEvent WING_FLAP = makeSound("wing_flap");
    public static SoundEvent ZOOM = makeSound("zoom");
    public static SoundEvent GOOD_ETHEREAL_CHILLS = makeSound("good_ethereal_chills");
    public static SoundEvent SCRIBBLING = makeSound("scribbling");
    public static SoundEvent SPELL_FAIL = makeSound("spell_fail");
    public static SoundEvent GAS_LEAK = makeSound("gas_leak");
    public static SoundEvent GRACE = makeSound("grace");
    public static SoundEvent SOUND_BOMB = makeSound("sound_bomb");
    public static SoundEvent FIREWORK = makeSound("firework");
    public static SoundEvent MARBLE_EXPLOSION = makeSound("marble_explosion");
    public static SoundEvent SLIME_SQUISHING = makeSound("slime_squishing");
    public static SoundEvent DARK_SPELL_WHISPERS = makeSound("dark_spell_whispers");
    public static SoundEvent DARK_SUCK_N_BLOW = makeSound("dark_suck_n_blow");
    public static SoundEvent ECHOY_HORROR_BREATHE = makeSound("echoy_horror_breathe");
    public static SoundEvent ELECTRIC_WHASHOOSH = makeSound("electric_whashoosh");
    public static SoundEvent ENCHANTED_WHASHOOSH = makeSound("enchanted_whashoosh");
    public static SoundEvent FROST_CRACKLE = makeSound("frost_crackle");
    public static SoundEvent HELLFIRE_LIGHT_MATCH = makeSound("hellfire_light_match");
    public static SoundEvent LARGE_BELL_BOINK = makeSound("large_bell_boink");
    public static SoundEvent HIGH_PITCHED_SOLO_BLEEP = makeSound("high_pitched_solo_bleep");
    public static SoundEvent ICE_BREATHE = makeSound("ice_breathe");
    public static SoundEvent MAGIC_GLINT_LIGHT_BREATHE = makeSound("magic_glint_light_breathe");
    public static SoundEvent NEGATIVELY_PITCHED_BREATHE_PUHH = makeSound("negatively_pitched_breathe_puhh");
    public static SoundEvent POSITIVELY_PITCHED_BREATHE_PUHH = makeSound("positively_pitched_breathe_puhh");
    public static SoundEvent STUTTERY_ELECTRIC_GRILL = makeSound("stuttery_electric_grill");
    public static SoundEvent SUBTLE_MAGIC_BOOK_GLINT = makeSound("subtle_magic_book_glint");
    public static SoundEvent SUDDEN_ANGELIC_SMOKE = makeSound("sudden_angelic_smoke");
    public static SoundEvent SUDDEN_DARK_PAFOOF = makeSound("sudden_dark_pafoof");
    public static SoundEvent TIME_REVERSE = makeSound("time_reverse");
    public static SoundEvent TINY_BELL = makeSound("tiny_bell");
    public static SoundEvent POSITIVE_LIGHT_TWINKLE = makeSound("positive_light_twinkle");

    public static void playSound(World world, Interactor caster, Interactor target, SoundEvent event) {
        playSound(world,
                caster,
                target,
                event,
                0.5f,
                1f);
    }

    public static void playSound(World world, Interactor caster, Interactor target, SoundEvent event, float volume) {
        playSound(world,
                caster,
                target,
                event,
                volume,
                1f);
    }

    public static void playSound(World world, Interactor caster, Interactor target, SoundEvent event, float volume,
                                 float pitch) {
        playSound(world,
                target,
                event,
                caster.getType() == Interactor.InteractorType.ENTITY ?
                        caster.getEntity() instanceof PlayerEntity ?
                                SoundCategory.PLAYERS :
                                caster.getEntity() instanceof MobEntity ?
                                        SoundCategory.HOSTILE :
                                        SoundCategory.NEUTRAL :
                        SoundCategory.BLOCKS,
                volume,
                pitch);
    }

    public static void playSound(World world, Interactor target, SoundEvent event, SoundCategory category) {
        playSound(world, target, event, category, 0.5f, 1f);
    }

    public static void playSound(World world, Interactor target, SoundEvent event, SoundCategory category,
                                 float volume) {
        playSound(world, target, event, category, volume, 1f);
    }

    public static void playSound(World world, Interactor target, SoundEvent event, SoundCategory category, float volume,
                                 float pitch) {
        world.playSound(null, target.getPos().x, target.getPos().y, target.getPos().z, event, category, volume, pitch);
    }

    private static SoundEvent makeSound(String name) {
        Identifier loc = Wizardry.getId(name);
        return new SoundEvent(loc);
    }

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, GLASS_BREAK.getId(), GLASS_BREAK);
        Registry.register(Registry.SOUND_EVENT, FIZZING_LOOP.getId(), FIZZING_LOOP);
        Registry.register(Registry.SOUND_EVENT, FRYING_SIZZLE.getId(), FRYING_SIZZLE);
        Registry.register(Registry.SOUND_EVENT, BUBBLING.getId(), BUBBLING);
        Registry.register(Registry.SOUND_EVENT, HARP1.getId(), HARP1);
        Registry.register(Registry.SOUND_EVENT, HARP2.getId(), HARP2);
        Registry.register(Registry.SOUND_EVENT, BELL.getId(), BELL);
        Registry.register(Registry.SOUND_EVENT, HALLOWED_SPIRIT.getId(), HALLOWED_SPIRIT);
        Registry.register(Registry.SOUND_EVENT, EXPLOSION_BOOM.getId(), EXPLOSION_BOOM);
        Registry.register(Registry.SOUND_EVENT, PROJECTILE_LAUNCH.getId(), PROJECTILE_LAUNCH);
        Registry.register(Registry.SOUND_EVENT, BASS_BOOM.getId(), BASS_BOOM);
        Registry.register(Registry.SOUND_EVENT, CHAINY_ZAP.getId(), CHAINY_ZAP);
        Registry.register(Registry.SOUND_EVENT, CHORUS_GOOD.getId(), CHORUS_GOOD);
        Registry.register(Registry.SOUND_EVENT, COLD_WIND.getId(), COLD_WIND);
        Registry.register(Registry.SOUND_EVENT, ELECTRIC_BLAST.getId(), ELECTRIC_BLAST);
        Registry.register(Registry.SOUND_EVENT, ETHEREAL_PASS_BY.getId(), ETHEREAL_PASS_BY);
        Registry.register(Registry.SOUND_EVENT, FAIRY.getId(), FAIRY);
        Registry.register(Registry.SOUND_EVENT, FIRE.getId(), FIRE);
        Registry.register(Registry.SOUND_EVENT, FIREBALL.getId(), FIREBALL);
        Registry.register(Registry.SOUND_EVENT, FLY.getId(), FLY);
        Registry.register(Registry.SOUND_EVENT, FROST_FORM.getId(), FROST_FORM);
        Registry.register(Registry.SOUND_EVENT, HEAL.getId(), HEAL);
        Registry.register(Registry.SOUND_EVENT, LIGHTNING.getId(), LIGHTNING);
        Registry.register(Registry.SOUND_EVENT, SLOW_MOTION_IN.getId(), SLOW_MOTION_IN);
        Registry.register(Registry.SOUND_EVENT, SLOW_MOTION_OUT.getId(), SLOW_MOTION_OUT);
        Registry.register(Registry.SOUND_EVENT, SMOKE_BLAST.getId(), SMOKE_BLAST);
        Registry.register(Registry.SOUND_EVENT, TELEPORT.getId(), TELEPORT);
        Registry.register(Registry.SOUND_EVENT, THUNDERBLAST.getId(), THUNDERBLAST);
        Registry.register(Registry.SOUND_EVENT, WIND.getId(), WIND);
        Registry.register(Registry.SOUND_EVENT, ZAP.getId(), ZAP);
        Registry.register(Registry.SOUND_EVENT, ELECTRIC_WHITE_NOISE.getId(), ELECTRIC_WHITE_NOISE);
        Registry.register(Registry.SOUND_EVENT, SPARKLE.getId(), SPARKLE);
        Registry.register(Registry.SOUND_EVENT, POP.getId(), POP);
        Registry.register(Registry.SOUND_EVENT, BELL_TING.getId(), BELL_TING);
        Registry.register(Registry.SOUND_EVENT, BUTTON_CLICK_IN.getId(), BUTTON_CLICK_IN);
        Registry.register(Registry.SOUND_EVENT, BUTTON_CLICK_OUT.getId(), BUTTON_CLICK_OUT);
        Registry.register(Registry.SOUND_EVENT, ETHEREAL.getId(), ETHEREAL);
        Registry.register(Registry.SOUND_EVENT, WHOOSH.getId(), WHOOSH);
        Registry.register(Registry.SOUND_EVENT, WING_FLAP.getId(), WING_FLAP);
        Registry.register(Registry.SOUND_EVENT, ZOOM.getId(), ZOOM);
        Registry.register(Registry.SOUND_EVENT, GOOD_ETHEREAL_CHILLS.getId(), GOOD_ETHEREAL_CHILLS);
        Registry.register(Registry.SOUND_EVENT, SCRIBBLING.getId(), SCRIBBLING);
        Registry.register(Registry.SOUND_EVENT, SPELL_FAIL.getId(), SPELL_FAIL);
        Registry.register(Registry.SOUND_EVENT, GAS_LEAK.getId(), GAS_LEAK);
        Registry.register(Registry.SOUND_EVENT, GRACE.getId(), GRACE);
        Registry.register(Registry.SOUND_EVENT, SOUND_BOMB.getId(), SOUND_BOMB);
        Registry.register(Registry.SOUND_EVENT, FIREWORK.getId(), FIREWORK);
        Registry.register(Registry.SOUND_EVENT, MARBLE_EXPLOSION.getId(), MARBLE_EXPLOSION);
        Registry.register(Registry.SOUND_EVENT, SLIME_SQUISHING.getId(), SLIME_SQUISHING);
        Registry.register(Registry.SOUND_EVENT, DARK_SPELL_WHISPERS.getId(), DARK_SPELL_WHISPERS);
        Registry.register(Registry.SOUND_EVENT, DARK_SUCK_N_BLOW.getId(), DARK_SUCK_N_BLOW);
        Registry.register(Registry.SOUND_EVENT, ECHOY_HORROR_BREATHE.getId(), ECHOY_HORROR_BREATHE);
        Registry.register(Registry.SOUND_EVENT, ELECTRIC_WHASHOOSH.getId(), ELECTRIC_WHASHOOSH);
        Registry.register(Registry.SOUND_EVENT, ENCHANTED_WHASHOOSH.getId(), ENCHANTED_WHASHOOSH);
        Registry.register(Registry.SOUND_EVENT, FROST_CRACKLE.getId(), FROST_CRACKLE);
        Registry.register(Registry.SOUND_EVENT, HELLFIRE_LIGHT_MATCH.getId(), HELLFIRE_LIGHT_MATCH);
        Registry.register(Registry.SOUND_EVENT, LARGE_BELL_BOINK.getId(), LARGE_BELL_BOINK);
        Registry.register(Registry.SOUND_EVENT, HIGH_PITCHED_SOLO_BLEEP.getId(), HIGH_PITCHED_SOLO_BLEEP);
        Registry.register(Registry.SOUND_EVENT, ICE_BREATHE.getId(), ICE_BREATHE);
        Registry.register(Registry.SOUND_EVENT, MAGIC_GLINT_LIGHT_BREATHE.getId(), MAGIC_GLINT_LIGHT_BREATHE);
        Registry.register(Registry.SOUND_EVENT, NEGATIVELY_PITCHED_BREATHE_PUHH.getId(), NEGATIVELY_PITCHED_BREATHE_PUHH);
        Registry.register(Registry.SOUND_EVENT, POSITIVELY_PITCHED_BREATHE_PUHH.getId(), POSITIVELY_PITCHED_BREATHE_PUHH);
        Registry.register(Registry.SOUND_EVENT, STUTTERY_ELECTRIC_GRILL.getId(), STUTTERY_ELECTRIC_GRILL);
        Registry.register(Registry.SOUND_EVENT, SUBTLE_MAGIC_BOOK_GLINT.getId(), SUBTLE_MAGIC_BOOK_GLINT);
        Registry.register(Registry.SOUND_EVENT, SUDDEN_ANGELIC_SMOKE.getId(), SUDDEN_ANGELIC_SMOKE);
        Registry.register(Registry.SOUND_EVENT, SUDDEN_DARK_PAFOOF.getId(), SUDDEN_DARK_PAFOOF);
        Registry.register(Registry.SOUND_EVENT, TIME_REVERSE.getId(), TIME_REVERSE);
        Registry.register(Registry.SOUND_EVENT, TINY_BELL.getId(), TINY_BELL);
        Registry.register(Registry.SOUND_EVENT, POSITIVE_LIGHT_TWINKLE.getId(), POSITIVE_LIGHT_TWINKLE);
    }
}