package com.gogomaya.server.player.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gogomaya.server.error.GogomayaException;
import com.gogomaya.server.player.PlayerState;
import com.gogomaya.server.spring.player.PlayerCommonSpringConfiguration;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PlayerCommonSpringConfiguration.class })
public class PlayerStateManagerTest {

    final private Random RANDOM = new Random();

    @Autowired
    public PlayerStateManager playerStateManager;

    @Test
    public void testMarkAvailable() {
        long player = new Random().nextLong();

        Assert.assertFalse(playerStateManager.isAvailable(player));

        playerStateManager.markAlive(player);

        Assert.assertTrue(playerStateManager.isAvailable(player));
    }

    @Test(expected = GogomayaException.class)
    public void testMarkActiveIncorrect() {
        long player = RANDOM.nextLong();
        long session = RANDOM.nextLong();

        Assert.assertFalse(playerStateManager.isAvailable(player));

        playerStateManager.markBusy(player, session);
    }

    @Test
    public void testMarkActive() {
        long player = RANDOM.nextLong();
        long session = RANDOM.nextLong();

        Assert.assertFalse(playerStateManager.isAvailable(player));

        playerStateManager.markAvailable(player);

        Assert.assertTrue(playerStateManager.isAvailable(player));

        playerStateManager.markBusy(player, session);

        Assert.assertFalse("Player must not be available", playerStateManager.isAvailable(player));
        Assert.assertEquals("Player active session must match", playerStateManager.getActiveSession(player), Long.valueOf(session));
    }

    @Test
    public void testMarkCollectionActive() {
        Collection<Long> players = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            players.add(RANDOM.nextLong());
        long session = RANDOM.nextLong();

        for (Long player : players) {
            Assert.assertFalse(playerStateManager.isAvailable(player));
            playerStateManager.markAvailable(player);
            Assert.assertTrue(playerStateManager.isAvailable(player));
        }

        playerStateManager.markBusy(players, session);

        Assert.assertFalse("None of the players supposed to be available ", playerStateManager.areAvailable(players));
        for (Long player : players) {
            Assert.assertFalse(playerStateManager.isAvailable(player));
            Assert.assertEquals(playerStateManager.getActiveSession(player), Long.valueOf(session));
        }
    }

    /**
     * Tests that allocation of specific player is only possible once
     */
    final private int MARK_ACTIVE_IN_PARRALLEL_THREADS = 100;

    @Test
    public void testMarkActiveInParrallel() {
        final long genericPlayer = RANDOM.nextLong();
        playerStateManager.markAvailable(genericPlayer);
        Assert.assertTrue(playerStateManager.isAvailable(genericPlayer));

        final CountDownLatch startLatch = new CountDownLatch(MARK_ACTIVE_IN_PARRALLEL_THREADS);
        final CountDownLatch endLatch = new CountDownLatch(MARK_ACTIVE_IN_PARRALLEL_THREADS);
        final AtomicInteger numLocksReceived = new AtomicInteger(0);

        final PlayerListener playerListener = new PlayerListener(1);
        playerStateManager.subscribe(genericPlayer, playerListener);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) {
        }

        for (int i = 0; i < MARK_ACTIVE_IN_PARRALLEL_THREADS; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long anotherPlayer = RANDOM.nextLong();
                        playerStateManager.markAvailable(anotherPlayer);
                        Assert.assertTrue(playerStateManager.isAvailable(anotherPlayer));

                        List<Long> participants = ImmutableList.<Long> of(genericPlayer, anotherPlayer);
                        long randomSession = RANDOM.nextLong();
                        startLatch.countDown();
                        startLatch.await();

                        if (playerStateManager.markBusy(participants, randomSession))
                            numLocksReceived.incrementAndGet();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            }).start();
            ;
        }

        try {
            endLatch.await();
        } catch (InterruptedException e) {
        }
        Assert.assertEquals("Expected single lock, but got " + numLocksReceived.get(), 1, numLocksReceived.get());

        try {
            playerListener.countDownLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals("Message did not reach listener ", playerListener.countDownLatch.getCount(), 0);
        Assert.assertEquals("Channel is incorrect ", playerListener.expectedPlayer.poll().longValue(), genericPlayer);
        Assert.assertEquals("State is incorrect ", playerListener.expectedState.poll(), PlayerState.busy);
    }

    @Test
    public void testArbitraryListening() throws InterruptedException {
        long playerId = RANDOM.nextLong();

        PlayerListener playerListener = new PlayerListener(1);
        playerStateManager.subscribe(playerId, playerListener);
        // There is a timeout between listen
        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) {
        }

        playerStateManager.markAvailable(playerId);

        playerListener.countDownLatch.await(1, TimeUnit.SECONDS);

        Assert.assertEquals("Message did not reach listener ", playerListener.countDownLatch.getCount(), 0);
        Assert.assertEquals("Channel is incorrect ", playerListener.expectedPlayer.poll().longValue(), playerId);
        Assert.assertEquals("State is incorrect ", playerListener.expectedState.poll(), PlayerState.available);
    }

    @Test
    public void testStateChangeListening() throws InterruptedException {
        long playerId = RANDOM.nextLong();

        PlayerListener playerListener = new PlayerListener(3);
        playerStateManager.subscribe(playerId, playerListener);
        // There is a timeout between listen

        Thread.sleep(50);

        playerStateManager.markAvailable(playerId);
        playerStateManager.markBusy(playerId, RANDOM.nextLong());
        playerStateManager.markAvailable(playerId);

        playerListener.countDownLatch.await(1, TimeUnit.SECONDS);

        Assert.assertEquals("Message did not reach listener ", playerListener.countDownLatch.getCount(), 0);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("Channel is incorrect ", playerListener.expectedPlayer.poll().longValue(), playerId);
            Assert.assertEquals("State is incorrect ", playerListener.expectedState.poll(), i % 2 == 0 ? PlayerState.available : PlayerState.busy);
        }
    }

    private class PlayerListener implements PlayerStateListener {
        final public CountDownLatch countDownLatch;
        final public ArrayBlockingQueue<Long> expectedPlayer;
        final public ArrayBlockingQueue<PlayerState> expectedState;

        public PlayerListener(int numCalls) {
            countDownLatch = new CountDownLatch(numCalls);
            expectedPlayer = new ArrayBlockingQueue<>(numCalls);
            expectedState = new ArrayBlockingQueue<>(numCalls);
        }

        @Override
        public void onUpdate(long playerId, PlayerState state) {
            try {
                expectedPlayer.put(playerId);
                expectedState.put(state);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        }
    }

}