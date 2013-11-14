knoesis-java-concurrency
========================

This project aims to provide the simplest possible interfaces for implementing 
a few common patterns of concurrent programs in the Java programming 
language. Although the JDK (1.5+) provides a number of high-level abstractions 
for concurrency ([java.util.concurrent](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/package-summary.html)),
writing concurrent programs using these still requires the user to write 
boilerplate code (e.g. to manage thread creation and lifecycle) and to be aware 
of some special considerations that must be made in order to avoid the common 
pitfalls inherent to concurrent programming.

The libraries in this project allow the user to parallelize certain types of 
programs without the need to become familiar with the java.util.concurrent 
package, and to eliminate as much boilerplate code as possible. Currently, 
there is support for two types of algorithms:

1. Parallel For-Each
2. Producer-Consumer

Parallel For-Each
-----------------
_Package: org.knoesis.utils.concurrent.parallel_

Parallel For-Each is used to process a collection of objects in parallel. The 
same operation is performed once for each item in the collection.

Here's a simple code example, which prints each element in a list of numbers to 
standard out (using one or more background threads). The main thread (the one 
from which Parallel.forEach() is called) will block until all the items have 
been processed:

    List<Integer> myNumbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    Parallel.forEach(myNumbers, new Operation<Integer>() {
        public void perform(Integer i) {
            System.out.println(i);
        }
    });

Note that in this example, the elements in the list may not necessarily be 
printed in their original order, and the order may be different across 
executions of the same program. This is the non-deterministic nature of 
concurrent programming.

Producer-Consumer
-----------------
_Package: org.knoesis.utils.concurrent.producerconsumer_

The [Producer-Consumer Problem](http://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem) 
is one which arises often in real-world programs. The main advantage of 
solutions to this problem compared with serial code is that consumer tasks can 
begin processing documents in parallel before producer tasks have finished 
producing the full collection of work objects (which also implies a smaller 
memory footprint). With a little bit of thought, many algorithms can be 
parallelized, to some degree, by modeling each of the independent tasks as 
producer-consumer problems (or a series of such), for example:

 - A search engine indexer, where the producers produce documents to be 
    indexed by reading them from the web or from disk as fast as they can, 
    and the consumers analyze the terms of each produced document and store 
    the indexed form to disk.
 - A web server, in which a producer listens on a socket and produces 
    requests, and a number of consumers make up the pool of worker threads 
    responsible for processing the requests.

The following [contrived] example illustrates basic usage, generating 1000 
random numbers between 1 and 1000 and printing each one:

    public class RandomIntegerGenerator implements Producer<Integer> {
        public void produce(Production<Integer> production) {
            for (int i=0; i<1000; i++) {
                try {
                    production.put((int) Math.floor(Math.random() * 1000) + 1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProducerConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public class ObjectPrinter implements Consumer<Object> {
        public void consume(Iterable<Object> objects) {
            for (Object o : objects) {
                System.out.println(o);
            }
        }
    }

    ProducerConsumer<Number> pc = ProducerConsumer.<Number>newBuilder()
            .addProducer(new RandomIntegerGenerator())
            .addConsumer(new ObjectPrinter())
            .addConsumer(new ObjectPrinter())
            .build();
    pc.begin(); // blocks until all objects are produced and consumed, or an InterruptedException occurs

As you can see, we have created a `ProducerConsumer<Number>`, but we added a 
`Producer<Integer>` and a `Consumer<Object>`. This is acceptable, as we can add 
producers which produce `Number`s or any subclass, and consumers which consume 
`Number`s or any superclass. This helps promote flexibility as well as code 
reuse, as the consumer class can be used to print any `Object`, not just a
`Number`.

If your classes (or at least your `produce()`/`consume()` methods) are thread 
safe, you can safely add the same instance multiple times:

    RandomIntegerGenerator producer = new RandomIntegerGenerator();
    ObjectPrinter consumer = new ObjectPrinter();
    ProducerConsumer.Builder<Number> builder = ProducerConsumer.<Number>newBuilder();
    for (int i=0; i<NUMBER_OF_PRODUCERS; i++) {
        builder.addProducer(producer);
    }
    for (int j=0; j<NUMBER_OF_CONSUMERS; j++) {
        builder.addConsumer(consumer);
    }
    builder.build().begin();

Note: [System.out.println](http://docs.oracle.com/javase/7/docs/api/java/io/PrintStream.html#println(java.lang.Object\)) 
makes no thread-safety guarantees, so in this example, the output lines may be 
interleaved.