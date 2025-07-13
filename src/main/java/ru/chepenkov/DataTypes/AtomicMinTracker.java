package ru.chepenkov.DataTypes;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicMinTracker<T extends Comparable<T>> {
    private final AtomicReference<T> min;

    public AtomicMinTracker(T initialValue) {
        this.min = new AtomicReference<>(initialValue);
    }

    public void update(T candidate) {
        if (candidate == null) return;

        min.accumulateAndGet(candidate, (current, newVal) ->
                current == null ? newVal :
                        newVal.compareTo(current) == -1 ? newVal : current
        );
    }

    public T getMin() {
        return min.get();
    }
}