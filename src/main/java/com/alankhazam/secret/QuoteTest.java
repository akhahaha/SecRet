package com.alankhazam.secret;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Quote unit tests
 * Created by Alan on 4/13/2016.
 */
public class QuoteTest {
    @Test(expected = IllegalArgumentException.class)
    public void illegalDate() throws Exception {
        new Quote("SPY", "20160606");
    }

    @Test
    public void knownQuote() throws Exception {
        Quote quote = new Quote("SPY", "20151201");
        assertEquals(BigDecimal.valueOf(209.440002), quote.open);
        assertEquals(BigDecimal.valueOf(208.358913), quote.adjustedClose);
    }
}
