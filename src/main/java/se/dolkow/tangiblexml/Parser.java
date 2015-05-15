package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES;
import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static se.dolkow.tangiblexml.Util.TAG;

/**
 * Parse XML into objects.
 *
 * The parser object is reusable and reentrant.
 *
 * @param <E> the type of the result
 */
public class Parser<E> {
    private final @NonNull FieldPutter<Holder<E>,E> putter;
    private final @NonNull ParserNode<Holder<E>> root;
    private final @NonNull String rootName;
    private final @NonNull TangibleFieldCache cache = TangibleFieldCache.getInstance();
    static boolean debug = false;

    public Parser(@NonNull Class<E> resultClass) throws SetupException {
        Trace.beginSection("parser init " + resultClass.getSimpleName());

        TangibleRoot annot = resultClass.getAnnotation(TangibleRoot.class);
        if (annot == null) {
            String cname = resultClass.getSimpleName();
            String aname = TangibleRoot.class.getSimpleName();
            throw new SetupException(cname + " doesn't have annotation " + aname);
        }

        String path = annot.value();
        if (!path.startsWith("/")) {
            String msg = "BUG: path for " + resultClass.getSimpleName() + " is not absolute";
            throw new SetupException(msg);
        }
        String[] parts = path.substring(1).split("/");

        try {
            putter = new FieldPutter<>(Holder.class.getField("value"));
        } catch (NoSuchFieldException e) {
            throw new SetupException("BUG: can't find Holder.value field", e);
        }
        rootName = parts[0];

        InnerNode<Holder<E>> builder = new InnerNode<>();
        builder.extend(parts, putter, resultClass);
        if (builder.children.size() != 1) {
            throw new SetupException("Need exactly one root, found " + builder.children.size());
        }

        final ParserNode<Holder<E>> root = builder.children.get(rootName);
        if (root == null) {
            Object act = builder.children.keySet().toArray()[0];
            throw new SetupException("Unexpected root name " + act + ", wanted " + rootName);
        }
        this.root = root;

        if (debug) {
            StringBuilder sb = new StringBuilder();
            root.dump(sb, 0, rootName);
            Log.d(TAG, sb.toString());
        }

        Trace.endSection();
    }

    public static void setDebug(boolean on) {
        debug = on;
    }

    public @NonNull E parse(@NonNull XmlPullParser xml)
            throws ConversionException, InputException, ValueCountException,
            NotSupportedException, InvalidFieldException, ReflectionException {
        Trace.beginSection(getClass().getSimpleName() + " parse");

        Holder<E> holder = new Holder<>();
        try {
            xml.setFeature(FEATURE_PROCESS_NAMESPACES, false);
            xml.require(START_DOCUMENT, null, null);

            while (xml.getEventType() != END_DOCUMENT) {
                if (xml.getEventType() != START_TAG) {
                    xml.next();
                    continue;
                }
                String name = xml.getName();
                if (name.equals(rootName)) {
                    root.parse(xml, holder);
                } else {
                    Util.skip(xml);
                }
            }
            E result = putter.get(holder);
            if (result == null) {
                throw new ValueCountException("Failed to find result node");
            }
            validate(result);
            return result;
        } catch (IOException e) {
            throw new InputException(e);
        } catch (XmlPullParserException e) {
            throw new InputException(e);
        } finally {
            Trace.endSection();
        }
    }

    private void validate(@NonNull Object obj)
            throws ValueCountException, ReflectionException, InvalidFieldException {
        if (obj instanceof Iterable) {
            for (Object child : (Iterable)obj) {
                validate(child);
            }
        }
        try {
            for (Pair<Field,TangibleField> pair : cache.get(obj.getClass())) {
                final Field f = pair.first;
                final TangibleField tang = pair.second;

                Object child = f.get(obj);
                if (tang.required()) {
                    if (child == null) {
                        String msg = obj + "is missing required field " + f;
                        msg += ", expected at " + tang.value();
                        throw new ValueCountException(msg);
                    } else if (child instanceof Collection && ((Collection)child).isEmpty()) {
                        String msg = f + " in " + obj + " is empty";
                        msg += ", expected to find elements at " + tang.value();
                        throw new ValueCountException(msg);
                    }
                }

                if (child != null) {
                    validate(child);
                }
            }
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    private static class Holder<E> {
        @SuppressWarnings("unused")
        public @Nullable E value;
    }
}
