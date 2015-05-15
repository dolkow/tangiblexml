package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

class Util {
    public static final String TAG = "TangibleXML";

    public static void consume(@NonNull XmlPullParser xml, int eventtype) throws InputException {
        try {
            xml.require(eventtype, null, null);
            xml.next();
        } catch (IOException e ) {
            throw new InputException(e);
        } catch (XmlPullParserException e) {
            throw new InputException(e);
        }
    }

    public static void skip(@NonNull XmlPullParser xml) throws InputException {
        try {
            xml.require(START_TAG, null, null);
            int depth = 1;
            while (depth != 0) {
                final int ev = xml.next();
                switch (ev) {
                    case START_TAG: depth++; break;
                    case END_TAG:   depth--; break;
                }
            }
            consume(xml, END_TAG);
        } catch (IOException e) {
            throw new InputException(e);
        } catch (XmlPullParserException e) {
            throw new InputException(e);
        }
    }
}
