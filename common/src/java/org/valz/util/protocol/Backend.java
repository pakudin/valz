package org.valz.util.protocol;

import org.json.simple.JSONObject;
import org.valz.util.aggregates.Aggregate;

import java.util.Collection;

/**
 * Created on: 27.03.2010 23:58:39
 */
public interface Backend {
    Aggregate<?> getAggregate(String name) throws RemoteException;

    Object getValue(String name) throws RemoteException;

    Collection<String> listVars() throws RemoteException;

    void submit(String name, Aggregate<?> aggregate, Object value) throws RemoteException;
}
