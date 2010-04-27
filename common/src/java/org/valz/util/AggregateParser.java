package org.valz.util;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateConfigParser;
import org.valz.util.aggregates.ParserException;

import java.util.Map;

import static org.valz.util.JsonUtils.makeJson;

public class AggregateParser {
    public static JSONValue toJson(AggregateRegistry registry, Aggregate<?> aggregate) {
        AggregateConfigParser parser = registry.get(aggregate.getName());
        return makeJson("name", aggregate.getName(), "config", parser.configToJson(aggregate));
    }

    public static Aggregate parse(AggregateRegistry registry, JSONValue jsonValue) throws ParserException {
        JSONObject jsonObject = (JSONObject)jsonValue;
        Map<String, JSONValue> map = jsonObject.getValue();
        String name = ((JSONString)map.get("name")).getValue();
        AggregateConfigParser configParser = registry.get(name);
        return configParser.parse(map.get("config"));
    }

    private AggregateParser() {
    }
}
