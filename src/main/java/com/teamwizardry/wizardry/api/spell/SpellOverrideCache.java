package com.teamwizardry.wizardry.api.spell;

import java.util.HashMap;

import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideException;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler;

public class SpellOverrideCache {
	private HashMap<String, ModuleOverrideHandler> handlers = new HashMap<>();
	
	public static final SpellOverrideCache INSTANCE = new SpellOverrideCache();
	
	private SpellOverrideCache() {
	}
	
	public ModuleOverrideHandler getHandler(SpellRing spellChain) {
		try {
			return getOrCreateHandler(spellChain, false);
		}
		catch(ModuleOverrideException e) {
			// Should not happen, as exception may only be thrown in case of initializeIfNotExisting=true
			throw new IllegalStateException("Unexpected exception. See cause.", e);
		}
	}
	
	ModuleOverrideHandler getOrCreateHandler(SpellRing spellChain, boolean initializeIfNotExisting) throws ModuleOverrideException {
		spellChain = spellChain.getRootRing();
		ModuleInstance[] overrides = ModuleOverrideHandler.getSequenceFromSpellRing(spellChain);
		String key = getCacheKey(overrides);
		
		ModuleOverrideHandler handler = handlers.get(key);
		if( handler == null ) {
			handler = new ModuleOverrideHandler(overrides);	// TODO: Remove exception. Or put initializations into spell builder.
			handlers.put(key, handler);
		}
		
		return handler;
	}
	
	/////////////
	
	public static String getCacheKey(ModuleInstance[] instances) {
		// TODO: Change me as soon as SpellRing is hierarchical.
		
		StringBuilder builder = new StringBuilder();
		for( ModuleInstance module : instances ) {
			if( builder.length() > 0 )
				builder.append(">");
			builder.append(module);
		}
		return builder.toString();
	}
}
