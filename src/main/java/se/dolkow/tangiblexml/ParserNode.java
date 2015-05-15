package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;

/**
 * @param <Target> the type of the target that will receive the result. May be Void for "none".
 */
abstract class ParserNode<Target> {

    /**
     * Parse your field, set it on target as appropriate.
     *
     * @param xml       source of xml parse events
     * @param target    the object on which we will set the results
     */
    public abstract void parse(@NonNull XmlPullParser xml, @NonNull Target target)
            throws InputException, ValueCountException, ReflectionException,
            ConversionException, NotSupportedException, InvalidFieldException;

    /**
     * This is the perfect place to set up the base value for target, like an empty list.
     * Note that this may be called several times for the same target!
     *
     * @param target    the object on which we will set the results
     */
    protected abstract void prepare(@NonNull Target target)
            throws ReflectionException, NotSupportedException, ValueCountException;

    /**
     * Produce a dump of the ParserNode tree, for debugging.
     * Don't forget the newline! :)
     * @param sb        a buffer to store the dump in.
     * @param indent    number of spaces to indent
     * @param prefix    a prefix (e.g. the node's name in the parent)
     */
    public void dump(@NonNull StringBuilder sb, int indent, @NonNull String prefix) {
        for (int i = 0; i < indent; ++i) {
            sb.append(' ');
        }
        sb.append("+ ");
        sb.append(prefix);
        sb.append(" : ");
        sb.append(this.toShortString());
        sb.append('\n');
    }

    protected @NonNull String toShortString() {
        return getClass().getSimpleName();
    }

}
