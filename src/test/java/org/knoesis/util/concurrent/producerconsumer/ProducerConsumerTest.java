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

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ProducerConsumerTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProducerConsumerTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ProducerConsumerTest.class );
    }

    /**
     * Asserts that all produced elements are consumed
     */
    public void testApp() throws InterruptedException
    {
        Set<String> stash = new ConcurrentSkipListSet<String>();
        ProducerConsumer<String> pc = ProducerConsumer.<String>newBuilder()
                .addProducer(new TestProducer(stash))
                .addConsumer(new TestConsumer(stash))
                .addConsumer(new TestConsumer(stash))
                .addConsumer(new TestConsumer(stash))
                .addConsumer(new TestConsumer(stash))
                .build();
        pc.begin();
        assertEquals(0, stash.size());
    }
    
    public class TestProducer implements Producer<String> {

        private static final int NUM = 10000;
        private final Set<String> stash;

        private TestProducer(Set<String> stash) {
            this.stash = stash;
        }
        
        public void produce(Production<String> channel) {
            try {
                for (int i=0; i<NUM; i++) {
                    String o = String.valueOf(i);
                    stash.add(o);
                    channel.put(o);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerConsumerTest.class.getName()).log(Level.SEVERE, "Producer thread interrupted", ex);
            }
            System.out.println("Number of elements produced: " + NUM);
        }

    }
    
    public class TestConsumer implements Consumer<String> {

        private final Set<String> stash;
        
        public TestConsumer(Set<String> stash) {
            this.stash = stash;
        }

        public void consume(Iterable<String> consumables) {
            int i = 0;
            for (String consumable : consumables) {
                stash.remove(consumable);
                i++;
            }
            System.out.println("Consumed: " + i);
        }

    }
    
    public void testTypes() throws InterruptedException {
        
        ProducerConsumer.<Number>newBuilder()
                .addProducer(new Producer<Number>() {
                    @Override
                    public void produce(Production<Number> production) {
                        try {
                            production.put(0);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ProducerConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                })
                .addProducer(new Producer<Integer>() {
                    @Override
                    public void produce(Production<Integer> production) {
                        try {
                            production.put(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ProducerConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                })
                .addConsumer(new Consumer<Number>() {
                    @Override
                    public void consume(Iterable<Number> consumables) {
                        for (Number n : consumables) {
                            System.out.println(this.getClass().getName() + ": " + n);
                        }
                    }
                })
                .addConsumer(new Consumer<Object>() {
                    @Override
                    public void consume(Iterable<Object> consumables) {
                        for (Object o : consumables) {
                            System.out.println(this.getClass().getName() + ": " + o);
                        }
                    }
                })
                .build()
                .begin();
    }
    
}
