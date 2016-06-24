package me.lordsaad.wizardry.api.modules.attribute;

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
        if (!isCapturing)
            return;
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
            if (isCapturing)
                invalidsCapture.add(mod);
            else
                invalids.add(mod);

            if (isCapturing)
                didHaveInvalid = true;
            return;
        }
        if (isCapturing)
            attributeCapture.put(attribute, mod);
        else
            attributes.put(attribute, mod);

    }

    public void apply(Attribute attribute, double value) {
        Collection<AttributeModifier> list = attributes.get(attribute);

        for (AttributeModifier mod : list) {
            value = mod.apply(value);
        }
    }

}
