package com.teamwizardry.wizardry.api;


import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * By Pau101
 */
public final class StateGraph<T> {
	private State<T> state;

	private StateGraph(final Builder<T> builder) {
		this.state = builder.state;
	}

	public static <T> Predicate<? super T> runOnce(final Consumer<? super T> consumer) {
		return t -> {
			consumer.accept(t);
			return true;
		};
	}

	public void offer(final T data) {
		this.state = this.state.accept(data);
	}

	public interface State<T> {
		default State<T> init() {
			return this;
		}

		State<T> accept(final T data);

		State<T> andThen(final State<T> other);
	}

	public static final class Builder<T> {
		private State<T> state = new NoOpState<>();

		public Builder() {
		}

		public Builder<T> runOnce(final Consumer<? super T> function) {
			return this.run(StateGraph.runOnce(function));
		}

		public Builder<T> run(final Predicate<? super T> function) {
			return this.andThen(new RunState<>(new NoOpState<>(), function));
		}

		public Builder<T> runOnceIf(final Predicate<? super T> condition, final Consumer<? super T> function) {
			return this.runIf(condition, StateGraph.runOnce(function));
		}

		public Builder<T> runIf(final Predicate<? super T> condition, final Predicate<? super T> function) {
			return this.andThen(new BranchState<>(condition, new RunState<>(new NoOpState<>(), function), new NoOpState<>()));
		}

		public Builder<T> wait(final int duration) {
			if (duration > 0) {
				return this.andThen(new WaitState<>(new NoOpState<>(), duration));
			}
			return this;
		}

		public Builder<T> waitIf(final Predicate<? super T> condition, final int duration) {
			if (duration > 0) {
				return this.andThen(new BranchState<>(condition, new WaitState<>(new NoOpState<>(), duration), new NoOpState<>()));
			}
			return this;
		}

		public Builder<T> runWhile(final Predicate<? super T> condition, final UnaryOperator<Builder<T>> consumer) {
			final JumpState<T> guard = new JumpState<>(condition, new NoOpState<>(), new NoOpState<>());
			guard.first = consumer.apply(new Builder<>()).andThen(guard).state;
			return this.andThen(guard);
		}

		Builder<T> andThen(final State<T> other) {
			this.state = this.state.andThen(other);
			return this;
		}

		public StateGraph<T> build() {
			return new StateGraph<>(this);
		}
	}

	private static final class NoOpState<T> implements State<T> {
		@Override
		public State<T> accept(final T data) {
			return this;
		}

		@Override
		public State<T> andThen(final State<T> other) {
			return other;
		}
	}

	private static class BranchState<T> implements State<T> {
		final Predicate<? super T> condition;

		final State<T> first;

		final State<T> second;

		private BranchState(final Predicate<? super T> condition, final State<T> first, final State<T> second) {
			this.condition = condition;
			this.first = first;
			this.second = second;
		}

		@Override
		public State<T> accept(final T data) {
			return this.condition.test(data) ? this.first.init().accept(data) : this.second.init().accept(data);
		}

		@Override
		public State<T> andThen(final State<T> other) {
			return new BranchState<>(this.condition, this.first.andThen(other), this.second.andThen(other));
		}
	}

	private static final class JumpState<T> implements State<T> {
		final Predicate<? super T> condition;

		State<T> first;

		State<T> second;

		private JumpState(final Predicate<? super T> condition, final State<T> first, final State<T> second) {
			this.condition = condition;
			this.first = first;
			this.second = second;
		}

		@Override
		public State<T> accept(final T data) {
			return this.condition.test(data) ? this.first.init().accept(data) : this.second;
		}

		@Override
		public State<T> andThen(final State<T> other) {
			this.second = this.second.andThen(other);
			return this;
		}
	}

	private static abstract class ParentState<T> implements State<T> {
		final State<T> child;

		ParentState(final State<T> child) {
			this.child = child;
		}
	}

	private static final class RunState<T> extends ParentState<T> {
		final Predicate<? super T> function;

		RunState(final State<T> child, final Predicate<? super T> function) {
			super(child);
			this.function = function;
		}

		@Override
		public State<T> accept(final T data) {
			return this.function.test(data) ? this.child.init() : this;
		}

		@Override
		public State<T> andThen(final State<T> other) {
			return new RunState<>(this.child.andThen(other), this.function);
		}
	}

	private static final class WaitState<T> extends ParentState<T> {
		final int duration;
		int countdown;

		WaitState(final State<T> child, final int duration) {
			super(child);
			this.duration = duration;
			this.countdown = duration;
		}

		@Override
		public State<T> init() {
			this.countdown = this.duration;
			return super.init();
		}

		@Override
		public State<T> accept(final T data) {
			if (--this.countdown < 0) {
				return this.child.init().accept(data);
			} else if (this.countdown == 0) {
				return this.child.init();
			}
			return this;
		}

		@Override
		public State<T> andThen(final State<T> other) {
			return new WaitState<>(this.child.andThen(other), this.countdown);
		}
	}
}