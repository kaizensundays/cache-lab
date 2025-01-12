package com.kaizensundays.eta.jgroups;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.jgroups.protocols.raft.LevelDBLog;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

/**
 * Created: Sunday 9/22/2024, 12:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@SuppressWarnings({
        "java:S117", // name
        "java:S1192", // constant
        "java:S3011", // private access
})
public class LevelDBLogExtended extends LevelDBLog {

    private static final Class<?> parent = LevelDBLog.class;

    public void set(String fieldName, Object value) {
        try {
            Field field = parent.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T get(String fieldName, Class<T> type) {
        try {
            Field field = parent.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Object invoke(String name, Object... args) {
        try {
            Class<?>[] parameterTypes = (args != null)
                    ? Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new)
                    : new Class<?>[0];
            Method method = parent.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method.invoke(this, args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void init(String log_name, Map<String, String> args) throws Exception {
        Options options = new Options().createIfMissing(true);
        File dbFileName = new File(log_name);
        set("dbFileName", dbFileName);
        DB db = factory.open(dbFileName, options);
        set("db", db);
        log.trace("opened %s", db);

        if ((boolean) invoke("isANewRAFTLog")) {
            log.trace("log %s is new, must be initialized", dbFileName);
            invoke("initLogWithMetadata");
        } else {
            log.trace("log %s exists, does not have to be initialized", dbFileName);
            invoke("readMetadataFromLog");
        }
        invoke("checkForConsistency");
    }

}
