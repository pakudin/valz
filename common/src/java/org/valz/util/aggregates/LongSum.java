package org.valz.util.aggregates;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class LongSum extends AbstractAggregate<Long> {

    @Override
    public Long reduce(@NotNull Iterator<Long> stream) {
        long res = 0;
        while (stream.hasNext()) {
            res += stream.next();
        }
        return res;
    }

    @Override
    public Long reduce(Long item1, Long item2) {
        return item1 + item2;
    }
}