package org.matilda.services.reflection;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ObjectRepository {
    @Inject
    ObjectIdGenerator mObjectIdGenerator;

    private final Map<Long, Object> mObjects;

    @Inject
    public ObjectRepository() {
        mObjects = new HashMap<>();
    }

    public long add(Object object) {
        long id = mObjectIdGenerator.generate(object);
        mObjects.put(id, object);
        return id;
    }

    public Object get(long id) {
        return mObjects.get(id);
    }

    public void remove(long id) {
        mObjects.remove(id);
    }
}
