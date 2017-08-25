package io.corbs.stocks;

import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreateNewSequenceTests {

    private static final String TICKER_FILE = "src/test/resources/tickers.csv";

    /**
     * Create a new sequence that emits a T I already have...
     *
     * Use: Mono#just
     */
    @Test
    public void emitObjectIHave() {
        Stock stock = Stock.builder()
                .ticker("AAPL")
                .company("Apple Inc.")
                .city("Cupertino")
                .state("California")
                .industry("Technology Hardware Storage & Peripherals")
                .sector("Information Technology")
                .cik(320193)
                .dateAdded("1982-11-30").build();

        // from a T
        Mono<Stock> mono = Mono.just(stock);
        mono.subscribe(System.out::println);

        // from an Optional<T>
        Optional<Stock> stockOptional = Optional.of(stock);

        mono = Mono.justOrEmpty(stockOptional).map(s -> {
            s.setTicker(s.getTicker().toLowerCase());
            return s;
        });
        mono.subscribe(stock1 -> Assert.assertEquals("aapl", stock1.getTicker()));
    }

    /**
     * Create a new sequence that emits a T but lazily captures it by wrapping just with defer.
     * Could also use Mono#fromSupplier
     */
    @Test
    public void emitObjectReturnedFromMethod() {
        Mono<Stock> stock = Mono.defer(() -> {
            Stock s = null;
            try {
                s = IO.randomStock("src/test/resources/tickers.csv");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return Mono.justOrEmpty(s);
        }).map(s -> { // make ticker lowercase
            s.setTicker(s.getTicker().toLowerCase());
            return s;
        });

        stock.subscribe(s -> {
            String ticker = s.getTicker();
            failIfNotLowercase(ticker);
        });
    }

    /**
     * Create a new sequence from a array of T(s)
     * @throws IOException
     */
    @Test
    public void emitObjectsIHave() throws IOException {

        Flux<Stock> stocks = Flux.just(
                IO.randomStock(TICKER_FILE),
                IO.randomStock(TICKER_FILE),
                IO.randomStock(TICKER_FILE))
                .map( s -> {
                    s.setTicker(s.getTicker().toLowerCase());
                    return s;
                });

        stocks.subscribe(stock -> failIfNotLowercase(stock.getTicker()));
    }

    /**
     * Create a new sequence from an Iterable
     * @throws IOException
     */
    @Test
    public void createNewSequenceFromIterable() throws IOException {

        List<Stock> stocks = Arrays.asList(
                IO.randomStock(TICKER_FILE),
                IO.randomStock(TICKER_FILE),
                IO.randomStock(TICKER_FILE));

        Flux<Stock> f1 = Flux.fromIterable(stocks);
        f1.subscribe(System.out::println);

        Stock[] stockArray = (Stock[])stocks.toArray();
        Flux<Stock> f2 = Flux.fromArray(stockArray);
        f2.subscribe(System.out::println);

        Flux<Integer> f3 = Flux.range(0, 1000).map( i -> i * 2);
        f3.subscribe(System.out::println);
    }

    private static void failIfNotLowercase(String text) {
        for(char c : text.toCharArray()) {
            if(Character.isLetter(c) && Character.isUpperCase(c)) {
                Assert.fail("Text characters should be all lowercase: " + text);
            }
        }
    }
}
