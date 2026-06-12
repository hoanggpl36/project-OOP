package com.hlu.preprocessing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataPreprocessorTest {

    @Test
    public void testCleanText_NormalText() {
        String input = "Bão Yagi rất lớn!";
        String expected = "bão yagi rất lớn";
        assertEquals(expected, DataPreprocessor.cleanText(input));
    }

    @Test
    public void testCleanText_WithSpecialChars() {
        String input = "Cứu!!! Điện thoại bị hỏng...";
        String expected = "cứu điện thoại bị hỏng";
        assertEquals(expected, DataPreprocessor.cleanText(input));
    }

    @Test
    public void testCleanText_DoubleSpaces() {
        String input = "   Bão    Yagi   ";
        String expected = "bão yagi";
        assertEquals(expected, DataPreprocessor.cleanText(input));
    }

    @Test
    public void testCleanText_NullInput() {
        assertEquals("", DataPreprocessor.cleanText(null));
    }
}
