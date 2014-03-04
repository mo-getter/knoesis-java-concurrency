/**
 * Copyright (C) 2014 Kno.e.sis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.knoesis.util.concurrent.producerconsumer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An API to help simplify implementations of the <a href="http://en.wikipedia.org/wiki/Producer_consumer_problem">Producer-Consumer Problem</a>.
 * This class supports an arbitrary number of {@link Producer}s and {@link Consumer}s, and runs each on its own separate thread.
 * @author Alan Smith
 */
public class ProducerConsumer<E> {
    
    private final BlockingQueue<Item<E>> buffer;
    private final Production<E> production;
    private final Iterable<E> consumables;
    private final Collection<Producer<? extends E>> producers;
    private final Collection<Consumer<? super E>> consumers;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    static final Item DONE = new Item(null);
    
    ProducerConsumer(Collection<Producer<? extends E>> producers, Collection<Consumer<? super E>> consumers, BlockingQueue<Item<E>> buffer) {
        this.buffer = buffer;
        this.production = new Production(buffer);
        this.consumables = new Consumables(buffer);
        this.producers = producers;
        this.consumers = consumers;
    }
    
    /**
     * Begins processing. This method will block until all {@link Producer}s and {@link Consumer}s have finished.
     * @throws InterruptedException if the calling thread is interrupted.
     */
    public void begin() throws InterruptedException {
        final CountDownLatch producerLatch = new CountDownLatch(producers.size());
        final CountDownLatch consumerLatch = new CountDownLatch(consumers.size());
        for (final Consumer consumer : consumers) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        consumer.consume(consumables);
                    } catch(Exception ex) {
                        Logger.getLogger(ProducerConsumer.class.getName()).log(Level.SEVERE, "Consumer threw exception", ex);
                    } finally {
                        consumerLatch.countDown();
                    }
                }
            });
        }
        for (final Producer producer : producers) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        producer.produce(production);
                    } catch (RuntimeException ex) {
                        Logger.getLogger(ProducerConsumer.class.getName()).log(Level.SEVERE, "Producer threw exception", ex);
                    } finally {
                        producerLatch.countDown();
                    }
                }
            });
        }
        producerLatch.await();
        for (int i=0; i<consumers.size(); i++) {
            buffer.put(DONE);
        }
        consumerLatch.await();
        executor.shutdownNow();
    }
    
    /**
     * Returns a new {@code ProducerConsumer.Builder} instance.<br/><br/>Example usage:<br/><br/>
     * <pre>
     * {@code 
     *  ProducerConsumer<String> pc = ProducerConsumer.<String>newBuilder()
     *      .addProducer(new MyProducer(1))
     *      .addProducer(new MyProducer(2))
     *      .addConsumer(new MyConsumer())
     *      .addConsumer(new MyConsumer())
     *      .build();
     * }
     * </pre>
     * @return a new {@code ProducerConsumer.Builder} instance
     */
    public static <E> Builder<E> newBuilder() {
        return new Builder<E>();
    }
    
    /**
     * @param E the type of object to produce and consume
     */
    public static final class Builder<E> {
        
        private final Collection<Producer<? extends E>> producers = new LinkedList<Producer<? extends E>>();
        private final Collection<Consumer<? super E>> consumers = new LinkedList<Consumer<? super E>>();
        
        private int bufferSize = Integer.MAX_VALUE;
        
        /**
         * Adds a producer to this instance
         * @param producer producer to add
         * @return this
         */
        public Builder<E> addProducer(Producer<? extends E> producer) {
            producers.add(producer);
            return this;
        }
        
        /**
         * Adds a consumer to this instance
         * @param consumer consumer to add
         * @return this
         */
        public Builder<E> addConsumer(Consumer<? super E> consumer) {
            consumers.add(consumer);
            return this;
        }
        
        /**
         * Sets the maximum buffer size of the bounded-buffer (work queue). Defaults to {@link Integer#MAX_VALUE}.
         * @param bufferSize
         * @return this
         */
        public Builder<E> setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }
        
        /**
         * @return a new instance of {@code ProducerConsumer} configured by this {@code Builder}.
         */
        public ProducerConsumer<E> build() {
            return new ProducerConsumer<E>(producers, consumers, new LinkedBlockingQueue<Item<E>>(bufferSize));
        }
        
    }

}
