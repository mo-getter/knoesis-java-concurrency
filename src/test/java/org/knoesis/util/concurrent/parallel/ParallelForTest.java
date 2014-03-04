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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author alan
 */
public class ParallelForTest extends TestCase {
    
    private static final int NUM_ELEMENTS = 10000;

    public ParallelForTest() {
    }

    public ParallelForTest(String name) {
        super(name);
    }
    
    public static Test suite() {
        return new TestSuite(ParallelForTest.class);
    }
    
    public void testParallelFor() throws InterruptedException {
        Set<Integer> input = new HashSet<Integer>(NUM_ELEMENTS);
        for (int i=0; i<NUM_ELEMENTS; i++) {
            input.add(i);
        }
        TestOperation operation = new TestOperation();
        long start = System.currentTimeMillis();
        Parallel.forEach(input, operation);
        System.out.format("Parallel.forEach took %dms\n", System.currentTimeMillis() - start);
        assertTrue(operation.getResult().containsAll(input));
    }
    
    public static class TestOperation implements Operation<Integer> {
        
        private final Set<Integer> result = new ConcurrentSkipListSet<Integer>();

        @Override
        public void perform(Integer x) {
            result.add(x);
        }
        
        public Collection<Integer> getResult() {
            return result;
        }
        
    }

}
