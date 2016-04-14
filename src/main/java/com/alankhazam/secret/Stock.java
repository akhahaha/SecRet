package com.alankhazam.secret;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Stock class
 * Created by Alan on 4/13/2016.
 */
class Stock {
    String symbol;
    Date startDate;
    Date endDate;
    List<Quote> quotes;

    Stock(String symbol, String startDate, String endDate) throws ParseException, IOException {
        this(symbol, new SimpleDateFormat("yyyyMMdd").parse(startDate),
                new SimpleDateFormat("yyyyMMdd").parse(endDate)); // TODO Implement flexible date formats
    }

    Stock(String symbol, Date startDate, Date endDate) throws IOException {
        this.symbol = symbol;
        this.startDate = startDate;
        this.endDate = endDate;

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Stock start date cannot be after end date");
        }
        // TODO Verify end date is not in the future

        // Get stock quotes
        this.quotes = new ArrayList<>();

        // Calculate start and end date for a single day
        Calendar from = Calendar.getInstance();
        from.setTime(startDate);
        Calendar to = Calendar.getInstance();
        to.setTime(endDate);
        to.add(Calendar.DATE, 1);

        List<HistoricalQuote> results = YahooFinance.get(symbol, from, to, Interval.DAILY).getHistory();
        if (results == null || results.isEmpty()) {
            throw new NoSuchElementException("Historical data not found for " + symbol);
        }
        if (results.get(0).getDate().getTime().after(endDate)) {
            results.remove(0); // Remove extra entry (newest) if necessary
        }

        for (HistoricalQuote quote : results) {
            quotes.add(new Quote(quote));
        }
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", quotes=" + quotes +
                '}';
    }
}
