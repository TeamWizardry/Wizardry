package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class FairySequence {

	@Nonnull
	private State activeState;

	FairySequence(@Nonnull State activeState) {
		this.activeState = activeState;
	}

	public void tick(EntityFairy fairy) {
		activeState = activeState.tick(fairy);
	}

	public static class LoopState extends State {

		private final State loopHead;

		LoopState(final State loopHead) {
			this.loopHead = loopHead;
		}

		@NotNull
		@Override
		public State tick(EntityFairy fairy) {
			return loopHead.tick(fairy);
		}
	}

	public static class TickState extends State {

		private final Predicate<EntityFairy> run;

		TickState(Predicate<EntityFairy> run) {
			this.run = run;
		}

		@NotNull
		@Override
		public State tick(EntityFairy fairy) {
			return run.test(fairy) ? getChild().tick(fairy) : this;
		}
	}

	public static class WaitState extends State {

		private int timeRemaining;

		WaitState(final int timeRemaining) {
			this.timeRemaining = timeRemaining;
		}

		@NotNull
		@Override
		public State tick(EntityFairy fairy) {
			return --timeRemaining < 0 ? getChild().tick(fairy) : this;
		}
	}

	public static class WaitIfState extends State {

		private Predicate<EntityFairy> condition;
		private int timeRemaining;

		WaitIfState(Predicate<EntityFairy> condition, final int condtionalTimeRemaining) {
			this.condition = condition;
			this.timeRemaining = condtionalTimeRemaining;
		}

		@NotNull
		@Override
		public State tick(EntityFairy fairy) {
			return condition.test(fairy) ? (--timeRemaining < 0 ? getChild().tick(fairy) : this) : getChild().tick(fairy);
		}

	}

	public static class IdleState extends State {

		@NotNull
		@Override
		public State tick(EntityFairy fairy) {
			return getChild().tick(fairy);
		}
	}

	public static abstract class State {
		private State child;

		State() {
		}

		@NotNull
		public abstract State tick(EntityFairy fairy);

		public @NotNull State getChild() {
			return child == null ? this : child;
		}

		public void setChild(State child) {
			this.child = child;
		}
	}
}
