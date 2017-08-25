package io.corbs.stocks;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuoteContext {
    int iteration = 0;
    boolean stop = false;
    Quote quote;
}
