
package org.knoesis.util.concurrent.producerconsumer;

import java.util.concurrent.BlockingQueue;

/**
 * A write-only view of the underlying work queue.
 * @author Alan Smith
 */
public class Production<E> {
    
    private final BlockingQueue<Item<E>> queue;
    
    Production(BlockingQueue<Item<E>> queue) {
        this.queue = queue;
    }
    
    /**
     * Places an element into the work queue for consumption by 
     * {@link Consumer}s.
     * @param element the element to produce
     * @throws InterruptedException if the calling thread is interrupted while 
     * waiting for an empty slot in the queue.
     */
    public void put(E element) throws InterruptedException {
        queue.put(new Item<E>(element));
    }

}
