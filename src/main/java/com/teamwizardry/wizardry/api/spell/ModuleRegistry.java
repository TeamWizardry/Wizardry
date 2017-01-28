package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.common.util.AnnotationHelper;
import com.teamwizardry.librarianlib.common.util.UnsafeKt;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleRegistry {

	public static ModuleRegistry INSTANCE = new ModuleRegistry();

	public Set<Module> modules = new HashSet<>();

	private ModuleRegistry() {
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), Module.class, RegisterModule.class, (clazz, info) -> {
			try {
				registerModule((Module) UnsafeKt.getUnsafeSafely(0).allocateInstance(clazz));
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			return null;
		});

	}

	public void registerModule(Module module) {
		modules.add(module);
	}

	@Nullable
	public Module getModule(String id) {
		for (Module module : modules) if (module.getID().equals(id)) return module.copy();
		return null;
	}

	@Nullable
	public Module getModule(ItemStack itemStack) {
		for (Module module : modules)
			if (ItemStack.areItemStacksEqual(itemStack, module.getRequiredStack())) return module.copy();
		return null;
	}

	@Nullable
	public Module getModule(Item item) {
		for (Module module : modules) if (item == module.getRequiredStack().getItem()) return module.copy();
		return null;
	}

	@NotNull
	public Set<Module> getModules(ModuleType type) {
		Set<Module> modules = new HashSet<>();
		for (Module module : this.modules) if (module.getModuleType() == type) modules.add(module.copy());
		return modules;
	}
}
