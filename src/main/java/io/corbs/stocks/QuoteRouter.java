package io.corbs.stocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class QuoteRouter {

    @Autowired
    private QuoteHandler handler;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(GET("/howdy").and(accept(TEXT_PLAIN)), handler::howdy)
            .andRoute(POST("/echo").and(accept(TEXT_PLAIN).and(contentType(TEXT_PLAIN))), handler::echo)
            .andRoute(GET("/quotes").and(accept(APPLICATION_JSON)), handler::fetchQuotes)
            .andRoute(GET("/quotes").and(accept(APPLICATION_STREAM_JSON)), handler::streamQuotes);
    }
}
