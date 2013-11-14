
package org.knoesis.utils.concurrent.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        List<Integer> input = new ArrayList<Integer>(NUM_ELEMENTS);
        for (int i=0; i<NUM_ELEMENTS; i++) {
            input.add(i);
        }
        TestOperation operation = new TestOperation();
        Parallel.forEach(input, operation);
        assertTrue(operation.getResult().containsAll(input));
    }
    
    public static class TestOperation implements Operation<Integer> {
        
        private final List<Integer> result = new CopyOnWriteArrayList<Integer>();

        public void perform(Integer x) {
            result.add(x);
        }

        public void handleException(Throwable ex, Integer x) {
            Logger.getLogger(TestOperation.class.getName()).log(Level.SEVERE, String.format("Exception on element %s", x), ex);
        }
        
        Collection<Integer> getResult() {
            return result;
        }
        
    }

}
