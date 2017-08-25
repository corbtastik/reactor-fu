package io.corbs.stocks;


import java.util.ArrayList;
import java.util.List;

public class Stocks {

    private final List<Stock> stockList;

    private Stocks(List<Stock> stocks) {
        this.stockList = stocks;
    }

    public static Stocks create(List<Stock> stocks) {
        return new Stocks(stocks);
    }

    public Stock getStock(String ticker) {
        for(Stock stock : stockList) {
            if(stock.getTicker().equalsIgnoreCase(ticker)) {
                return stock;
            }
        }
        return null;
    }

    public List<Stock> getStocks(String...tickers) {
        List<Stock> list = new ArrayList<>();
        for (String ticker : tickers) {
            Stock stock = getStock(ticker);
            list.add(stock);
        }
        return list;
    }


}
