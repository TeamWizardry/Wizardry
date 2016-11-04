package com.teamwizardry.wizardry.api.module.attribute;

import com.google.common.collect.Maps;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Priority;

import java.util.*;
import java.util.Map.Entry;

public class AttributeMap {

	protected HashMap<Attribute, List<AttributeModifier>> attributes = Maps.newHashMap();

	protected List<AttributeModifier> invalids = new ArrayList<>();
	protected Set<Attribute> validAttributes = new HashSet<>();

	protected HashMap<Attribute, List<AttributeModifier>> attributeCapture = Maps.newHashMap();
	protected List<AttributeModifier> invalidsCapture = new ArrayList<>();
	protected boolean isCapturing;
	protected boolean didHaveInvalid;

	public boolean didHaveInvalid() {
		return didHaveInvalid;
	}

	public void beginCaputure() {
		isCapturing = true;
	}

	public void endCapture(boolean add) {
		if (!isCapturing) return;
		isCapturing = false;
		if (add) {
			for (Entry<Attribute, List<AttributeModifier>> attributeListEntry : attributeCapture.entrySet()) {
				List<AttributeModifier> mods = attributeListEntry.getValue();
				attributes.get(attributeListEntry.getKey()).addAll(mods);
				mods.clear();
			}
		}
		didHaveInvalid = false;
	}

	public void addAttribute(Attribute attribute) {
		validAttributes.add(attribute);
		attributeCapture.put(attribute, new ArrayList<>());
		attributes.put(attribute, new ArrayList<>());
	}

	public void putModifier(Attribute attribute, AttributeModifier mod) {
		if (mod == null) return;
		if (!validAttributes.contains(attribute)) {
			if (isCapturing) invalidsCapture.add(mod);
			else invalids.add(mod);

			if (isCapturing) didHaveInvalid = true;
			return;
		}
		if (attributeCapture.get(attribute) != null) {
			if (isCapturing) attributeCapture.get(attribute).add(mod);
			else attributes.get(attribute).add(mod);
		}

	}

	public double apply(Attribute attribute, double value) {
		List<AttributeModifier> list = attributes.get(attribute);

		Map<Priority, ArrayList<AttributeModifier>> priorityLists = new HashMap<>();

		priorityLists.putIfAbsent(Priority.HIGHEST, new ArrayList<>());
		priorityLists.putIfAbsent(Priority.HIGH, new ArrayList<>());
		priorityLists.putIfAbsent(Priority.NORMAL, new ArrayList<>());
		priorityLists.putIfAbsent(Priority.LOW, new ArrayList<>());
		priorityLists.putIfAbsent(Priority.LOWEST, new ArrayList<>());

		if (list != null)
			for (AttributeModifier mod : list) {
				if (mod == null) continue;
				if (mod.op == Operation.ADD) priorityLists.get(mod.priority).add(0, mod);
				else priorityLists.get(mod.priority).add(mod);
			}

		for (AttributeModifier mod : priorityLists.get(Priority.HIGHEST))
			value = mod.apply(value);
		for (AttributeModifier mod : priorityLists.get(Priority.HIGH))
			value = mod.apply(value);
		for (AttributeModifier mod : priorityLists.get(Priority.NORMAL))
			value = mod.apply(value);
		for (AttributeModifier mod : priorityLists.get(Priority.LOW))
			value = mod.apply(value);
		for (AttributeModifier mod : priorityLists.get(Priority.LOWEST))
			value = mod.apply(value);

		return value;
	}
}
