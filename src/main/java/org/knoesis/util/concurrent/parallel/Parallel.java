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

package org.knoesis.util.concurrent.parallel;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.knoesis.util.concurrent.producerconsumer.Consumer;
import org.knoesis.util.concurrent.producerconsumer.Producer;
import org.knoesis.util.concurrent.producerconsumer.ProducerConsumer;
import org.knoesis.util.concurrent.producerconsumer.Production;

/**
 * General purpose utilities for writing concurrent programs.
 * @author Alan Smith
 */
public class Parallel {
    
    private static final int ALL_CORES = Runtime.getRuntime().availableProcessors();
    
    /**
     * Concurrently runs the given {@link Operation} on each item in the {@link Collection}.
     * 
     * @param <E> The type of element in the {@link Collection} to process
     * @param elements The {@link Collection} of elements to process concurrently
     * @param operation The {@link Operation} to perform on each item in the {@link Collection}
     * @param numThreads The number of threads to create
     * @throws InterruptedException if the main thread is interrupted while waiting for the collection to finish processing
     */
    public static <E> void forEach(Collection<? extends E> elements, final Operation<? super E> operation, int numThreads) throws InterruptedException {
        int size = elements.size();
        if (size == 0) {
            return;
        }
        final ProducerConsumer.Builder<E> pcb = ProducerConsumer.<E>newBuilder()
                .addProducer(new CollectionProducer<E>(elements));
        for (int i = 0; i < Math.min(numThreads, size) - 1; i++) {
            pcb.addConsumer(new OperationConsumer<E>(operation));
        }
        pcb.build().begin();
    }
    
    /**
     * Concurrently runs the given {@link Operation} on each item in the {@link Collection} with the number of threads
     * determined by {@code Runtime.getRuntime().availableProcessors()}.
     * 
     * @param <E> The type of element in the {@link Collection} to process
     * @param elements The {@link Collection} of elements to process concurrently
     * @param operation The {@link Operation} to perform on each item in the {@link Collection}
     * @throws InterruptedException if the main thread is interrupted while waiting for the collection to finish processing
     */
    public static <E> void forEach(Collection<? extends E> elements, final Operation<? super E> operation) throws InterruptedException {
        forEach(elements, operation, ALL_CORES);
    }
    
    private Parallel() {}
    
    private static class CollectionProducer<E> implements Producer<E> {

        private final Iterable<E> elements;
        
        public CollectionProducer(Iterable<? extends E> elements) {
            this.elements = (Iterable<E>) elements;
        }
        
        @Override
        public void produce(Production<E> production) {
            try {
                for (E element : elements) {
                    production.put(element);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Parallel.class.getName()).log(Level.SEVERE, "Producer interrupted", ex);
            }
        }
    }
    
    private static class OperationConsumer<E> implements Consumer<E> {

        private final Operation<? super E> operation;

        public OperationConsumer(Operation<? super E> operation) {
            this.operation = operation;
        }
        
        @Override
        public void consume(Iterable<E> elements) {
            for (E element : elements) {
                operation.perform(element);
            }
        }
        
    }

}
