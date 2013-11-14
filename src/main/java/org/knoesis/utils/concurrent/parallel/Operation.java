
package org.knoesis.utils.concurrent.parallel;

/**
 * Defines a generic operation to perform on an item.
 * @author Alan Smith
 */
public interface Operation<T> {
    
    /**
     * Performs an operation upon a given item. Note: {@code RuntimeException}s thrown from this method will be ignored.
     * @param item the item upon which to perform the operation
     */
    void perform(T item);

}
