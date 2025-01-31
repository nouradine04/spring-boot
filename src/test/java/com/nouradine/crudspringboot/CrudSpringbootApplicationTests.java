package com.nouradine.crudspringboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CrudSpringbootApplicationTests {

    @Test
    void testAjouterElement() {
        List<String> liste = new ArrayList<>();
        liste.add("Élément 1");
        assertEquals(1, liste.size());
    }

}
