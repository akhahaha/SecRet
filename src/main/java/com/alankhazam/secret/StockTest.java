package com.alankhazam.secret;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Stock unit tests
 * Created by Alan on 4/14/2016.
 */
public class StockTest {
    @Test
    public void quoteCount() throws Exception {
        Stock stock = new Stock("SPY", "20151201", "20151209");
        assertEquals(7, stock.quotes.size());
    }

    @Test
    public void cumulative() throws Exception {
        Quote start = new Quote("SPY", "20151201");
        Quote end = new Quote("SPY", "20151202");
        assertEquals(-0.01020502060307829, Stock.calculateCumulativeReturn(start, end), 0.001);
    }
}
