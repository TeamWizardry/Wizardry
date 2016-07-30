package com.teamwizardry.wizardry.api.module.attribute;

import com.google.common.collect.Maps;

import java.util.*;

public class AttributeMap {

    protected HashMap<Attribute, List<AttributeModifier>> attributes = Maps.newHashMap();

    protected List<AttributeModifier> invalids = new ArrayList<>();
    protected Set<Attribute> validAttributes = new HashSet<>();

    protected HashMap<Attribute, List<AttributeModifier>> attributeCapture = Maps.newHashMap();
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
        if (isCapturing) attributeCapture.get(attribute).add(mod);
        else attributes.get(attribute).add(mod);

    }

    public double apply(Attribute attribute, double value) {
        Collection<AttributeModifier> list = attributes.get(attribute);

        HashMap<AttributeModifier.Priority, ArrayList<AttributeModifier>> priorityLists = new HashMap<AttributeModifier.Priority, ArrayList<AttributeModifier>>();

        priorityLists.putIfAbsent(AttributeModifier.Priority.HIGHEST, new ArrayList<>());
        priorityLists.putIfAbsent(AttributeModifier.Priority.HIGH, new ArrayList<>());
        priorityLists.putIfAbsent(AttributeModifier.Priority.NORMAL, new ArrayList<>());
        priorityLists.putIfAbsent(AttributeModifier.Priority.LOW, new ArrayList<>());
        priorityLists.putIfAbsent(AttributeModifier.Priority.LOWEST, new ArrayList<>());

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
