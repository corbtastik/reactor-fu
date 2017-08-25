package io.corbs.stocks;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class YahooAPI {

    static String getLiveValue(String stock) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = client
                .exchange("http://download.finance.yahoo.com/d/quotes.csv?s=" + stock + "&f=a",
                        HttpMethod.GET, requestEntity, String.class);
        return responseEntity.getBody();
    }
}
