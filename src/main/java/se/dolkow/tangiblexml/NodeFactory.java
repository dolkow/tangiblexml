package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static se.dolkow.tangiblexml.Util.TAG;

final class NodeFactory {

    static @NonNull <T,V> ParserNode<T> create(@NonNull Putter<T,V> putter, @NonNull Type what)
            throws SetupException {
        if (what instanceof Class) {
            Class cwhat = (Class)what;
            if (cwhat.equals(String.class)) {
                //noinspection unchecked
                return new TextNodes.Str<>((Putter<T,String>) putter);
            } else if (cwhat.equals(Integer.class)) {
                //noinspection unchecked
                return new TextNodes.Int<>((Putter<T,Integer>) putter);
            } else {
                //noinspection unchecked
                return new TangibleNode(putter, cwhat);
            }
        } else if (what instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) what;
            final Class raw, param;
            try {
                raw = (Class) pt.getRawType();
            } catch (ClassCastException e) {
                throw new SetupException("Expected the raw type to be a class", e);
            }

            final Type[] params = pt.getActualTypeArguments();
            if (params.length != 1) {
                throw new SetupException("Expected exactly one type parameter in " + pt);
            }
            try {
                param = (Class) params[0];
            } catch (ClassCastException e) {
                throw new SetupException("Type param must be a plain class, was " + params[0]);
            }
            if (List.class.isAssignableFrom(raw)) {
                //noinspection unchecked
                return new ListNode(putter, raw, param);
            } else {
                throw new SetupException("Unhandled type " + pt);
            }
        } else {
            throw new SetupException("Unhandled kind of type: " + what);
        }
    }

    static @NonNull <T> InnerNode<T> createTree(@NonNull Class<? extends T> what)
            throws SetupException {
        InnerNode<T> root = new InnerNode<>();
        TangibleFieldCache cache = TangibleFieldCache.getInstance();
        final Pair<Field, TangibleField>[] fields = cache.get(what);
        if (fields.length == 0) {
            String msg = "Potential bug: Created node tree for "
                    + what + ", which has no TangibleFields.";
            Log.w(TAG, msg);
        }
        for (Pair<Field, TangibleField> pair : fields) {
            Field f = pair.first;
            TangibleField a = pair.second;
            //noinspection unchecked
            root.extend(a.value().split("/"), new FieldPutter(f), f.getGenericType());
        }
        return root;
    }
}
