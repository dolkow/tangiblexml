package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

abstract class TextNodes<T,V> extends ParserNode<T> {
    private final @NonNull Putter<T,V> putter;


    TextNodes(@NonNull Putter<T, V> putter) {
        this.putter = putter;
    }

    protected abstract @NonNull V parse(@NonNull String txt);

    @Override
    public final void parse(@NonNull XmlPullParser xml, @NonNull T target)
            throws ConversionException, ValueCountException, ReflectionException, InputException {
        Util.consume(xml, START_TAG);
        String textblock = "";
        try {
            if (xml.getEventType() == TEXT) {
                textblock = xml.getText();
                Util.consume(xml, TEXT);
            }
        } catch (XmlPullParserException e) {
            throw new InputException(e);
        }
        Util.consume(xml, END_TAG);

        final V value;
        try {
            value = parse(textblock);
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
        putter.put(target, value);
    }

    @Override
    protected void prepare(@NonNull T t) {
        // do nothing.
    }

    public static class Int<T> extends TextNodes<T,Integer> {
        Int(Putter<T, Integer> putter) {
            super(putter);
        }

        @Override
        protected @NonNull Integer parse(@NonNull String txt) {
            return Integer.valueOf(txt);
        }
    }

    public static class Str<T> extends TextNodes<T,String> {
        Str(Putter<T, String> putter) {
            super(putter);
        }

        @Override
        protected @NonNull String parse(@NonNull String txt) {
            return txt;
        }
    }
}
