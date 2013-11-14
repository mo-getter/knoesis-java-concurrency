
package org.knoesis.utils.concurrent.parallel;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sun.org.mozilla.javascript.Callable;

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
        final ExecutorService executor = Executors.newFixedThreadPool(Math.min(numThreads, size));
        final CountDownLatch latch = new CountDownLatch(size);
        for (final E e : elements) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        operation.perform(e);
                    } catch (RuntimeException ignored) {
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        executor.shutdown();
        latch.await();
        executor.shutdownNow();
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

}
