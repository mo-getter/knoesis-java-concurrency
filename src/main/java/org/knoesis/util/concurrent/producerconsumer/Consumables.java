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
