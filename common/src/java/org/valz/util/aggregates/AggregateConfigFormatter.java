package org.valz.util.aggregates;

import com.sdicons.json.model.JSONValue;

public interface AggregateConfigFormatter<T extends Aggregate<?>> {
    T parse(JSONValue jsonValue) throws ParserException;

    JSONValue configToJson(T aggregate);
}