package io.corbs.stocks;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuoteStreamTest {

    @Test
    public void testQuoteStream() throws IOException {

        List<Stock> stocks = IO.readJSONStocks("src/test/resources/stocks.json");
        QuoteStream stream = new QuoteStream(stocks);
        Flux<Quote> quotes = stream.quoteStream(Duration.ofSeconds(5));

        quotes.subscribe(System.out::println);
        System.out.println("DONE");
    }
}
