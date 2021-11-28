package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.common.spell.component.Interactor
import com.teamwizardry.wizardry.getID
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object ModSounds {
    var GLASS_BREAK: SoundEvent = makeSound("glassbreak")
    var FIZZING_LOOP: SoundEvent = makeSound("fizzingloop")
    var FRYING_SIZZLE: SoundEvent = makeSound("firesizzleloop")
    var BUBBLING: SoundEvent = makeSound("bubbling")
    var HARP1: SoundEvent = makeSound("harp1")
    var HARP2: SoundEvent = makeSound("harp2")
    var BELL: SoundEvent = makeSound("bell")
    var HALLOWED_SPIRIT: SoundEvent = makeSound("hallowed_spirit_shriek")
    var EXPLOSION_BOOM: SoundEvent = makeSound("expl_boom")
    var PROJECTILE_LAUNCH: SoundEvent = makeSound("proj_launch")
    var BASS_BOOM: SoundEvent = makeSound("bass_boom")
    var CHAINY_ZAP: SoundEvent = makeSound("chainy_zap")
    var CHORUS_GOOD: SoundEvent = makeSound("chorus_good")
    var COLD_WIND: SoundEvent = makeSound("cold_wind")
    var ELECTRIC_BLAST: SoundEvent = makeSound("electric_blast")
    var ETHEREAL_PASS_BY: SoundEvent = makeSound("ethereal_pass_by")
    var FAIRY: SoundEvent = makeSound("fairy_1")
    var FIRE: SoundEvent = makeSound("fire")
    var FIREBALL: SoundEvent = makeSound("fireball")
    var FLY: SoundEvent = makeSound("fly")
    var FROST_FORM: SoundEvent = makeSound("frost_form")
    var HEAL: SoundEvent = makeSound("heal")
    var LIGHTNING: SoundEvent = makeSound("lightning")
    var SLOW_MOTION_IN: SoundEvent = makeSound("slow_motion_in")
    var SLOW_MOTION_OUT: SoundEvent = makeSound("slow_motion_out")
    var SMOKE_BLAST: SoundEvent = makeSound("smoke_blast")
    var TELEPORT: SoundEvent = makeSound("teleport")
    var THUNDERBLAST: SoundEvent = makeSound("thunder_blast")
    var WIND: SoundEvent = makeSound("wind")
    var ZAP: SoundEvent = makeSound("zap")
    var ELECTRIC_WHITE_NOISE: SoundEvent = makeSound("electric_white_noise")
    var SPARKLE: SoundEvent = makeSound("sparkle")
    var POP: SoundEvent = makeSound("pop")
    var BELL_TING: SoundEvent = makeSound("bell_ting")
    var BUTTON_CLICK_IN: SoundEvent = makeSound("button_click_in")
    var BUTTON_CLICK_OUT: SoundEvent = makeSound("button_click_out")
    var ETHEREAL: SoundEvent = makeSound("ethereal")
    var WHOOSH: SoundEvent = makeSound("whoosh")
    var WING_FLAP: SoundEvent = makeSound("wing_flap")
    var ZOOM: SoundEvent = makeSound("zoom")
    var GOOD_ETHEREAL_CHILLS: SoundEvent = makeSound("good_ethereal_chills")
    var SCRIBBLING: SoundEvent = makeSound("scribbling")
    var SPELL_FAIL: SoundEvent = makeSound("spell_fail")
    var GAS_LEAK: SoundEvent = makeSound("gas_leak")
    var GRACE: SoundEvent = makeSound("grace")
    var SOUND_BOMB: SoundEvent = makeSound("sound_bomb")
    var FIREWORK: SoundEvent = makeSound("firework")
    var MARBLE_EXPLOSION: SoundEvent = makeSound("marble_explosion")
    var SLIME_SQUISHING: SoundEvent = makeSound("slime_squishing")
    var DARK_SPELL_WHISPERS: SoundEvent = makeSound("dark_spell_whispers")
    var DARK_SUCK_N_BLOW: SoundEvent = makeSound("dark_suck_n_blow")
    var ECHOY_HORROR_BREATHE: SoundEvent = makeSound("echoy_horror_breathe")
    var ELECTRIC_WHASHOOSH: SoundEvent = makeSound("electric_whashoosh")
    var ENCHANTED_WHASHOOSH: SoundEvent = makeSound("enchanted_whashoosh")
    var FROST_CRACKLE: SoundEvent = makeSound("frost_crackle")
    var HELLFIRE_LIGHT_MATCH: SoundEvent = makeSound("hellfire_light_match")
    var LARGE_BELL_BOINK: SoundEvent = makeSound("large_bell_boink")
    var HIGH_PITCHED_SOLO_BLEEP: SoundEvent = makeSound("high_pitched_solo_bleep")
    var ICE_BREATHE: SoundEvent = makeSound("ice_breathe")
    var MAGIC_GLINT_LIGHT_BREATHE: SoundEvent = makeSound("magic_glint_light_breathe")
    var NEGATIVELY_PITCHED_BREATHE_PUHH: SoundEvent = makeSound("negatively_pitched_breathe_puhh")
    var POSITIVELY_PITCHED_BREATHE_PUHH: SoundEvent = makeSound("positively_pitched_breathe_puhh")
    var STUTTERY_ELECTRIC_GRILL: SoundEvent = makeSound("stuttery_electric_grill")
    var SUBTLE_MAGIC_BOOK_GLINT: SoundEvent = makeSound("subtle_magic_book_glint")
    var SUDDEN_ANGELIC_SMOKE: SoundEvent = makeSound("sudden_angelic_smoke")
    var SUDDEN_DARK_PAFOOF: SoundEvent = makeSound("sudden_dark_pafoof")
    var TIME_REVERSE: SoundEvent = makeSound("time_reverse")
    var TINY_BELL: SoundEvent = makeSound("tiny_bell")
    var POSITIVE_LIGHT_TWINKLE: SoundEvent = makeSound("positive_light_twinkle")

    @JvmOverloads
    fun playSound(world: World, caster: Interactor, target: Interactor, event: SoundEvent, volume: Float = 0.5f, pitch: Float = 1f) {
        playSound(world, target, event,
                if (caster.type == Interactor.InteractorType.ENTITY)
                    when (caster.entity) {
                        is PlayerEntity -> SoundCategory.PLAYERS
                        is MobEntity -> SoundCategory.HOSTILE
                        else -> SoundCategory.NEUTRAL
                    }
                else SoundCategory.BLOCKS,
                volume, pitch
        )
    }

    @JvmOverloads
    fun playSound(world: World, target: Interactor, event: SoundEvent, category: SoundCategory, volume: Float = 0.5f, pitch: Float = 1f) {
        world.playSound(null, target.pos.x, target.pos.y, target.pos.z, event, category, volume, pitch)
    }

    private fun makeSound(name: String): SoundEvent {
        val loc: Identifier = getID(name)
        return SoundEvent(loc)
    }

    fun init() {
        Registry.register(Registry.SOUND_EVENT, GLASS_BREAK.id, GLASS_BREAK)
        Registry.register(Registry.SOUND_EVENT, FIZZING_LOOP.id, FIZZING_LOOP)
        Registry.register(Registry.SOUND_EVENT, FRYING_SIZZLE.id, FRYING_SIZZLE)
        Registry.register(Registry.SOUND_EVENT, BUBBLING.id, BUBBLING)
        Registry.register(Registry.SOUND_EVENT, HARP1.id, HARP1)
        Registry.register(Registry.SOUND_EVENT, HARP2.id, HARP2)
        Registry.register(Registry.SOUND_EVENT, BELL.id, BELL)
        Registry.register(Registry.SOUND_EVENT, HALLOWED_SPIRIT.id, HALLOWED_SPIRIT)
        Registry.register(Registry.SOUND_EVENT, EXPLOSION_BOOM.id, EXPLOSION_BOOM)
        Registry.register(Registry.SOUND_EVENT, PROJECTILE_LAUNCH.id, PROJECTILE_LAUNCH)
        Registry.register(Registry.SOUND_EVENT, BASS_BOOM.id, BASS_BOOM)
        Registry.register(Registry.SOUND_EVENT, CHAINY_ZAP.id, CHAINY_ZAP)
        Registry.register(Registry.SOUND_EVENT, CHORUS_GOOD.id, CHORUS_GOOD)
        Registry.register(Registry.SOUND_EVENT, COLD_WIND.id, COLD_WIND)
        Registry.register(Registry.SOUND_EVENT, ELECTRIC_BLAST.id, ELECTRIC_BLAST)
        Registry.register(Registry.SOUND_EVENT, ETHEREAL_PASS_BY.id, ETHEREAL_PASS_BY)
        Registry.register(Registry.SOUND_EVENT, FAIRY.id, FAIRY)
        Registry.register(Registry.SOUND_EVENT, FIRE.id, FIRE)
        Registry.register(Registry.SOUND_EVENT, FIREBALL.id, FIREBALL)
        Registry.register(Registry.SOUND_EVENT, FLY.id, FLY)
        Registry.register(Registry.SOUND_EVENT, FROST_FORM.id, FROST_FORM)
        Registry.register(Registry.SOUND_EVENT, HEAL.id, HEAL)
        Registry.register(Registry.SOUND_EVENT, LIGHTNING.id, LIGHTNING)
        Registry.register(Registry.SOUND_EVENT, SLOW_MOTION_IN.id, SLOW_MOTION_IN)
        Registry.register(Registry.SOUND_EVENT, SLOW_MOTION_OUT.id, SLOW_MOTION_OUT)
        Registry.register(Registry.SOUND_EVENT, SMOKE_BLAST.id, SMOKE_BLAST)
        Registry.register(Registry.SOUND_EVENT, TELEPORT.id, TELEPORT)
        Registry.register(Registry.SOUND_EVENT, THUNDERBLAST.id, THUNDERBLAST)
        Registry.register(Registry.SOUND_EVENT, WIND.id, WIND)
        Registry.register(Registry.SOUND_EVENT, ZAP.id, ZAP)
        Registry.register(
            Registry.SOUND_EVENT,
            ELECTRIC_WHITE_NOISE.id,
            ELECTRIC_WHITE_NOISE
        )
        Registry.register(Registry.SOUND_EVENT, SPARKLE.id, SPARKLE)
        Registry.register(Registry.SOUND_EVENT, POP.id, POP)
        Registry.register(Registry.SOUND_EVENT, BELL_TING.id, BELL_TING)
        Registry.register(Registry.SOUND_EVENT, BUTTON_CLICK_IN.id, BUTTON_CLICK_IN)
        Registry.register(Registry.SOUND_EVENT, BUTTON_CLICK_OUT.id, BUTTON_CLICK_OUT)
        Registry.register(Registry.SOUND_EVENT, ETHEREAL.id, ETHEREAL)
        Registry.register(Registry.SOUND_EVENT, WHOOSH.id, WHOOSH)
        Registry.register(Registry.SOUND_EVENT, WING_FLAP.id, WING_FLAP)
        Registry.register(Registry.SOUND_EVENT, ZOOM.id, ZOOM)
        Registry.register(
            Registry.SOUND_EVENT,
            GOOD_ETHEREAL_CHILLS.id,
            GOOD_ETHEREAL_CHILLS
        )
        Registry.register(Registry.SOUND_EVENT, SCRIBBLING.id, SCRIBBLING)
        Registry.register(Registry.SOUND_EVENT, SPELL_FAIL.id, SPELL_FAIL)
        Registry.register(Registry.SOUND_EVENT, GAS_LEAK.id, GAS_LEAK)
        Registry.register(Registry.SOUND_EVENT, GRACE.id, GRACE)
        Registry.register(Registry.SOUND_EVENT, SOUND_BOMB.id, SOUND_BOMB)
        Registry.register(Registry.SOUND_EVENT, FIREWORK.id, FIREWORK)
        Registry.register(Registry.SOUND_EVENT, MARBLE_EXPLOSION.id, MARBLE_EXPLOSION)
        Registry.register(Registry.SOUND_EVENT, SLIME_SQUISHING.id, SLIME_SQUISHING)
        Registry.register(
            Registry.SOUND_EVENT,
            DARK_SPELL_WHISPERS.id,
            DARK_SPELL_WHISPERS
        )
        Registry.register(Registry.SOUND_EVENT, DARK_SUCK_N_BLOW.id, DARK_SUCK_N_BLOW)
        Registry.register(
            Registry.SOUND_EVENT,
            ECHOY_HORROR_BREATHE.id,
            ECHOY_HORROR_BREATHE
        )
        Registry.register(Registry.SOUND_EVENT, ELECTRIC_WHASHOOSH.id, ELECTRIC_WHASHOOSH)
        Registry.register(
            Registry.SOUND_EVENT,
            ENCHANTED_WHASHOOSH.id,
            ENCHANTED_WHASHOOSH
        )
        Registry.register(Registry.SOUND_EVENT, FROST_CRACKLE.id, FROST_CRACKLE)
        Registry.register(
            Registry.SOUND_EVENT,
            HELLFIRE_LIGHT_MATCH.id,
            HELLFIRE_LIGHT_MATCH
        )
        Registry.register(Registry.SOUND_EVENT, LARGE_BELL_BOINK.id, LARGE_BELL_BOINK)
        Registry.register(
            Registry.SOUND_EVENT,
            HIGH_PITCHED_SOLO_BLEEP.id,
            HIGH_PITCHED_SOLO_BLEEP
        )
        Registry.register(Registry.SOUND_EVENT, ICE_BREATHE.id, ICE_BREATHE)
        Registry.register(
            Registry.SOUND_EVENT,
            MAGIC_GLINT_LIGHT_BREATHE.id,
            MAGIC_GLINT_LIGHT_BREATHE
        )
        Registry.register(
            Registry.SOUND_EVENT,
            NEGATIVELY_PITCHED_BREATHE_PUHH.id,
            NEGATIVELY_PITCHED_BREATHE_PUHH
        )
        Registry.register(
            Registry.SOUND_EVENT,
            POSITIVELY_PITCHED_BREATHE_PUHH.id,
            POSITIVELY_PITCHED_BREATHE_PUHH
        )
        Registry.register(
            Registry.SOUND_EVENT,
            STUTTERY_ELECTRIC_GRILL.id,
            STUTTERY_ELECTRIC_GRILL
        )
        Registry.register(
            Registry.SOUND_EVENT,
            SUBTLE_MAGIC_BOOK_GLINT.id,
            SUBTLE_MAGIC_BOOK_GLINT
        )
        Registry.register(
            Registry.SOUND_EVENT,
            SUDDEN_ANGELIC_SMOKE.id,
            SUDDEN_ANGELIC_SMOKE
        )
        Registry.register(Registry.SOUND_EVENT, SUDDEN_DARK_PAFOOF.id, SUDDEN_DARK_PAFOOF)
        Registry.register(Registry.SOUND_EVENT, TIME_REVERSE.id, TIME_REVERSE)
        Registry.register(Registry.SOUND_EVENT, TINY_BELL.id, TINY_BELL)
        Registry.register(
            Registry.SOUND_EVENT,
            POSITIVE_LIGHT_TWINKLE.id,
            POSITIVE_LIGHT_TWINKLE
        )
    }
}