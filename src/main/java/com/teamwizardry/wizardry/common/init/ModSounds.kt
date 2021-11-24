package com.teamwizardry.wizardry.common.init

import com.teamwizardry.librarianlib.math.Vec2d.x
import com.teamwizardry.librarianlib.math.Vec2d.y
import com.teamwizardry.wizardry.Wizardry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ModSounds {
    private var GLASS_BREAK: SoundEvent = makeSound("glassbreak")
    private var FIZZING_LOOP: SoundEvent = makeSound("fizzingloop")
    private var FRYING_SIZZLE: SoundEvent = makeSound("firesizzleloop")
    private var BUBBLING: SoundEvent = makeSound("bubbling")
    private var HARP1: SoundEvent = makeSound("harp1")
    private var HARP2: SoundEvent = makeSound("harp2")
    private var BELL: SoundEvent = makeSound("bell")
    private var HALLOWED_SPIRIT: SoundEvent = makeSound("hallowed_spirit_shriek")
    private var EXPLOSION_BOOM: SoundEvent = makeSound("expl_boom")
    private var PROJECTILE_LAUNCH: SoundEvent = makeSound("proj_launch")
    private var BASS_BOOM: SoundEvent = makeSound("bass_boom")
    private var CHAINY_ZAP: SoundEvent = makeSound("chainy_zap")
    private var CHORUS_GOOD: SoundEvent = makeSound("chorus_good")
    private var COLD_WIND: SoundEvent = makeSound("cold_wind")
    private var ELECTRIC_BLAST: SoundEvent = makeSound("electric_blast")
    private var ETHEREAL_PASS_BY: SoundEvent = makeSound("ethereal_pass_by")
    private var FAIRY: SoundEvent = makeSound("fairy_1")
    private var FIRE: SoundEvent = makeSound("fire")
    private var FIREBALL: SoundEvent = makeSound("fireball")
    private var FLY: SoundEvent = makeSound("fly")
    private var FROST_FORM: SoundEvent = makeSound("frost_form")
    private var HEAL: SoundEvent = makeSound("heal")
    private var LIGHTNING: SoundEvent = makeSound("lightning")
    private var SLOW_MOTION_IN: SoundEvent = makeSound("slow_motion_in")
    private var SLOW_MOTION_OUT: SoundEvent = makeSound("slow_motion_out")
    private var SMOKE_BLAST: SoundEvent = makeSound("smoke_blast")
    private var TELEPORT: SoundEvent = makeSound("teleport")
    private var THUNDERBLAST: SoundEvent = makeSound("thunder_blast")
    private var WIND: SoundEvent = makeSound("wind")
    private var ZAP: SoundEvent = makeSound("zap")
    private var ELECTRIC_WHITE_NOISE: SoundEvent = makeSound("electric_white_noise")
    private var SPARKLE: SoundEvent = makeSound("sparkle")
    private var POP: SoundEvent = makeSound("pop")
    private var BELL_TING: SoundEvent = makeSound("bell_ting")
    private var BUTTON_CLICK_IN: SoundEvent = makeSound("button_click_in")
    private var BUTTON_CLICK_OUT: SoundEvent = makeSound("button_click_out")
    private var ETHEREAL: SoundEvent = makeSound("ethereal")
    private var WHOOSH: SoundEvent = makeSound("whoosh")
    private var WING_FLAP: SoundEvent = makeSound("wing_flap")
    private var ZOOM: SoundEvent = makeSound("zoom")
    private var GOOD_ETHEREAL_CHILLS: SoundEvent = makeSound("good_ethereal_chills")
    private var SCRIBBLING: SoundEvent = makeSound("scribbling")
    private var SPELL_FAIL: SoundEvent = makeSound("spell_fail")
    private var GAS_LEAK: SoundEvent = makeSound("gas_leak")
    private var GRACE: SoundEvent = makeSound("grace")
    private var SOUND_BOMB: SoundEvent = makeSound("sound_bomb")
    private var FIREWORK: SoundEvent = makeSound("firework")
    private var MARBLE_EXPLOSION: SoundEvent = makeSound("marble_explosion")
    private var SLIME_SQUISHING: SoundEvent = makeSound("slime_squishing")
    private var DARK_SPELL_WHISPERS: SoundEvent = makeSound("dark_spell_whispers")
    private var DARK_SUCK_N_BLOW: SoundEvent = makeSound("dark_suck_n_blow")
    private var ECHOY_HORROR_BREATHE: SoundEvent = makeSound("echoy_horror_breathe")
    private var ELECTRIC_WHASHOOSH: SoundEvent = makeSound("electric_whashoosh")
    private var ENCHANTED_WHASHOOSH: SoundEvent = makeSound("enchanted_whashoosh")
    private var FROST_CRACKLE: SoundEvent = makeSound("frost_crackle")
    private var HELLFIRE_LIGHT_MATCH: SoundEvent = makeSound("hellfire_light_match")
    private var LARGE_BELL_BOINK: SoundEvent = makeSound("large_bell_boink")
    private var HIGH_PITCHED_SOLO_BLEEP: SoundEvent = makeSound("high_pitched_solo_bleep")
    private var ICE_BREATHE: SoundEvent = makeSound("ice_breathe")
    private var MAGIC_GLINT_LIGHT_BREATHE: SoundEvent = makeSound("magic_glint_light_breathe")
    private var NEGATIVELY_PITCHED_BREATHE_PUHH: SoundEvent = makeSound("negatively_pitched_breathe_puhh")
    private var POSITIVELY_PITCHED_BREATHE_PUHH: SoundEvent = makeSound("positively_pitched_breathe_puhh")
    private var STUTTERY_ELECTRIC_GRILL: SoundEvent = makeSound("stuttery_electric_grill")
    private var SUBTLE_MAGIC_BOOK_GLINT: SoundEvent = makeSound("subtle_magic_book_glint")
    private var SUDDEN_ANGELIC_SMOKE: SoundEvent = makeSound("sudden_angelic_smoke")
    private var SUDDEN_DARK_PAFOOF: SoundEvent = makeSound("sudden_dark_pafoof")
    private var TIME_REVERSE: SoundEvent = makeSound("time_reverse")
    private var TINY_BELL: SoundEvent = makeSound("tiny_bell")
    private var POSITIVE_LIGHT_TWINKLE: SoundEvent = makeSound("positive_light_twinkle")

    @JvmOverloads
    fun playSound(
        world: World?, caster: Interactor, target: Interactor?, event: SoundEvent?, volume: Float =
            0.5f,
        pitch: Float =
            1f
    ) {
        playSound(
            world,
            target,
            event,
            if (caster.getType() == InteractorType.ENTITY) if (caster.getEntity() is PlayerEntity) SoundCategory.PLAYERS else if (caster.getEntity() is MobEntity) SoundCategory.HOSTILE else SoundCategory.NEUTRAL else SoundCategory.BLOCKS,
            volume,
            pitch
        )
    }

    @JvmOverloads
    fun playSound(
        world: World, target: Interactor, event: SoundEvent?, category: SoundCategory?, volume: Float = 0.5f,
        pitch: Float = 1f
    ) {
        world.playSound(null, target.getPos().x, target.getPos().y, target.getPos().z, event, category, volume, pitch)
    }

    private fun makeSound(name: String): SoundEvent {
        val loc: Identifier = Wizardry.getId(name)
        return SoundEvent(loc)
    }

    fun init() {
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, GLASS_BREAK.getId(), GLASS_BREAK)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FIZZING_LOOP.getId(), FIZZING_LOOP)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FRYING_SIZZLE.getId(), FRYING_SIZZLE)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, BUBBLING.getId(), BUBBLING)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, HARP1.getId(), HARP1)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, HARP2.getId(), HARP2)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, BELL.getId(), BELL)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, HALLOWED_SPIRIT.getId(), HALLOWED_SPIRIT)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, EXPLOSION_BOOM.getId(), EXPLOSION_BOOM)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, PROJECTILE_LAUNCH.getId(), PROJECTILE_LAUNCH)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, BASS_BOOM.getId(), BASS_BOOM)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, CHAINY_ZAP.getId(), CHAINY_ZAP)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, CHORUS_GOOD.getId(), CHORUS_GOOD)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, COLD_WIND.getId(), COLD_WIND)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ELECTRIC_BLAST.getId(), ELECTRIC_BLAST)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ETHEREAL_PASS_BY.getId(), ETHEREAL_PASS_BY)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FAIRY.getId(), FAIRY)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FIRE.getId(), FIRE)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FIREBALL.getId(), FIREBALL)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FLY.getId(), FLY)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FROST_FORM.getId(), FROST_FORM)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, HEAL.getId(), HEAL)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, LIGHTNING.getId(), LIGHTNING)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SLOW_MOTION_IN.getId(), SLOW_MOTION_IN)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SLOW_MOTION_OUT.getId(), SLOW_MOTION_OUT)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SMOKE_BLAST.getId(), SMOKE_BLAST)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, TELEPORT.getId(), TELEPORT)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, THUNDERBLAST.getId(), THUNDERBLAST)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, WIND.getId(), WIND)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ZAP.getId(), ZAP)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            ELECTRIC_WHITE_NOISE.getId(),
            ELECTRIC_WHITE_NOISE
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SPARKLE.getId(), SPARKLE)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, POP.getId(), POP)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, BELL_TING.getId(), BELL_TING)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, BUTTON_CLICK_IN.getId(), BUTTON_CLICK_IN)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, BUTTON_CLICK_OUT.getId(), BUTTON_CLICK_OUT)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ETHEREAL.getId(), ETHEREAL)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, WHOOSH.getId(), WHOOSH)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, WING_FLAP.getId(), WING_FLAP)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ZOOM.getId(), ZOOM)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            GOOD_ETHEREAL_CHILLS.getId(),
            GOOD_ETHEREAL_CHILLS
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SCRIBBLING.getId(), SCRIBBLING)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SPELL_FAIL.getId(), SPELL_FAIL)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, GAS_LEAK.getId(), GAS_LEAK)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, GRACE.getId(), GRACE)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SOUND_BOMB.getId(), SOUND_BOMB)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FIREWORK.getId(), FIREWORK)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, MARBLE_EXPLOSION.getId(), MARBLE_EXPLOSION)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SLIME_SQUISHING.getId(), SLIME_SQUISHING)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            DARK_SPELL_WHISPERS.getId(),
            DARK_SPELL_WHISPERS
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, DARK_SUCK_N_BLOW.getId(), DARK_SUCK_N_BLOW)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            ECHOY_HORROR_BREATHE.getId(),
            ECHOY_HORROR_BREATHE
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ELECTRIC_WHASHOOSH.getId(), ELECTRIC_WHASHOOSH)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            ENCHANTED_WHASHOOSH.getId(),
            ENCHANTED_WHASHOOSH
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, FROST_CRACKLE.getId(), FROST_CRACKLE)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            HELLFIRE_LIGHT_MATCH.getId(),
            HELLFIRE_LIGHT_MATCH
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, LARGE_BELL_BOINK.getId(), LARGE_BELL_BOINK)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            HIGH_PITCHED_SOLO_BLEEP.getId(),
            HIGH_PITCHED_SOLO_BLEEP
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, ICE_BREATHE.getId(), ICE_BREATHE)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            MAGIC_GLINT_LIGHT_BREATHE.getId(),
            MAGIC_GLINT_LIGHT_BREATHE
        )
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            NEGATIVELY_PITCHED_BREATHE_PUHH.getId(),
            NEGATIVELY_PITCHED_BREATHE_PUHH
        )
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            POSITIVELY_PITCHED_BREATHE_PUHH.getId(),
            POSITIVELY_PITCHED_BREATHE_PUHH
        )
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            STUTTERY_ELECTRIC_GRILL.getId(),
            STUTTERY_ELECTRIC_GRILL
        )
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            SUBTLE_MAGIC_BOOK_GLINT.getId(),
            SUBTLE_MAGIC_BOOK_GLINT
        )
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            SUDDEN_ANGELIC_SMOKE.getId(),
            SUDDEN_ANGELIC_SMOKE
        )
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, SUDDEN_DARK_PAFOOF.getId(), SUDDEN_DARK_PAFOOF)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, TIME_REVERSE.getId(), TIME_REVERSE)
        Registry.register<SoundEvent, SoundEvent>(Registry.SOUND_EVENT, TINY_BELL.getId(), TINY_BELL)
        Registry.register<SoundEvent, SoundEvent>(
            Registry.SOUND_EVENT,
            POSITIVE_LIGHT_TWINKLE.getId(),
            POSITIVE_LIGHT_TWINKLE
        )
    }
}