package org.stathissideris.ascii2image;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.stathissideris.ascii2image.core.CommandLineConverter;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestConversion {

    @Parameterized.Parameters(name= "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"text/art1.txt"},
                {"text/art10.txt"},
                {"text/art11.txt"},
                {"text/art12.txt"},
                {"text/art13.txt"},
                {"text/art14.txt"},
                {"text/art15.txt"},
                {"text/art16.txt"},
                {"text/art17.txt"},
                {"text/art18.txt"},
                {"text/art19.txt"},
                {"text/art2.txt"},
                {"text/art20.txt"},
                {"text/art2_5.txt"},
                {"text/art3.txt"},
                {"text/art3_5.txt"},
                {"text/art4.txt"},
                {"text/art5.txt"},
                {"text/art6.txt"},
                {"text/art7.txt"},
                {"text/art8.txt"},
                {"text/art_text.txt"},
                {"text/bug1.txt"},
                {"text/bug10.txt"},
                {"text/bug11.txt"},
                {"text/bug12.txt"},
                {"text/bug13.txt"},
                {"text/bug14.txt"},
                {"text/bug15.txt"},
                {"text/bug16.txt"},
                {"text/bug17.txt"},
                {"text/bug18.txt"},
                {"text/bug2.txt"},
                {"text/bug3.txt"},
                {"text/bug4.txt"},
                {"text/bug5.txt"},
                {"text/bug6.txt"},
                {"text/bug7.txt"},
                {"text/bug8.txt"},
                {"text/bug9.txt"},
                {"text/bug9_5.txt"},
                {"text/color_codes.txt"},
                {"text/corner_case01.txt"},
                {"text/corner_case02.txt"},
                {"text/ditaa_bug.txt"},
                {"text/ditaa_bug2.txt"},
                {"text/logo.txt"},
                {"text/simple_S01.txt"},
                {"text/simple_U01.txt"},
                {"text/simple_square01.txt"},
        });
    }

    private final String inputFile;

    public TestConversion(String inputFile)
    {
        this.inputFile = inputFile;
    }

    @Test
    public void testConversion() {
        InputStream in = getClass().getClassLoader().getResourceAsStream(inputFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int result = CommandLineConverter.convert(new String[]{}, in, out);
        assertEquals(0, result);
    }
}
