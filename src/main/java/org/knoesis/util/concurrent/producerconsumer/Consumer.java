
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
