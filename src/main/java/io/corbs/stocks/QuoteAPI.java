package io.corbs.stocks;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

@RestController
public class QuoteAPI {

    private final Random random = new Random();

    @Autowired
    private Stocks stocks;

    private static final int MAX_ENTRIES = 100;
    private Map<String, Quote> quotes = new LinkedHashMap<String, Quote>(MAX_ENTRIES + 1, .75F, true) {
        // This method is called just after a new entry has been added
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    /**
     * TODO complete this example
     * @param ticker
     * @return
     */
    @GetMapping("/quote/{ticker}")
    public Flux<Quote> getStockQuote(final @PathVariable String ticker, @RequestParam(defaultValue = "200") Integer duration) {

        Flux<Quote> flux = Flux.generate(
            () -> 0, // initial state value of zero
            (BiFunction<Integer, SynchronousSink<Quote>, Integer>) (index, sink) -> {
                // use the state to choose what to emit
                Quote quote = updatedQuote(ticker);
                sink.next(quote);
                // return new state that will be used in next invocation
                return index + 1;
            }
        ).zipWith(Flux.interval(Duration.ofMillis(duration))).map(Tuple2::getT1)
                // values are generated in batches so we need to set timestamp
                // after creation so they're unique per quote
                .map(quote -> {
                    quote.setInstant(Instant.now());
                    return quote;
                });

        return flux;
    }

    private Quote updatedQuote(String ticker) {
        Quote quote;

        if(!quotes.containsKey(ticker)) {
            // if Quote isn't in cache then render initial Quote and put into cache
            String value = YahooAPI.getLiveValue(ticker);
            quote = Quote.builder().price(BigDecimal.valueOf(Double.valueOf(value)))
                    .instant(Instant.now())
                    .stock(stocks.getStock(ticker))
                    .build();
            quotes.put(ticker, quote);
            return quote;
        }

        // Quote is in cache so use
        quote = quotes.get(ticker);

        Double change = this.random.nextDouble();
        if(!this.random.nextBoolean()) {
            change = change * -1;
        }

        Quote updatedQuote = Quote.builder().stock(quote.getStock()).build();
        updatedQuote.setPrice(quote.getPrice().add(BigDecimal.valueOf(change)));
        updatedQuote.setInstant(Instant.now());
        // update cache
        quotes.put(ticker, updatedQuote);

        return updatedQuote;
    }
}
