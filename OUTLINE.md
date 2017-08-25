
## Introduction to Reactor

    1. Non-blocking reactive programming foundation for the JVM
    1. Includes demand management via Backpressure
    1. Builds on CompletableFuture, Stream and Duration
    1. Composable asynchronous sequence APIs Flux and Mono
    1. Non-blocking IPC for microservice architectures
    1. IPC implementations for HTTP, Websockets, TCP and UDP

## Reactive programming

    1. Built around dataflows and propagation of change
    1. Execution model propagates change via dataflow
    1. Reactive aspects achieved in OO as an extension of the Observer pattern
    1. Reactive Streams are push based
    1. Publisher-Subscriber is default concept
    1. Publisher notifies Subscriber of new values as they come
    1. The push aspect is key to being reactive
    1. Operations applied to pushed values are expressed declaratively
    1. Error handling and completion aspects are well defined
    1. Publisher can push new values to Subscriber but also signal an error or completion to terminate sequence

## Why do we need Reactive programming?

    1. Large scale use presents challenges for modern hardware
    1. Performance can be improved in one of two ways
      1. Parallelize
      1. Use current resources more effectively
    1. Typical java developers write blocking code
    1. More threads run similar blocking code
    1. Waiting on I/O is a waste of time and resources
    1. So parallelization is not a silver bullet

## Is asynchronous programming really the answer?

    1. asynchronous Non-blocking code allows execution to switch to another task using the same underlying resources
    1. Coming back to the current train of thought is a key aspect
    1. How to accomplish on the JVM?
      1. Callbacks: asynchronous methods don't return a value.  Think EventListener.
      1. Futures: asynchronous methods return a Future<T> immediately.  The Future wraps access to T.
    1. Callbacks are hard to compose
    1. Futures are still not so good at composition
      1. Orchestrating multiple futures together is doable but not easy
      1. Future.get() is familiar blocking territory

## Imperative to Reactive

    1. Reactor tries to address the Composable problem
    1. Data as flow
    1. Data is manipulated using operators
    1. Nothing happens until you Subscribe
    1. Subscriber can signal to Producer that flow rate is too high

## Composability and readability

    1. Ability to orchestrate multiple asynchronous tasks together
    1. Allows using results from previous tasks to feed input to subsequent ones.  
    1. Allows for executing several tasks in a fork-join style
    1. Allows for reusing asynchronous tasks as components
    1. Single callback model is simple
      1. But a Callback executed from a callback, itself inside another callback is hell and hard to reason with.
    1. Reactor offers rich composition options and everything is kept at same level.

## Assembly line analogy

    1. Think of data processed by a reactive application as moving through an assembly line.
    1. Reactor is the conveyor belt and work stations
    1. The raw material pours from a source (Publisher)
    1. Ends up a finished products ready to be shipped to consumer (Subscriber)
    1. If theres a glitch or a clogging the work station can signal upstream and limit the flow of raw material

## Operators

    1. Operators are the assembly line workstations
    1. Each operator adds behavior to a Publisher
    1. Each operator wraps the previous step's Publisher into a new instance
    1. The whole chain is layered like an onion
    1. Data originates in the center and moves outward being transformed by each layer

## Nothing happens until you Subscribe

    1. A Publisher chain doesn't start pumping data by default
    1. Subscription ties together the Publisher and Subscriber which triggers the flow of data into the chain
    1. Subscriber sends a single request signal upstream to source Publisher

## Backpressure

    1. Same single request signal is used to implement backpressure. A feedback signal sent up the line that a work station is slower to process than upstream.
    1. Model is called push-pull
      1. Subscriber can pull N elements from Producer if they're available but if not they will get pushed by Publisher when ready.

## Hot vs. Cold

    1. Two categories of Reactive sequences: hot and cold
    1. Cold sequence will start new for each Subscriber
    1. Hot sequences will not start for scratch for each Subscriber

## Reactive Core Features

    1. reator-core is main artifact of library
    1. Composable reactive types that implement Publisher
    1. Operators Flux and Mono
    1. Flux represents 0..N items
    1. Mono represents 1 item or empty

## Flux asynchronous sequence of 0..N items

    1. A Flux<T> is a standard Publisher<T> representing an asynchronous sequence of 0 to N emitted items
    1. Flux is a general purpose Reactive type
    1. All events are optional
    1. No onNext event with an onComplete event represents and empty finite sequence
    1. No onNext event and no onComplete event represents an infinite empty sequence

## Mono an asynchronous 0-1 result

    1. A Mono<T> is a specialized Publisher<T> that emits at most one item then optionally terminates with onComplete or onError
    1. Mono can be used to represent no-value asynchronous processes that only have concept of completion. Like a java Runnable.  Mono<Void> ~ Runnable
