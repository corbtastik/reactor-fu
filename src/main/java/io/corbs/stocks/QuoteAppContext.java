package io.corbs.stocks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class QuoteAppContext {

    @Value("${stocks.file}")
    private String stocksFile;

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
        List<Stock> stocks = IO.readJSONStocks(new File(stocksFile));
        return new QuoteStream(stocks);
    }

    @Bean
    public Stocks stocks() throws IOException {
        return Stocks.create(IO.readJSONStocks(new File(stocksFile)));
    }

}
