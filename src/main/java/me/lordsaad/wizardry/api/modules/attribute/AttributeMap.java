package me.lordsaad.wizardry.api.modules.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Operation;
import me.lordsaad.wizardry.api.modules.attribute.AttributeModifier.Priority;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class AttributeMap
{

	protected Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();

	protected List<AttributeModifier> invalids = new ArrayList<>();
	protected Set<Attribute> validAttributes = new HashSet<>();

	protected Multimap<Attribute, AttributeModifier> attributeCapture = HashMultimap.create();
	protected List<AttributeModifier> invalidsCapture = new ArrayList<>();
	protected boolean isCapturing = false;
	protected boolean didHaveInvalid = false;

	public boolean didHaveInvalid()
	{
		return didHaveInvalid;
	}

	public void beginCaputure()
	{
		isCapturing = true;
	}

	public void endCapture(boolean add)
	{
		if (!isCapturing) return;
		isCapturing = false;
		if (add)
		{
			attributes.putAll(attributeCapture);
		}
		attributeCapture.clear();
		didHaveInvalid = false;
	}

	public void addAttribute(Attribute attribute)
	{
		validAttributes.add(attribute);
	}

	public void putModifier(Attribute attribute, AttributeModifier mod)
	{
		if (!validAttributes.contains(attribute))
		{
			if (isCapturing) invalidsCapture.add(mod);
			else invalids.add(mod);

			if (isCapturing) didHaveInvalid = true;
			return;
		}
		if (isCapturing) attributeCapture.put(attribute, mod);
		else attributes.put(attribute, mod);

	}

	public void apply(Attribute attribute, double value)
	{
		Collection<AttributeModifier> list = attributes.get(attribute);

		HashMap<Priority, ArrayList<AttributeModifier>> priorityLists = new HashMap<Priority, ArrayList<AttributeModifier>>();
		
		priorityLists.put(Priority.HIGHEST, new ArrayList<AttributeModifier>());
		priorityLists.put(Priority.HIGH, new ArrayList<AttributeModifier>());
		priorityLists.put(Priority.NORMAL, new ArrayList<AttributeModifier>());
		priorityLists.put(Priority.LOW, new ArrayList<AttributeModifier>());
		priorityLists.put(Priority.LOWEST, new ArrayList<AttributeModifier>());

		for (AttributeModifier mod : list)
		{
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

	}
}
