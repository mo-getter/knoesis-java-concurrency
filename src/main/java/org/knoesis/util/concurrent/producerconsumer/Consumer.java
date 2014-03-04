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
 * This interface represents a consumer of objects produced.<br/><br/>Typical implementation:<br/><br/>
 * <pre>
 * {@code
 * public class MyConsumer implements Consumer<String> {
 *      public void consume(Iterable<String> consumables) {
 *          for (String foo : consumables) {
 *              doSomethingWith(foo);
 *          }
 *      }
 *      ...
 * }
 * }
 * </pre>
 * @author Alan Smith
 */
public interface Consumer<E> {

    /**
     * This method should consume objects by iterating the provided 
     * {@link Iterable} instance.
     * @param consumables A blocking {@code Iterable} view of the work queue.
     */
    void consume(Iterable<E> consumables);
    
}
