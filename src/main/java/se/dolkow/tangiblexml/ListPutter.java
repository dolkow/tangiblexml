package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import java.util.List;

class ListPutter<Value> implements Putter<List<Value>, Value> {
    @Override
    public void put(@NonNull List<Value> list, @NonNull Value value) {
        list.add(value);
    }

    @Override
    public Value get(@NonNull List<Value> values) throws NotSupportedException {
        throw new NotSupportedException("BUG: can't get value from ListPutter");
    }
}
