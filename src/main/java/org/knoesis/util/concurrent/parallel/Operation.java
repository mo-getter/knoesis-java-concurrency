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

package org.knoesis.util.concurrent.parallel;

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
