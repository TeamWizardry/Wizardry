package com.teamwizardry.wizardry.common.init;


import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Interactor;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                                caster.getEntity() instanceof MonsterEntity ?
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
        ResourceLocation loc = new ResourceLocation(Wizardry.MODID, name);
        return new SoundEvent(loc).setRegistryName(loc);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(GLASS_BREAK);
        event.getRegistry().register(FIZZING_LOOP);
        event.getRegistry().register(FRYING_SIZZLE);
        event.getRegistry().register(BUBBLING);
        event.getRegistry().register(HARP1);
        event.getRegistry().register(HARP2);
        event.getRegistry().register(BELL);
        event.getRegistry().register(HALLOWED_SPIRIT);
        event.getRegistry().register(EXPLOSION_BOOM);
        event.getRegistry().register(PROJECTILE_LAUNCH);
        event.getRegistry().register(BASS_BOOM);
        event.getRegistry().register(CHAINY_ZAP);
        event.getRegistry().register(CHORUS_GOOD);
        event.getRegistry().register(COLD_WIND);
        event.getRegistry().register(ELECTRIC_BLAST);
        event.getRegistry().register(ETHEREAL_PASS_BY);
        event.getRegistry().register(FAIRY);
        event.getRegistry().register(FIRE);
        event.getRegistry().register(FIREBALL);
        event.getRegistry().register(FLY);
        event.getRegistry().register(FROST_FORM);
        event.getRegistry().register(HEAL);
        event.getRegistry().register(LIGHTNING);
        event.getRegistry().register(SLOW_MOTION_IN);
        event.getRegistry().register(SLOW_MOTION_OUT);
        event.getRegistry().register(SMOKE_BLAST);
        event.getRegistry().register(TELEPORT);
        event.getRegistry().register(THUNDERBLAST);
        event.getRegistry().register(WIND);
        event.getRegistry().register(ZAP);
        event.getRegistry().register(ELECTRIC_WHITE_NOISE);
        event.getRegistry().register(SPARKLE);
        event.getRegistry().register(POP);
        event.getRegistry().register(BELL_TING);
        event.getRegistry().register(BUTTON_CLICK_IN);
        event.getRegistry().register(BUTTON_CLICK_OUT);
        event.getRegistry().register(ETHEREAL);
        event.getRegistry().register(WHOOSH);
        event.getRegistry().register(WING_FLAP);
        event.getRegistry().register(ZOOM);
        event.getRegistry().register(GOOD_ETHEREAL_CHILLS);
        event.getRegistry().register(SCRIBBLING);
        event.getRegistry().register(SPELL_FAIL);
        event.getRegistry().register(GAS_LEAK);
        event.getRegistry().register(GRACE);
        event.getRegistry().register(SOUND_BOMB);
        event.getRegistry().register(FIREWORK);
        event.getRegistry().register(MARBLE_EXPLOSION);
        event.getRegistry().register(SLIME_SQUISHING);
        event.getRegistry().register(DARK_SPELL_WHISPERS);
        event.getRegistry().register(DARK_SUCK_N_BLOW);
        event.getRegistry().register(ECHOY_HORROR_BREATHE);
        event.getRegistry().register(ELECTRIC_WHASHOOSH);
        event.getRegistry().register(ENCHANTED_WHASHOOSH);
        event.getRegistry().register(FROST_CRACKLE);
        event.getRegistry().register(HELLFIRE_LIGHT_MATCH);
        event.getRegistry().register(LARGE_BELL_BOINK);
        event.getRegistry().register(HIGH_PITCHED_SOLO_BLEEP);
        event.getRegistry().register(ICE_BREATHE);
        event.getRegistry().register(MAGIC_GLINT_LIGHT_BREATHE);
        event.getRegistry().register(NEGATIVELY_PITCHED_BREATHE_PUHH);
        event.getRegistry().register(POSITIVELY_PITCHED_BREATHE_PUHH);
        event.getRegistry().register(STUTTERY_ELECTRIC_GRILL);
        event.getRegistry().register(SUBTLE_MAGIC_BOOK_GLINT);
        event.getRegistry().register(SUDDEN_ANGELIC_SMOKE);
        event.getRegistry().register(SUDDEN_DARK_PAFOOF);
        event.getRegistry().register(TIME_REVERSE);
        event.getRegistry().register(TINY_BELL);
        event.getRegistry().register(POSITIVE_LIGHT_TWINKLE);
    }
}