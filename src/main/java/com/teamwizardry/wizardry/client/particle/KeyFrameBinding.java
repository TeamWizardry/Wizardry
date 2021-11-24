//package com.teamwizardry.wizardry.client.particle;
//
//import com.teamwizardry.librarianlib.glitter.ReadParticleBinding;
//import com.teamwizardry.librarianlib.glitter.bindings.AbstractTimeBinding;
//import com.teamwizardry.librarianlib.math.Easing;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//class KeyFrameBinding extends AbstractTimeBinding {
//
//    private final int bindingSize;
//    private final ReadParticleBinding frameCount;
//    private final ReadParticleBinding easings;
//    private final ReadParticleBinding frameValues;
//
//    private final double[] contents;
//
//    public KeyFrameBinding(
//            @NotNull ReadParticleBinding lifetime,
//            @NotNull ReadParticleBinding age,
//            @Nullable ReadParticleBinding timescale,
//            @Nullable ReadParticleBinding offset, int bindingSize,
//            ReadParticleBinding frameCount, ReadParticleBinding easings,
//            ReadParticleBinding frameValues) {
//        super(lifetime, age, timescale, offset, Easing.linear);
//        this.bindingSize = bindingSize;
//        this.frameCount = frameCount;
//        this.easings = easings;
//        this.frameValues = frameValues;
//        contents = new double[bindingSize];
//    }
//
//    @NotNull
//    @Override
//    public double[] getContents() {
//        return contents;
//    }
//
//    @Override
//    public void load(@NotNull double[] particle) {
//        super.load(particle);
//        frameValues.load(particle);
//        frameCount.load(particle);
//        easings.load(particle);
//
//        double time = getTime() * Math.max(0, frameCount.getContents()[0] - 1);
//        int index = (int) time;
//        for (int i = 0; i < bindingSize; i++) {
//            double first = frameValues.getContents()[index * bindingSize + i];
//            double second = frameValues.getContents()[(index + 1) * bindingSize + i];
//            float t = KeyFramedGlitter.easingArray.get((int) easings.getContents()[index]).ease((float) (time - index));
//            double result = t * second + (1 - t) * first;
//            contents[i] = result;
//        }
//    }
//
//}
//
