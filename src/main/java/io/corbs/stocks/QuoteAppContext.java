package io.corbs.stocks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class QuoteAppContext {

    private String stocksFile = "/stocks.json";

    @Bean
    public QuoteRouter quoteRouter() {
        return new QuoteRouter();
    }

    @Bean
    public QuoteHandler quoteHandler() throws IOException {
        return new QuoteHandler(quoteStream());
    }

    @Bean
    public QuoteStream quoteStream() throws IOException {
        File file = new File(getClass().getResource(stocksFile).getFile());
        List<Stock> stocks = IO.readJSONStocks(file);
        return new QuoteStream(stocks);
    }

    @Bean
    public Stocks stocks() throws IOException {
        File file = new File(getClass().getResource(stocksFile).getFile());
        return Stocks.create(IO.readJSONStocks(file));
    }

}
