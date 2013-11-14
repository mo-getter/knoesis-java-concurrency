
package org.knoesis.util.concurrent.producerconsumer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

/**
 * A blocking Iterable of consumable items produced by the producer.
 * @author Alan Smith
 */
class Consumables<E> implements Iterable<E> {
    
    private final BlockingQueue<Item<E>> queue;

    Consumables(BlockingQueue<Item<E>> queue) {
        this.queue = queue;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new ConsumableIterator();
    }
    
    private class ConsumableIterator implements Iterator<E> {
        
        Item<E> current = null;

        @Override
        public boolean hasNext() {
            try {
                current = queue.take();
            } catch (InterruptedException ex) {
                return false;
            }
            return current != ProducerConsumer.DONE;
        }

        @Override
        public E next() {
            if (current == ProducerConsumer.DONE) {
                throw new NoSuchElementException();
            }
            return current.get();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }

}
