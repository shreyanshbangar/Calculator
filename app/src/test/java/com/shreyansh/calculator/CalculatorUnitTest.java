package com.shreyansh.calculator;

import org.junit.Test;
import static org.junit.Assert.*;

public class CalculatorUnitTest {

    @Test
    public void testChackCalculatorworkingfine() {
        Calculator calc= new Calculator();
        int answer = (int)(calc.compute("10/2-20"));
        assertEquals("Calculator Not working fine",-15, answer);
    }
}
