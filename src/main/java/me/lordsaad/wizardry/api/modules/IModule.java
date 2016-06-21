package me.lordsaad.wizardry.api.modules;

/**
 * Created by Saad on 6/21/2016.
 */
public interface IModule {

    void tick();

    void process();

    ModuleType getType();
}
