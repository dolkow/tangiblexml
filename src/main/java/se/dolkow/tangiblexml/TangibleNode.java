package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;

import static se.dolkow.tangiblexml.Util.TAG;

/**
 * A node that dynamically creates an object and uses its @TangibleField fields to populate it.
 */
final class TangibleNode<T,V> extends ParserNode<T> {
    private final @NonNull Class<? extends V> what;
    private final @NonNull Putter<T,V> putter;
    private final @NonNull InnerNode<V> valueRoot;

    TangibleNode(@NonNull Putter<T, V> putter, @NonNull Class<? extends V> what)
            throws SetupException {
        this.putter = putter;
        this.what = what;

        try {
            what.getConstructor();
        } catch (NoSuchMethodException e) {
            String msg = what.getName() + " doesn't have an accessible no-arg constructor";
            throw new SetupException(msg);
        }

        valueRoot = NodeFactory.createTree(what);
    }

    @Override
    protected void prepare(@NonNull T target) {
        // do nothing.
    }

    @Override
    public void parse(@NonNull XmlPullParser xml, @NonNull T target)
            throws InputException, InvalidFieldException, ValueCountException,
            ConversionException, NotSupportedException, ReflectionException {
        if (Parser.debug) {
            Log.d(TAG, "trying to parse " + what.getSimpleName() + " from " + xml.getName());
        }
        final V result;
        try {
            result = what.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException("Failed to create instance of " + what.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Failed to create instance of " + what.getName(), e);
        }
        valueRoot.parse(xml, result);

        TangibleFieldCache cache = TangibleFieldCache.getInstance();
        try {
            for (Pair<Field, TangibleField> pair : cache.get(result.getClass())) {
                Field f = pair.first;
                TangibleField a = pair.second;
                if (a.required() && f.get(result) == null) {
                    String msg = "Required field '" + f + "' missing";
                    throw new ValueCountException(msg);
                }
            }
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }

        putter.put(target, result);
    }

    @Override
    public void dump(@NonNull StringBuilder sb, int indent, @NonNull String prefix) {
        super.dump(sb, indent, prefix);
        valueRoot.dump(sb, indent+2, prefix+":content");
    }
}
