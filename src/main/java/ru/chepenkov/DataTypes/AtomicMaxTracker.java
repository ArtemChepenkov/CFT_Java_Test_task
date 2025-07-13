package ru.chepenkov.DataTypes;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicMaxTracker<T extends Comparable<T>> {
    private final AtomicReference<T> max;

    public AtomicMaxTracker(T initialValue) {
        this.max = new AtomicReference<>(initialValue);
    }

    public void update(T candidate) {
        if (candidate == null) return;

        max.accumulateAndGet(candidate, (current, newVal) ->
                current == null ? newVal :
                        newVal.compareTo(current) == 1 ? newVal : current
        );
    }

    public T getMax() {
        return max.get();
    }
}