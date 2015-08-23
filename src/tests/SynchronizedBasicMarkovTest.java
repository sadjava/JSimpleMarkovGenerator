package tests;

import com.peterson.markovchain.ConcurrentBasicMarkovChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.*;

/**
 * @author Peterson, Ryan
 *         Created: 6/9/15
 */
public class SynchronizedBasicMarkovTest extends BasicMarkovTest
{
    public static final int NUM_THREADS = 100;

    @Before
    @Override
    public void setUp()
    {
        markovChain = new ConcurrentBasicMarkovChain();
    }

    @Test
    public void testConcurrentPhraseAdd() throws BrokenBarrierException, InterruptedException
    {
        Runnable []threads = new Runnable[NUM_THREADS];
        CyclicBarrier barrier = new CyclicBarrier(threads.length + 1);
        for(int i = 0; i < threads.length; i++)
        {
            threads[i] = new SimpleThread(markovChain, barrier);
        }

        ExecutorService pool = Executors.newFixedThreadPool(threads.length);
        for(Runnable t : threads)
            pool.submit(t);
        barrier.await();

        for(Runnable t : threads)
        {
            Assert.assertFalse(((SimpleThread)t).exceptionThrown());
        }

    }
}
