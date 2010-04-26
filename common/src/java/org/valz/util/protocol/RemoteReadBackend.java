package org.valz.util.protocol;

import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.Value;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.protocol.messages.InteractionType;

import java.io.StringReader;
import java.util.Collection;

public class RemoteReadBackend implements ReadBackend {
    private final ReadConfiguration conf;
    private final AggregateRegistry registry;

    public RemoteReadBackend(ReadConfiguration conf, AggregateRegistry registry) {
        this.conf = conf;
        this.registry = registry;
    }

    public Aggregate<?> getAggregate(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_AGGREGATE, name);
    }

    public Value getValue(String name) throws RemoteReadException {
        return getDataResponse(InteractionType.GET_VALUE, name);
    }

    public Collection<String> listVars() throws RemoteReadException {
        return getDataResponse(InteractionType.LIST_VARS, null);
    }

    private <I, O> O getDataResponse(InteractionType<I, O> type, I request) throws RemoteReadException {
        try {
            String response = HttpConnector.post(
                    conf.getServerUrls().get(0),
                    InteractionType.requestToJson(type, request, registry).render(false));
            JSONValue responseJson = new JSONParser(new StringReader(response)).nextValue();
            return (O) InteractionType.responseFromJson(responseJson, registry).second;
        } catch (Exception e) {
            throw new RemoteReadException(e);
        } 
    }
}
