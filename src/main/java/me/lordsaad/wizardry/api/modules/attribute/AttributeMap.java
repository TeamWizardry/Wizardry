package me.lordsaad.wizardry.api.modules.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class AttributeMap {

	protected Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
	protected List<AttributeModifier> invalids = new ArrayList<>();
	protected Set<Attribute> validAttributes = new HashSet<>();
	
	public void addAttribute(Attribute attribute) {
		validAttributes.add(attribute);
	}
	
	public void putModifier(Attribute attribute, AttributeModifier mod) {
		if(!validAttributes.contains(attribute)) {
			invalids.add(mod);
			return;
		}
		attributes.put(attribute, mod);
	}
	
	public void apply(Attribute attribute, double value) {
		Collection<AttributeModifier> list = attributes.get(attribute);
		
		for (AttributeModifier mod : list) {
			value = mod.apply(value);
		}
	}
	
}
