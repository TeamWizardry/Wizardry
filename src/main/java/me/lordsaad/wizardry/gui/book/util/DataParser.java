package me.lordsaad.wizardry.gui.book.util;

import com.google.gson.*;
import jline.internal.InputStreamReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataParser {

    private static JsonParser parser = new JsonParser();

    public static DataNode parse(InputStream stream) {
        JsonElement jsonElement = parser.parse(new InputStreamReader(stream));

        return parseNode(jsonElement);
    }

    private static DataNode parseNode(JsonElement elem) {
        if (elem instanceof JsonObject)
            return parseObject((JsonObject) elem);
        if (elem instanceof JsonArray)
            return parseList((JsonArray) elem);
        if (elem instanceof JsonNull)
            return null;
        return parseOther(elem);
    }

    private static DataNode parseObject(JsonObject object) {

        Map<String, DataNode> map = new HashMap<>();

        for (Entry<String, JsonElement> entry : object.entrySet()) {
            DataNode node = parseNode(entry.getValue());
            if (node != null)
                map.put(entry.getKey(), node);
        }

        return new DataNode(map);
    }

    private static DataNode parseList(JsonArray array) {

        List<DataNode> list = new ArrayList<>();

        for (JsonElement elem : array) {
            DataNode node = parseNode(elem);
            if (node != null)
                list.add(node);
        }

        return new DataNode(list);
    }

    private static DataNode parseOther(JsonElement elem) {

        if (elem instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive) elem;
            return new DataNode(primitive.getAsString());
        }


        return new DataNode("!!ERROR!!");
    }

}
