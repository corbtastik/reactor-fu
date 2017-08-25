package io.corbs.stocks;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactorFuApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void createPublisherExample(){
	    // create publisher
        Flux<String> people = Flux.just("Nacho","Bucky","Lou");

        // subscribe and do something with each value
        people.subscribe(System.out::println);
    }

    @Test
    public void baseSubscriberExample(){
        Flux<String> source = peopleSource(10);
        source.map(String::toUpperCase)
            .subscribe(new BaseSubscriber<String>() {
                // hooks for various signal handling
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    // request is one such signal for propagating back-pressure and triggering flow
                    // here we are requesting 1 element from source
                    System.out.println("Hook on Subscribe");
                    request(1);
                }
                @Override
                protected void hookOnNext(String value) {
                    // upon receiving a new value we continue requesting new items from source
                    // one-by-one
                    System.out.println("\t * Hook on Next: " + value);
                    // TODO comment out and show only one element is sent
                    request(1);
                }
                @Override
                protected void hookOnComplete() {
                    System.out.println("Hook on Complete!");
                }
                @Override
                protected void hookOnError(Throwable error) {
                    System.out.println("Hook on Error: " + error.getMessage());
                }
                @Override
                protected void hookOnCancel(){
                    System.out.println("Hook on Cancel");
                }
                @Override
                protected void hookFinally(SignalType signalType) {
                    System.out.println("Hook on Finally: " + signalType.toString());
                }

            });
    }

    @Test
    public void testSynchronousGenerate() {
        Flux<String> flux = Flux.generate(
                () -> 0, // initial state value of zero
                (state, sink) -> {
                    // use the state to choose what to emit
                    sink.next("3 x " + state + " = " + 3 * state);
                    // also use it to choose when to stop
                    if(state == 10) {
                        sink.complete();
                    }
                    // return new state that will be used in next invocation
                    return state + 1;
                }
        );
        // trigger
        flux.subscribe(System.out::println);
    }

    class HowdyEventProcessor<T>{
        private List<HowdyEventListener<T>> listeners = new ArrayList<>();
        void register(HowdyEventListener<T> listener) {
            this.listeners.add(listener);
        }

        void process() {
            for(HowdyEventListener listener : listeners) {
                List<String> chunk = new ArrayList<>();
                int n = Randomness.getIntegerBetween(1, 13);
                for(int i = 0; i < n; i++) {
                    chunk.add(Randomness.getFirstName());
                    listener.onDataChunk(chunk);
                }
                listener.processComplete();
            }
        }
    }

    interface HowdyEventListener<T> {
        void onDataChunk(List<T> chunk);
        void processComplete();
    }

    /**
     * Flux.create() works in async and sync mode and is suitable for
     * multiple emissions per round but contains no state management
     */
    @Test
    public void fluxCreate() {
        HowdyEventProcessor<String> processor = new HowdyEventProcessor<>();
        Flux<String> flux = Flux.create(sink -> {

            // bridge to the HowdyEventListener API
            // done asynchronously whenever HowdyEventProcessor executes
           processor.register(new HowdyEventListener<String>() {
               @Override
               public void onDataChunk(List<String> chunk) {
                   System.out.println(Thread.currentThread().toString());
                   for(String s : chunk) {
                       // each element in the chunk becomes an element in the Flux
                       sink.next(s);
                   }
               }
               @Override
               public void processComplete() {
                   // processComplete API translated to completing the Flux
                   sink.complete();
               }
           });
        });

        // subscribe to the Flux
        flux.subscribe(System.out::println);
        // trigger
        processor.process();
        System.out.println("After processor...");
    }

    @Test
    public void monoCreate() {

        Mono<Integer> mono = Mono.create(sink -> {
            Integer number = Randomness.getIntegerBetween(1, 100);
            if(number % 2 == 0) {
                // Note mono doesn't have a next method
                // sink.next();
                sink.success(number);
                // what will happen here?
                sink.success(number + 1);
            } else {
                sink.success();
            }
        });

        mono.subscribe(System.out::println);
    }

    /**
     * .handle is an instance method, works like generate in that it uses
     * SynchronousSink and only allows one-by-one emissions.
     *
     * Used to generate an arbitrary value out of each source element,
     * possibly skipping some elements, kinda like a map and filter operation in one
     */
    @Test
    public void handle() {
        Flux<String> flux = Flux.just(-1, 20, 5, 24, 1, 19, 100)
            .handle((i, sink) -> {
                String letter = alphabet(i);
                if(letter != null) {
                    sink.next(letter);
                }
            });

        flux.subscribe(System.out::println);
    }

    /*
     * ===============================================================================================================
     * From "Which operator do I need" section
     * http://projectreactor.io/docs/core/release/reference/#which-operator
     * ===============================================================================================================
     */

    /**
     * Create a new sequence from a Single Value source
     */
    @Test
    public void createNewSequenceFromSingleValueSources() throws IOException {

        Mono<Quote> mono = Mono.fromCallable(new StockQuoteCallable("AAPL"))
            .map(data -> {
                // map JSON data to Quote
                return Quote.builder()
                    .stock(Stock.builder().ticker(data.getString("ticker")).build())
                    .price(BigDecimal.valueOf(Double.valueOf(data.getString("value"))))
                    .instant(Instant.now())
                    .build();
        });

        mono.subscribe(System.out::println);
    }

    @Test
    public void createNewSequenceThatErrors() {
        Mono<Integer> mono = Mono.error(new NumberFormatException());

        mono.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {

            }

            @Override
            public void onNext(Integer integer) {
                Assert.fail("Should never get called..." + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Complete");
            }
        });
    }

    /**
     * Create a new sequence one-by-one with Flux#generate
     */
    @Test
    public void createNewSequenceSyncOneByOne() {
        Flux<Quote> flux = Flux.generate(QuoteContext::new, (context, sink) -> {
                if(context.getIteration() > 20) {
                    sink.complete();
                }

                Quote quote = null;
                try {
                    Double liveValue = Double.valueOf(YahooAPI.getLiveValue("AAPL"));
                    if(context.getQuote() != null) {
                        Double change = liveValue - context.getQuote().getPrice().doubleValue();
                        System.out.println("change: " + change);
                    }
                    quote = Quote.builder()
                        .stock(Stock.builder().ticker("AAPL").company("Apple Inc.").build())
                        .instant(Instant.now())
                        .price(BigDecimal.valueOf(liveValue)).build();
                    sink.next(quote);
                } catch(Exception ex){
                    sink.error(ex);
                }

                context.setIteration(context.getIteration() + 1);
                context.setQuote(quote);

                return context;
            }
        );

        flux.subscribe(System.out::println);
    }

    /*
     * ===============================================================================================================
     * How to handle errors
     * http://projectreactor.io/docs/core/release/reference/#error.handling
     * ===============================================================================================================
     */

    @Test
    public void testOnError() {
        Flux<Integer> flux = Flux.range(1, 10)
            .map(value -> doDivideByZero(value))
            .map(value -> doMultiplyByRandom(value));

        flux.subscribe(
            value -> {
                System.out.println("got it: " + value);
            },
            error -> {
                System.err.println("gulp: " + error);
            }
        );
    }

    Integer doDivideByZero(Integer value) {
        return value / 0;
    }

    Integer doMultiplyByRandom(Integer value) {
        return value * Randomness.getIntegerBetween(1, 1000);
    }



    private String alphabet(int letter) {
        if (letter < 1 || letter > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letter - 1;
        return "" + (char) letterIndexAscii;
    }

    static Flux<String> peopleSource(int size) {
        if(size < 0) {
            size = Randomness.getIntegerBetween(1, 13);
        }
        String[] people = new String[size];
        for(int i = 0; i < people.length; i++) {
            people[i] = Randomness.getFirstName()
                + " " + Randomness.getLastName();
        }
        return Flux.fromArray(people);
    }
}
