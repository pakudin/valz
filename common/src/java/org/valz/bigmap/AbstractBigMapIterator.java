package org.valz.bigmap;

import org.valz.aggregates.Aggregate;
import org.valz.backends.RemoteReadException;
import org.valz.keytypes.KeyType;
import org.valz.protocol.messages.BigMapChunkValue;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractBigMapIterator<K,T> implements BigMapIterator<K,T> {
    private final String name;

    private Iterator<Map.Entry<K, T>> curIterator = null;
    protected K curKey = null;
    protected final int chunkSize;
    protected KeyType<K> keyType;
    protected Aggregate<T> aggregate;

    public AbstractBigMapIterator(String name, int chunkSize) {
        this.chunkSize = chunkSize;
        this.name = name;
    }

    public boolean hasNext() {
        return getIterator().hasNext();
    }

    public Map.Entry<K, T> next() {
        Map.Entry<K, T> entry = getIterator().next();
        curKey = entry.getKey();
        return entry;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Iterator<Map.Entry<K, T>> getIterator() {
        if (curIterator == null || !curIterator.hasNext()) {
            try {
                curIterator = getNextIterator(curIterator != null);
            } catch (RemoteReadException e) {
                // return empty iterator
                return new TreeMap<K, T>().entrySet().iterator();
            }
        }
        return curIterator;
    }


    public Iterator<Map.Entry<K, T>> getNextIterator(boolean passFirstItem) throws RemoteReadException {
        BigMapChunkValue<K, T> chunkValue = getNextChunk(name, curKey, chunkSize);
        keyType = chunkValue.getKeyType();
        aggregate = chunkValue.getAggregate();
        Iterator<Map.Entry<K, T>> iter =
                chunkValue.getValue().entrySet().iterator();
        if (iter.hasNext() && passFirstItem) {
            iter.next();
        }
        return iter;
    }

    public KeyType<K> getKeyType() {
        return keyType;
    }

    public Aggregate<T> getAggregate() {
        return aggregate;
    }
}