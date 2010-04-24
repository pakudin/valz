package org.valz.util.protocol;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.parser.JSONParser;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.ResponseMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

public class RemoteWriteBackend implements WriteBackend {
    private final String serverUrl;
    private final AggregateRegistry registry;


    public RemoteWriteBackend(String serverUrl, AggregateRegistry registry) {
        this.serverUrl = serverUrl;
        this.registry = registry;
    }

    public <T> void submit(String name, Aggregate<T> aggregate, T value) throws RemoteWriteException {
        getDataResponse(InteractionType.SUBMIT,
                new SubmitRequest<T>(name, aggregate, value));
        // TODO: save val at exception to queue and try send later
    }

    private <I,O> O getDataResponse(InteractionType<I,O> requestType, I request) throws RemoteWriteException {
        try {
            String response =
                    HttpConnector.post(serverUrl, new RequestMessage<I>(requestType, request).toJson().render(false));
            ResponseMessage<O> responseMessage = ResponseMessage.parse(registry, new JSONParser(new StringReader(response)) .nextValue());
            return responseMessage.getData();
        } catch (IOException e) {
            throw new RemoteWriteException(e);
        } catch (RecognitionException e) {
            throw new RemoteWriteException(e);
        } catch (TokenStreamException e) {
            throw new RemoteWriteException(e);
        } catch (ParserException e) {
            throw new RemoteWriteException(e);
        }
    }
}