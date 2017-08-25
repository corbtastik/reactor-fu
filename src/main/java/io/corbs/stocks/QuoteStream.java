package io.corbs.stocks;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

@Component
public class QuoteStream {

    private final MathContext mathContext = new MathContext(2);

    private final Random random = new Random();

    private final List<Stock> stocks;

    public QuoteStream(List<Stock> stocks){
        this.stocks = stocks;
    }

    public Flux<Quote> quoteStream(Duration duration) {
        return Flux.generate(
                () -> 0, // initial state value of zero
                (BiFunction<Integer, SynchronousSink<Quote>, Integer>) (index, sink) -> {
                    // use the state to choose what to emit
                    Quote quote = updateQuote(Quote.builder().stock(stocks.get(index)).build());
                    sink.next(quote);
                    return ++index % stocks.size();

                // emit using a specific period so we Zip the flux with an interval
                }).zipWith(Flux.interval(duration)).map(Tuple2::getT1)
                // values are generated in batches so we need to set timestamp
                // after creation so they're unique per quote
                .map( quote -> {
                    quote.setInstant(Instant.now());
                    return quote;
                }).log("io.corbs.stocks");
    }

    /**
     * Update quote with new price info
     * @param quote
     * @return
     */
    private Quote updateQuote(Quote quote) {
        // set initial price value if needed
        initPrice(quote);

        BigDecimal priceChange = quote.getPrice().multiply(
            new BigDecimal(0.05 * this.random.nextDouble()), this.mathContext);
        Quote updatedQuote = Quote.builder().stock(quote.getStock()).build();
        updatedQuote.setPrice(quote.getPrice().add(priceChange));
        updatedQuote.setInstant(Instant.now());
        return updatedQuote;
    }

    private void initPrice(Quote quote) {
        if(quote.getPrice() == null) {
            Double price = null;
            try {
                price = Double.valueOf(YahooAPI.getLiveValue(quote.getStock().getTicker()));
            } catch (Exception e) {
                // if we can't getLiveValue real data from Yahoo
                // then we make shit up
                price = (double)Randomness.getIntegerBetween(1, 100);
                System.out.println("Using random value for price: "
                    + quote.getStock().getTicker() + " " + price);
            }
            quote.setPrice(BigDecimal.valueOf(price));
        }
    }
}
