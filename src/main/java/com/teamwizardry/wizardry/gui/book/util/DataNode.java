package com.teamwizardry.wizardry.gui.book.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataNode {

    public static final DataNode NULL = new DataNodeNull();
    private static final int RANDOM_NUMBER_CHECK_VALUE = -262920932;
    protected EnumNodeType type;
    protected String stringValue;
    protected Map<String, DataNode> mapValue;
    protected List<DataNode> listValue;

    public DataNode(String value) {
        type = EnumNodeType.STRING;
        stringValue = value;
    }

    public DataNode(Map<String, DataNode> value) {
        type = EnumNodeType.MAP;
        mapValue = value;
    }

    public DataNode(List<DataNode> value) {
        type = EnumNodeType.LIST;
        listValue = value;
    }

    public static DataNode str(String value) {
        return new DataNode(value);
    }

    public static DataNode map() {
        return new DataNode(new HashMap<>());
    }

    public static DataNode list() {
        return new DataNode(new ArrayList<>());
    }

    public boolean exists() {
        return true;
    }

    public boolean isString() {
        return type == EnumNodeType.STRING;
    }

    public boolean isInt() {
        return type == EnumNodeType.STRING && asIntOr(RANDOM_NUMBER_CHECK_VALUE) != RANDOM_NUMBER_CHECK_VALUE;
    }

    public boolean isDouble() {
        return type == EnumNodeType.STRING && asDoubleOr(RANDOM_NUMBER_CHECK_VALUE) != RANDOM_NUMBER_CHECK_VALUE;
    }

    public boolean isList() {
        return type == EnumNodeType.LIST;
    }

    public boolean isMap() {
        return type == EnumNodeType.MAP;
    }

    public String asStringOr(String defaultValue) {
        if (!isString())
            return defaultValue;
        return stringValue;
    }

    public String asString() {
        return asStringOr(null);
    }

    public int asIntOr(int i) {
        String str = asStringOr(null);
        if (str == null)
            return i;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // TODO: logging
        }
        return i;
    }

    public int asInt() {
        return asIntOr(0);
    }

    public double asDoubleOr(double i) {
        String str = asStringOr(null);
        if (str == null)
            return i;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            // TODO: logging
        }
        return i;
    }

    public double asDouble() {
        return asIntOr(0);
    }

    public Map<String, DataNode> asMap() {
        if (!isMap())
            return ImmutableMap.of();
        return mapValue;
    }

    public List<DataNode> asList() {
        if (!isList())
            return ImmutableList.of();
        return listValue;
    }

    public DataNode get(String key) {
        if (!isMap() || !mapValue.containsKey(key))
            return NULL;
        return mapValue.get(key);
    }

    public DataNode get(int index) {
        if (!isList() || index < 0 || index >= listValue.size())
            return NULL;
        return listValue.get(index);
    }

    public DataNode getValue(String... path) {
        DataNode node = this;

        for (String part : path) {
            if (node == NULL)
                break;
            node = node.get(part);
        }
        return node;
    }

    public boolean put(String key, String str) {
        return put(key, DataNode.str(str));
    }

    public boolean put(String key, DataNode node) {
        if (!isMap())
            return false;

        asMap().put(key, node);

        return true;
    }

    public boolean put(int index, String str) {
        return put(index, DataNode.str(str));
    }

    public boolean put(int index, DataNode node) {
        if (!isList())
            return false;

        asList().set(index, node);

        return true;
    }

    public boolean add(String str) {
        return add(DataNode.str(str));
    }

    public boolean add(DataNode node) {
        if (!isList())
            return false;

        asList().add(node);

        return true;
    }

    @Override
    public String toString() {
        return "DataNode(" + type + ")-[" + (type == EnumNodeType.MAP ? mapValue : type == EnumNodeType.LIST ? listValue : stringValue) + "]";
    }

    private enum EnumNodeType {
        STRING, LIST, MAP
    }

    private static class DataNodeNull extends DataNode {
        public DataNodeNull() {
            super("");
            this.type = null;
            this.listValue = null;
            this.mapValue = null;
        }

        @Override
        public boolean exists() {
            return false;
        }

        public String asStringOr(String defaultValue) {
            return defaultValue;
        }

        public String asString() {
            return asStringOr(null);
        }

        public Map<String, DataNode> asMap() {
            return ImmutableMap.of();
        }

        public List<DataNode> asList() {
            return ImmutableList.of();
        }

        public DataNode get(String key) {
            return NULL;
        }

        public DataNode get(int index) {
            return NULL;
        }

        public DataNode getValue(String... path) {
            return NULL;
        }
    }

}
