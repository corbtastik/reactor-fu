package io.corbs.stocks;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    String ticker;
    String company;
    String sector;
    String industry;
    String city;
    String state;
    String dateAdded;
    Integer cik;
}
