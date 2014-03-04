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
