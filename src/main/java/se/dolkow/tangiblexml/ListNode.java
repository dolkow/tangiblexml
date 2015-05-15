package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;

final class ListNode<T,E> extends ParserNode<T> {
    private final @NonNull Class<? extends List<E>> what;
    private final @NonNull Putter<T,List<E>> where;
    private final @NonNull ParserNode<List<E>> elemParser;

    ListNode(@NonNull Putter<T,List<E>> where, @NonNull Class<? extends List<E>> what,
             @NonNull Class<? extends E> whatElem) throws SetupException {

        this.what = what;
        this.where = where;

        try {
            what.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new SetupException(what.getName() + " doesn't have a no-arg constructor");
        }

        elemParser = NodeFactory.create(new ListPutter<E>(), whatElem);
    }


    @Override
    public void prepare(@NonNull T target)
            throws ReflectionException, NotSupportedException, ValueCountException {
        try {
            if (where.get(target) == null) {
                where.put(target, what.newInstance());
            }
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void parse(@NonNull XmlPullParser xml, @NonNull T target)
            throws NotSupportedException, InputException, ReflectionException,
            ValueCountException, ConversionException, InvalidFieldException {
        List<E> list = where.get(target);
        if (list == null) {
            throw new RuntimeException("BUG: list is null, but should've been set in prepare()");
        }
        elemParser.parse(xml, list);
    }
}