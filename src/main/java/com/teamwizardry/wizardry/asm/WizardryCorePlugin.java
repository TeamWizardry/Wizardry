package com.teamwizardry.wizardry.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Demoniaque.
 */
@IFMLLoadingPlugin.Name("Wizardry Plugin")
@IFMLLoadingPlugin.TransformerExclusions("com.teamwizardry.wizardry.asm")
@IFMLLoadingPlugin.SortingIndex(1001) // After runtime deobf
public class WizardryCorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"com.teamwizardry.wizardry.asm.WizardryTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
