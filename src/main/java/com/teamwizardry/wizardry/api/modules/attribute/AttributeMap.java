package com.teamwizardry.wizardry.api.modules.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class AttributeMap {

    protected Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();

    protected List<AttributeModifier> invalids = new ArrayList<>();
    protected Set<Attribute> validAttributes = new HashSet<>();

    protected Multimap<Attribute, AttributeModifier> attributeCapture = HashMultimap.create();
    protected List<AttributeModifier> invalidsCapture = new ArrayList<>();
    protected boolean isCapturing = false;
    protected boolean didHaveInvalid = false;

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
            attributes.putAll(attributeCapture);
        }
        attributeCapture.clear();
        didHaveInvalid = false;
    }

    public void addAttribute(Attribute attribute) {
        validAttributes.add(attribute);
    }

    public void putModifier(Attribute attribute, AttributeModifier mod) {
        if (!validAttributes.contains(attribute)) {
            if (isCapturing) invalidsCapture.add(mod);
            else invalids.add(mod);

            if (isCapturing) didHaveInvalid = true;
            return;
        }
        if (isCapturing) attributeCapture.put(attribute, mod);
        else attributes.put(attribute, mod);

    }

    public double apply(Attribute attribute, double value) {
        Collection<AttributeModifier> list = attributes.get(attribute);

        HashMap<AttributeModifier.Priority, ArrayList<AttributeModifier>> priorityLists = new HashMap<AttributeModifier.Priority, ArrayList<AttributeModifier>>();

        priorityLists.put(AttributeModifier.Priority.HIGHEST, new ArrayList<AttributeModifier>());
        priorityLists.put(AttributeModifier.Priority.HIGH, new ArrayList<AttributeModifier>());
        priorityLists.put(AttributeModifier.Priority.NORMAL, new ArrayList<AttributeModifier>());
        priorityLists.put(AttributeModifier.Priority.LOW, new ArrayList<AttributeModifier>());
        priorityLists.put(AttributeModifier.Priority.LOWEST, new ArrayList<AttributeModifier>());

        for (AttributeModifier mod : list) {
            if (mod.op == AttributeModifier.Operation.ADD) priorityLists.get(mod.priority).add(0, mod);
            else priorityLists.get(mod.priority).add(mod);
        }

        for (AttributeModifier mod : priorityLists.get(AttributeModifier.Priority.HIGHEST))
            value = mod.apply(value);
        for (AttributeModifier mod : priorityLists.get(AttributeModifier.Priority.HIGH))
            value = mod.apply(value);
        for (AttributeModifier mod : priorityLists.get(AttributeModifier.Priority.NORMAL))
            value = mod.apply(value);
        for (AttributeModifier mod : priorityLists.get(AttributeModifier.Priority.LOW))
            value = mod.apply(value);
        for (AttributeModifier mod : priorityLists.get(AttributeModifier.Priority.LOWEST))
            value = mod.apply(value);

        return value;
    }
}
