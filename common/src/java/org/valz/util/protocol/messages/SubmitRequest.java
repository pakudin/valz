package org.valz.util.protocol.messages;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.AggregateParser;
import org.valz.util.aggregates.ParserException;
import org.valz.util.protocol.InteractionType;

import static org.valz.util.Utils.makeJson;

public class SubmitRequest<T> {

    public static SubmitRequest parse(AggregateRegistry registry, JSONValue json) throws ParserException {
        JSONObject jsonObject = (JSONObject)json;
        String name = ((JSONString)jsonObject.get("name")).getValue();
        AggregateParser aggregateParser = registry.get(name);
        Aggregate aggregate = aggregateParser.parse(jsonObject.get("aggregate"));
        Object value = aggregate.parseData(jsonObject.get("value"));

        return new SubmitRequest(name, aggregate, value);
    }


    public String getName() {
        return name;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }

    public T getValue() {
        return value;
    }

    private String name;
    private Aggregate<T> aggregate;
    private T value;


    public SubmitRequest(String name, Aggregate<T> aggregate, T value) {
        this.name = name;
        this.aggregate = aggregate;
        this.value = value;
    }

    public JSONValue toJson() {
        return makeJson(
                "name", name,
                "aggregate", aggregate.toJson(),
                "value", aggregate.dataToJson(value));
    }
}
