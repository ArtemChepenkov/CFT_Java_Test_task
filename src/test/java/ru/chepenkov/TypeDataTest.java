package ru.chepenkov;

import org.junit.jupiter.api.Test;
import ru.chepenkov.DataTypes.TypeData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeDataTest {

    @Test
    void testAddAndGet() {
        TypeData<Integer> data = new TypeData<>();
        data.addElement(1);
        data.addElement(2);
        data.addElement(3);

        assertEquals(3, data.getAmount());
        assertEquals(List.of(1, 2, 3), data.getElements());
    }
}
