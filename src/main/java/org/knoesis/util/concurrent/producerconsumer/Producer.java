
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
