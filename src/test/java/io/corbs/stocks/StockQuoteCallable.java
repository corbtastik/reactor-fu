package io.corbs.stocks;

import cworks.json.Json;
import cworks.json.JsonObject;

import java.util.concurrent.Callable;

public class StockQuoteCallable implements Callable<JsonObject> {

    private String ticker;

    StockQuoteCallable(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public JsonObject call() throws Exception {
        String value = YahooAPI.getLiveValue(this.ticker);
        return Json.object()
            .string("ticker", this.ticker)
            .string("value", value)
            .build();
    }

    public String getTicker() {
        return this.ticker;
    }
}
