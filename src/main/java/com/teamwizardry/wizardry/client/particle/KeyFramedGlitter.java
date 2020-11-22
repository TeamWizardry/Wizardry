package com.teamwizardry.wizardry.client.particle;

import com.teamwizardry.librarianlib.core.rendering.BlendMode;
import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.SetValueUpdateModule;
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;


public class KeyFramedGlitter extends ParticleSystem {

    public static final List<Easing> easingArray = new ArrayList<>();

    static {
        easingArray.add(Easing.linear);
        easingArray.add(Easing.easeInSine);
        easingArray.add(Easing.easeOutSine);
        easingArray.add(Easing.easeInOutSine);
        easingArray.add(Easing.easeInQuad);
        easingArray.add(Easing.easeOutQuad);
        easingArray.add(Easing.easeInOutQuad);
        easingArray.add(Easing.easeInCubic);
        easingArray.add(Easing.easeOutCubic);
        easingArray.add(Easing.easeInOutCubic);
        easingArray.add(Easing.easeInQuart);
        easingArray.add(Easing.easeOutQuart);
        easingArray.add(Easing.easeInOutQuart);
        easingArray.add(Easing.easeInQuint);
        easingArray.add(Easing.easeOutQuint);
        easingArray.add(Easing.easeInOutQuint);
        easingArray.add(Easing.easeInExpo);
        easingArray.add(Easing.easeOutExpo);
        easingArray.add(Easing.easeInOutExpo);
        easingArray.add(Easing.easeInCirc);
        easingArray.add(Easing.easeOutCirc);
        easingArray.add(Easing.easeInOutCirc);
        easingArray.add(Easing.easeInBack);
        easingArray.add(Easing.easeOutBack);
        easingArray.add(Easing.easeInOutBack);
        easingArray.add(Easing.easeInElastic);
        easingArray.add(Easing.easeOutElastic);
        easingArray.add(Easing.easeInOutElastic);
        easingArray.add(Easing.easeInBounce);
        easingArray.add(Easing.easeOutBounce);
        easingArray.add(Easing.easeInOutBounce);
    }

    @Override
    public void configure() {
        StoredBinding pos = bind(3);
        StoredBinding previousPos = bind(3);
        StoredBinding posFrameCount = bind(1);
        StoredBinding posFrames = bind(3 * 5);
        StoredBinding posEasings = bind(5);

        StoredBinding colorFrameCount = bind(1);
        StoredBinding colorFrames = bind(4 * 5);
        StoredBinding colorEasings = bind(5);

        StoredBinding sizeFrameCount = bind(1);
        StoredBinding sizeFrames = bind(5);
        StoredBinding sizeEasings = bind(5);

        StoredBinding alphaFrameCount = bind(1);
        StoredBinding alphaFrames = bind(5);
        StoredBinding alphaEasings = bind(5);

        getUpdateModules().add(new SetValueUpdateModule(previousPos, pos));

        getUpdateModules().add(new SetValueUpdateModule(pos,
                new KeyFrameBinding(getLifetime(), getAge(), null, null, 3, posFrameCount, posEasings, posFrames)));


        getRenderModules().add(new SpriteRenderModule(SpriteRenderModule.simpleRenderType(
                new ResourceLocation(Wizardry.MODID, "textures/particles/sparkle_blurred.png"),
                BlendMode.getADDITIVE(),
                false,
                true),
                pos,
                previousPos,
                new KeyFrameBinding(getLifetime(), getAge(), null, null, 4, colorFrameCount, colorEasings, colorFrames),
                new KeyFrameBinding(getLifetime(), getAge(), null, null, 1, sizeFrameCount, sizeEasings, sizeFrames),
                null,
                new KeyFrameBinding(getLifetime(), getAge(), null, null, 1, alphaFrameCount, alphaEasings, alphaFrames)
        ));
    }

    public void spawn(KeyFramedGlitterBox box) {
        List<Double> params = new ArrayList<>();

        params.add(box.pos[0]);
        params.add(box.pos[1]);
        params.add(box.pos[2]);
        params.add(box.pos[0]);
        params.add(box.pos[1]);
        params.add(box.pos[2]);
        params.add((double) box.posFrameCount);
        for (double v : box.pos) params.add(v);
        for (double v : box.posEasings) params.add(v);

        params.add((double) box.colorFrameCount);
        for (double v : box.color) params.add(v);
        for (double v : box.colorEasings) params.add(v);

        params.add((double) box.sizeFrameCount);
        for (double v : box.size) params.add(v);
        for (double v : box.sizeEasings) params.add(v);

        params.add((double) box.alphaFrameCount);
        for (double v : box.alpha) params.add(v);
        for (double v : box.alphaEasings) params.add(v);

        // double[] result = new double[box.pos.length + box.posEasings.length + 1 + box.color.length +
        //         box.colorEasings.length + 1 + box.size.length + box.sizeEasings.length + 1 + box.alpha.length +
        //         box.alphaEasings.length + 1];
        // result[]
        // System.arraycopy(box.pos, 0, result, 0, box.pos.length);
        // System.arraycopy(box.posEasings, 0, result, 0, box.pos.length);
        // System.arraycopy(b, 0, result, a.length, b.length);

        addParticle(box.lifetime, params.stream().mapToDouble(Double::doubleValue).toArray());
    }
}

