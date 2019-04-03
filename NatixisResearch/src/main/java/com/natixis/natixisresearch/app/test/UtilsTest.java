package com.natixis.natixisresearch.app.test;

import android.test.InstrumentationTestCase;

import com.natixis.natixisresearch.app.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;

/**
 * Created by Thibaud on 24/04/2017.
 */
public class UtilsTest extends InstrumentationTestCase {
    public void testDotNetDateConversion(){

        assertEquals(Utils.convertDotNetDate("/Date(1391075730000+0100)/").getTime(),1391079330000L);
        assertEquals(Utils.convertDotNetDate("/Date(1391075730000+0200)/").getTime(),1391082930000L);

    }
}
