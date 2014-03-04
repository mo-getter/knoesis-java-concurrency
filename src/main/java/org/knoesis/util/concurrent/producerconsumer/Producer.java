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

/**
 * This interface specifies a producer of objects to be consumed by the {@link Consumer}s.
 * @author Alan Smith
 */
public interface Producer<E> {
    
    /**
     * This method should produce objects into the provided {@link Production}.
     * @param production A {@code Producer}'s view of the work queue, which may 
     * only be added to.
     */
    void produce(Production<E> production);
    
}
