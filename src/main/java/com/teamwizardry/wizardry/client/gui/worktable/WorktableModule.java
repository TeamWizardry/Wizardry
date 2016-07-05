package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.wizardry.api.module.Module;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableModule {

    private int x, y;
    private Module module;
    private boolean isMaster = false;

    public WorktableModule(Module module) {
        this.module = module;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Module getModule() {
        return module;
    }

    public WorktableModule copy() {
        WorktableModule copy = new WorktableModule(module);
        copy.setX(x);
        copy.setY(y);
        return copy;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }
}
