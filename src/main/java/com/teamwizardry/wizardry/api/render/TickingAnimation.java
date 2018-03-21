package com.teamwizardry.wizardry.api.render;

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty;
import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class TickingAnimation extends Animation<ScheduledEventAnimation.PointlessAnimatableObject> {

	private final Consumer<Float> consumer;

	public TickingAnimation(float time, @Nonnull Consumer<Float> consumer) {
		super(ScheduledEventAnimation.PointlessAnimatableObject.INSTANCE, AnimatableProperty.Companion.get(com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation.PointlessAnimatableObject.class, "field"));
		this.consumer = consumer;
		this.setDuration(time);
	}

	public void update(float time) {
		consumer.accept(time);
	}
}
