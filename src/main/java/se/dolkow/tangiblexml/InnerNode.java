package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

/**
 * A node that parses its children, just passing the target along.
 */
final class InnerNode<T> extends ParserNode<T> {
    final HashMap<String,ParserNode<T>> children = new HashMap<>();

    @Override
    protected void prepare(@NonNull T target)
            throws NotSupportedException, ValueCountException, ReflectionException {
        for (ParserNode<T> child : children.values()) {
            child.prepare(target);
        }
    }

    @Override
    public void parse(@NonNull XmlPullParser xml, @NonNull T target)
            throws InputException, NotSupportedException, ValueCountException,
            ReflectionException, ConversionException, InvalidFieldException {
        try {
            Util.consume(xml, START_TAG);
            for (ParserNode node : children.values()) {
                //noinspection unchecked
                node.prepare(target);
            }
            while (xml.getEventType() != END_TAG) {
                if (xml.getEventType() != START_TAG) {
                    xml.next();
                    continue;
                }
                String name = xml.getName();
                ParserNode child = children.get(name);
                if (child == null) {
                    Util.skip(xml);
                } else {
                    //noinspection unchecked
                    child.parse(xml, target);
                }
            }
            Util.consume(xml, END_TAG);
        } catch (XmlPullParserException e) {
            throw new InputException(e);
        } catch (IOException e) {
            throw new InputException(e);
        }
    }

    @Override
    public void dump(@NonNull StringBuilder sb, int indent, @NonNull String prefix) {
        super.dump(sb, indent, prefix);

        for (Map.Entry<String,ParserNode<T>> entry : children.entrySet()) {
            entry.getValue().dump(sb, indent+2, entry.getKey());
        }
    }

    public <V> void extend(@NonNull String[] pathParts, @NonNull Putter<T,V> putter,
                           @NonNull Type leafType) throws SetupException {

        final int N = pathParts.length;
        if (N == 0) {
            throw new SetupException("BUG: node path is empty");
        }

        InnerNode<T> parent = this;
        for (int i=0; i<N-1; ++i) {
            String part = pathParts[i];
            if (part.length() == 0) {
                String msg = "BUG: empty component in " + Arrays.toString(pathParts);
                throw new SetupException(msg);
            }

            ParserNode child = parent.children.get(part);
            if (child == null) {
                child = new InnerNode<>();
                //noinspection unchecked
                parent.children.put(part, child);
            } else if (child.getClass() != InnerNode.class) {
                // if it's not *exactly* that class, we have a problem
                String arr = Arrays.toString(pathParts);
                String msg = "BUG: conflicting uses of " + part + " in " + arr;
                throw new SetupException(msg);
            }
            //noinspection unchecked
            parent = (InnerNode)child;
        }

        String part = pathParts[N-1];
        if (part.length() == 0) {
            throw new SetupException("BUG: empty component in " + Arrays.toString(pathParts));
        }
        ParserNode old = parent.children.put(part, NodeFactory.create(putter, leafType));
        if (old != null) {
            String msg = "BUG: double use of " + part + " in " + Arrays.toString(pathParts);
            throw new SetupException(msg);
        }
    }
}
