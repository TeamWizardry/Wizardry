package com.teamwizardry.wizardry.client.gui.book;

/**
 * Created by Saad on 6/12/2016.
 */
public class Slot {

    private int slot = 0;

    public Slot(int slot) {
        this.slot = slot;
    }

    public int getX() {
        switch (slot) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 0;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 0;
            case 7:
                return 1;
            case 8:
                return 2;
        }
        return 0;
    }

    public int getY() {
        switch (slot) {
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 1;
            case 5:
                return 1;
            case 6:
                return 2;
            case 7:
                return 2;
            case 8:
                return 2;
        }
        return 0;
    }
}
