package io.corbs.stocks;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quote {
    private Stock stock;
    private Instant instant;
    private BigDecimal price;
}
