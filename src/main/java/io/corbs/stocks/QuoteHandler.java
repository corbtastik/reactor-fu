package io.corbs.stocks;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class QuoteHandler {

    private final Flux<Quote> quoteStream;

    public QuoteHandler(QuoteStream stream) {
        this.quoteStream = stream.quoteStream(Duration.ofMillis(100)).share();
    }

    public Mono<ServerResponse> howdy(ServerRequest request) {
        return ok().contentType(TEXT_PLAIN).body(BodyInserters.fromObject("Howdy Webflux!"));
    }

    public Mono<ServerResponse> echo(ServerRequest request) {
        return ok().contentType(TEXT_PLAIN)
                .body(request.bodyToMono(String.class), String.class);
    }

    public Mono<ServerResponse> fetchQuotes(ServerRequest request) {
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        return ok().contentType(APPLICATION_JSON)
            .body(this.quoteStream.take(size), Quote.class);
    }

    public Mono<ServerResponse> streamQuotes(ServerRequest request) {
        return ok().contentType(APPLICATION_STREAM_JSON)
            .body(this.quoteStream, Quote.class);
    }
}
