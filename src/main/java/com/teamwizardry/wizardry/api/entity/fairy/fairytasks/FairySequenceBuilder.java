package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class FairySequenceBuilder {

	@Nonnull
	private FairySequence.State initialState = new FairySequence.IdleState();

	public FairySequenceBuilder() {
	}

	public FairySequenceBuilder wait(int timeRemaining) {
		getLastState().setChild(new FairySequence.WaitState(timeRemaining));
		return this;
	}

	public FairySequenceBuilder waitIf(Predicate<EntityFairy> condition, int timeRemaining) {
		getLastState().setChild(new FairySequence.WaitIfState(condition, timeRemaining));
		return this;
	}

	public FairySequenceBuilder run(Predicate<EntityFairy> run) {
		getLastState().setChild(new FairySequence.TickState(run));
		return this;
	}

	public FairySequence build() {
		getLastState().setChild(new FairySequence.LoopState(initialState));
		return new FairySequence(initialState);
	}

	private FairySequence.State getLastState() {
		FairySequence.State state = initialState;
		//	while (!(state.getChild() instanceof FairySequence.IdleState)) {
		//		state = state.getChild();
		//	}
		return state;
	}

}
