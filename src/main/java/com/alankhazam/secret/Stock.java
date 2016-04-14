package com.alankhazam.secret;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Stock class
 * Created by Alan on 4/13/2016.
 */
class Stock {
    private static final String CR_OUTPUT_DATE_FORMAT = "yyyy/MM/dd";

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

    /**
     * Calculates the cumulative return (close-to-close, adjusted) across two quotes.
     *
     * @param start Start quote
     * @param end   End quote
     * @return Cumulative return (decimal)
     */
    public static double calculateCumulativeReturn(Quote start, Quote end) {
        if (!start.symbol.equals(end.symbol)) {
            throw new IllegalArgumentException("Quote symbols do not match");
        }
        if (start.date.equals(end.date) || start.date.after(end.date)) {
            throw new IllegalArgumentException("Start quote cannot be after end quote");
        }
        if (start.adjustedClose.equals(BigDecimal.valueOf(0))) {
            throw new IllegalArgumentException("Start close cannot be 0");
        }

        // Cumulative return = (current price - original price) / original price
        return (end.adjustedClose.doubleValue() - start.adjustedClose.doubleValue()) /
                start.adjustedClose.doubleValue();
    }

    /**
     * Returns the cumulative return values for all historical quotes against the earliest quote in CSV format.
     */
    public String generateCumulativeReturnsCSV() {
        if (quotes == null || quotes.size() <= 1) {
            return null;
        }

        List<String> points = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(CR_OUTPUT_DATE_FORMAT);
        Quote first = quotes.get(quotes.size() - 1);
        for (int i = 0; i < quotes.size() - 1; i++) {
            Quote quote = quotes.get(i);
            points.add(symbol + ", " + sdf.format(startDate) + ", " + sdf.format(quote.date) + ", " +
                    calculateCumulativeReturn(first, quote));
        }

        return String.join("\n", points);
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
