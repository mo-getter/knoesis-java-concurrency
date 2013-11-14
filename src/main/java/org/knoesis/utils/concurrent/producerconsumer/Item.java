
package org.knoesis.utils.concurrent.producerconsumer;

/**
 *
 * @author Alan Smith
 */
final class Item<E> {
    
    private final E item;
    
    public Item(E item) {
        this.item = item;
    }
    
    public E get() {
        return item;
    }

}
