package com.alibaba.fastjson2.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Tag("util")
public class TypeUtilsTest2 {




    @Test
    public void parseBigDecimal() {
        String[] strings = new String[]{
                "12.34",
                ".34",
                "123.",
                "123.4",
                "1234.56",
                "1.23456E1",
                "123E1",
                "123E-1",
                "1234567890.1234567890",
                "12345678901234567890.1234567890",
                "123456789012345678901234567890.1234567890",
                "1234567890123456789012345678901234567890.1234567890",
                "12345678901234567890123456789012345678901234567890.1234567890",
                "123456789012345678901234567890123456789012345678901234567890.1234567890",
                "1234567890123456789012345678901234567890123456789012345678901234567890.1234567890",
                "-12.34",
                "-.34",
                "-123.",
                "-123.4",
                "-1234.56",
                "-1.23456",
                "-1.23456E1",
                "-123E1",
                "-123E-1",
                "-1234567890.1234567890",
                "-12345678901234567890.1234567890",
                "-123456789012345678901234567890.1234567890",
                "-1234567890123456789012345678901234567890.1234567890",
                "-12345678901234567890123456789012345678901234567890.1234567890",
                "-123456789012345678901234567890123456789012345678901234567890.1234567890",
                "-1234567890123456789012345678901234567890123456789012345678901234567890.1234567890"
        };

        for (String string : strings) {
            byte[] bytes = ("a," + string).getBytes();
            BigDecimal decimal = IOUtils.parseBigDecimal(bytes, 2, string.length());
            assertEquals(new BigDecimal(string), decimal);

            char[] chars = ("a," + string).toCharArray();
            BigDecimal decimal1 = IOUtils.parseBigDecimal(chars, 2, string.length());
            assertEquals(new BigDecimal(string), decimal1);
        }

        for (String string : strings) {
            byte[] bytes = ("ab," + string).getBytes();
            BigDecimal decimal = IOUtils.parseBigDecimal(bytes, 3, string.length());
            assertEquals(new BigDecimal(string), decimal);

            char[] chars = ("ab," + string).toCharArray();
            BigDecimal decimal1 = IOUtils.parseBigDecimal(chars, 3, string.length());
            assertEquals(new BigDecimal(string), decimal1);
        }

        for (String string : strings) {
            byte[] bytes = ("abc," + string).getBytes();
            BigDecimal decimal = IOUtils.parseBigDecimal(bytes, 4, string.length());
            assertEquals(new BigDecimal(string), decimal);

            char[] chars = ("abc," + string).toCharArray();
            BigDecimal decimal1 = IOUtils.parseBigDecimal(chars, 4, string.length());
            assertEquals(new BigDecimal(string), decimal1);
        }

        assertNull(IOUtils.parseBigDecimal(new byte[128], 0, 0));
        assertNull(IOUtils.parseBigDecimal(new char[128], 0, 0));
    }






}
