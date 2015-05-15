package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

/**
 * If you want this to be useful, you'd better keep a hard reference yourself. The singleton is
 * weakly linked, so if no one holds a reference, it'll be garbage collected.
 */
class TangibleFieldCache {

    private static @NonNull WeakReference<TangibleFieldCache> instance = new WeakReference<>(null);

    private final @NonNull HashMap<Class,Pair<Field,TangibleField>[]> map;

    public synchronized static TangibleFieldCache getInstance() {
        TangibleFieldCache cache = instance.get();
        if (cache == null) {
            cache = new TangibleFieldCache();
            instance = new WeakReference<>(cache);
        }
        return cache;
    }

    private TangibleFieldCache() {
        map = new HashMap<>();
    }

    public synchronized @NonNull Pair<Field,TangibleField>[] get(@NonNull Class clazz)
            throws InvalidFieldException {
        Pair<Field,TangibleField>[] fields = map.get(clazz);
        if (fields != null) {
            return fields;
        }

        ArrayList<Pair<Field,TangibleField>> flist = new ArrayList<>();
        find(clazz, flist);
        //noinspection unchecked
        fields = flist.toArray(new Pair[flist.size()]);
        map.put(clazz, fields);
        return fields;
    }

    private synchronized void find(final @NonNull Class clazz,
                                   final @NonNull ArrayList<Pair<Field,TangibleField>> out)
            throws InvalidFieldException {
        Class cur = clazz;
        while (cur != null) {
            for (Field f : cur.getDeclaredFields()) {
                TangibleField tang = f.getAnnotation(TangibleField.class);
                if (tang != null) {

                    if (f.getType().isPrimitive()) {
                        // primitives make the already-set (i.e. null) check hard...
                        throw new InvalidFieldException("Field " + f + " may not be primitive");
                    }

                    int mod = f.getModifiers();
                    if ((mod & PUBLIC) != PUBLIC) {
                        throw new InvalidFieldException("Field " + f + " must be public");
                    } else if ((mod & (STATIC|FINAL)) != 0) {
                        String msg = "Field " + f + " may not be static or final";
                        throw new InvalidFieldException(msg);
                    }

                    out.add(new Pair<>(f, tang));
                }
            }
            cur = cur.getSuperclass();
        }
    }
}
