package com.teamwizardry.wizardry.client.gui.worktable2;

interface IRevealable {
	void reveal(Runnable onComplete);

	void hide(Runnable onComplete);
}
