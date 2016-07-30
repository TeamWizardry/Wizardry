package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.math.Vec2;
import com.teamwizardry.librarianlib.math.shapes.BezierCurve2D;

/**
 * Created by Saad on 15/7/2016.
 */
public class WorktableLink extends BezierCurve2D {

    /**
     * The modules linked
     */
    private WorktableModule startPointModule, endPointModule;

    public WorktableLink(WorktableModule startPointModule, WorktableModule endPointModule) {
        super(new Vec2(startPointModule.getX() + WorktableGui.iconSize / 2, startPointModule.getY() + WorktableGui.iconSize / 2), new Vec2(endPointModule.getX() + WorktableGui.iconSize / 2, endPointModule.getY() + WorktableGui.iconSize / 2));
        this.startPointModule = startPointModule;
        this.endPointModule = endPointModule;
    }

    public WorktableModule getStartPointModule() {
        return startPointModule;
    }

    public void setStartPointModule(WorktableModule startPoint) {
        this.startPointModule = startPoint;
        setStartPoint(new Vec2(startPoint.getX() + WorktableGui.iconSize / 2, startPoint.getY() + WorktableGui.iconSize / 2));
    }

    public WorktableModule getEndPointModule() {
        return endPointModule;
    }

    public void setEndPointModule(WorktableModule endPoint) {
        this.endPointModule = endPoint;
        setEndPoint(new Vec2(endPoint.getX() + WorktableGui.iconSize / 2, endPoint.getY() + WorktableGui.iconSize / 2));
    }
}
