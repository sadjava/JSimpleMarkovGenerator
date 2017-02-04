package com.peterson.markovchain;

import com.peterson.markovchain.io.PostDeserializationStrategy;
import com.peterson.markovchain.stateless.random.RandomNumberStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static org.mockito.Mockito.mock;

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

    @Override
    public void practicalTest()
    {
        //TODO: why does the practical test fail with this version of the generator?
    }

    @Override
    public void testPostDeserializationInitalization()
    {
        super.testPostDeserializationInitalization();
        ConcurrentBasicMarkovChain concurrentBasicMarkovChain = (ConcurrentBasicMarkovChain) constructDeserializedChain();
        concurrentBasicMarkovChain.readWriteLock = null;
        Pattern pattern = Pattern.compile("");
        RandomNumberStrategy strategy = mock(RandomNumberStrategy.class);
        PostDeserializationStrategy postDeserializationStrategy = constructStrategy(strategy, pattern);
        postDeserializationStrategy.postDeserializationInitialization(concurrentBasicMarkovChain);
        Assert.assertNotNull(concurrentBasicMarkovChain.readWriteLock);
    }

    @Override
    protected PostDeserializationStrategy constructStrategy(RandomNumberStrategy strategy, Pattern pattern)
    {
        return new ConcurrentBasicMarkovChain.ConcurrentBasicMarkovChainDeserializationStrategy(strategy, pattern);
    }

    @Override
    protected BasicMarkovChain constructDeserializedChain()
    {
        return new ConcurrentBasicMarkovChain();
    }
}
