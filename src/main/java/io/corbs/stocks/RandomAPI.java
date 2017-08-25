package io.corbs.stocks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class RandomAPI {

    @GetMapping("/random/names")
    public Flux<Object> randomNames(@RequestParam(defaultValue="200") Integer delay) {

        Flux<Object> flux = Flux.generate(sink -> {
            sink.next(Randomness.getFirstName() + " " + Randomness.getLastName());
        }).delayElements(Duration.ofMillis(delay)).log("io.corbs");

        return flux;
    }

}
