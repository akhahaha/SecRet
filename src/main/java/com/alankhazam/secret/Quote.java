package com.alankhazam.secret;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Stock quote class
 * Created by Alan on 4/13/2016.
 */
class Quote {
    private static final String URL_BASE = "http://query.yahooapis.com/v1/public/yql";
    private static final String QUERY_DATE_FORMAT = "yyyy-MM-dd";

    String symbol;
    Date date;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    Long volume;
    BigDecimal adjustedClose;

    Quote(String symbol, String dateString) throws ParseException, IOException {
        this(symbol, new SimpleDateFormat("yyyyMMdd").parse(dateString)); // TODO Implement flexible date formats
    }

    Quote(String symbol, Date date) throws IOException {
        this.symbol = symbol;
        this.date = date;

        // Verify the date is in the past
        Date current = new Date();
        if (date.equals(current) || date.after(current)) {
            throw new IllegalArgumentException("Quote date must be in the past");
        }

        // Calculate start and end date for a single day
        Calendar from = Calendar.getInstance();
        from.setTime(date);
        Calendar to = Calendar.getInstance();
        to.setTime(date);
        to.add(Calendar.DATE, 1);

        // Pull stock information from Yahoo! Finances
        List<HistoricalQuote> quotes = YahooFinance.get(symbol, from, to, Interval.DAILY).getHistory();
        if (quotes == null || quotes.isEmpty()) {
            throw new NoSuchElementException("No historical data found for " + symbol +
                    "(" + from + ":" + to + ")");
        }

        initializeQuote(quotes.get(quotes.size() - 1)); // Use the last (earliest) entry TODO Verify date
    }

    Quote(HistoricalQuote quote) {
        initializeQuote(quote);
    }

    /**
     * Populate the quote from a HistoricalQuote object.
     */
    private void initializeQuote(HistoricalQuote quote) {
        this.symbol = quote.getSymbol();
        Calendar c = quote.getDate();
        c.add(Calendar.HOUR, 3); // Add 3 hours to compensate for 9pm HistoricalQuote time
        this.date = c.getTime();
        this.open = quote.getOpen();
        this.high = quote.getHigh();
        this.low = quote.getLow();
        this.close = quote.getClose();
        this.volume = quote.getVolume();
        this.adjustedClose = quote.getAdjClose();
    }

    @Override
    public String toString() {
        return "Quote{" +
                "symbol='" + symbol + '\'' +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", adjustedClose=" + adjustedClose +
                '}';
    }
}
