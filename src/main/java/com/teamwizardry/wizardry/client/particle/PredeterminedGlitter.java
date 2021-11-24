//package com.teamwizardry.wizardry.client.particle;
//
//import com.teamwizardry.librarianlib.core.rendering.BlendMode;
//import com.teamwizardry.librarianlib.glitter.ParticleSystem;
//import com.teamwizardry.librarianlib.glitter.bindings.EaseBinding;
//import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
//import com.teamwizardry.librarianlib.glitter.modules.SetValueUpdateModule;
//import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
//import com.teamwizardry.librarianlib.math.Easing;
//import com.teamwizardry.wizardry.Wizardry;
//import net.minecraft.util.Identifier;
//
//public class PredeterminedGlitter extends ParticleSystem {
//
//    @Override
//    public void configure() {
//        StoredBinding origin = bind(3);
//        StoredBinding previousPos = bind(3);
//        StoredBinding target = bind(3);
//        StoredBinding initialColor = bind(4);
//        StoredBinding goalColor = bind(4);
//        StoredBinding initialSize = bind(1);
//        StoredBinding goalSize = bind(1);
//        StoredBinding initialAlpha = bind(1);
//        StoredBinding middleAlpha = bind(1);
//        StoredBinding goalAlpha = bind(1);
//
//        getUpdateModules().add(new SetValueUpdateModule(previousPos, origin));
//
//        getUpdateModules().add(new SetValueUpdateModule(origin,
//                new EaseBinding(getLifetime(),
//                        getAge(),
//                        null,
//                        null,
//                        Easing.easeInOutSine,
//                        3,
//                        origin, target
//                )));
//
//
//        getRenderModules().add(new SpriteRenderModule(SpriteRenderModule.simpleRenderType(
//                new Identifier(Wizardry.MODID, "textures/particles/sparkle_blurred.png"),
//                BlendMode.getADDITIVE(),
//                false,
//                true),
//                origin,
//                previousPos,
//                new EaseBinding(getLifetime(), getAge(), null, null, Easing.linear, 4, initialColor, goalColor),
//                new EaseBinding(getLifetime(), getAge(), null, null, Easing.linear, 1, initialSize, goalSize),
//                null,
//                new EaseBinding(getLifetime(), getAge(), null, null, Easing.linear, 1, initialAlpha, goalAlpha)
//        ));
//    }
//
//    public void spawn(GlitterBox box) {
//        addParticle(box.lifetime,
//                box.originX,
//                box.originY,
//                box.originZ,
//                box.originX,
//                box.originY,
//                box.originZ,
//                box.targetX,
//                box.targetY,
//                box.targetZ,
//                box.initialColor.getRed() / 255.0,
//                box.initialColor.getGreen() / 255.0,
//                box.initialColor.getBlue() / 255.0,
//                1,
//                box.goalColor.getRed() / 255.0,
//                box.goalColor.getGreen() / 255.0,
//                box.goalColor.getBlue() / 255.0,
//                1,
//                box.initialSize,
//                box.goalSize,
//                box.initialAlpha,
//                box.middleAlpha,
//                box.goalAlpha
//        );
//    }
//}