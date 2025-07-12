package ru.chepenkov.DataTypes;

import java.util.ArrayList;
import java.util.List;

public class TypeData<T> {

    private int amount = 0;
    private final List<T> elements = new ArrayList<>();

    public void addElement(T element) {
        elements.add(element);
        amount++;
    }

    public int getAmount() {
        return amount;
    }

    public List<T> getElements() {
        return elements;
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }



}
