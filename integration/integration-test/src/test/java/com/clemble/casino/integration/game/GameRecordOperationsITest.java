package com.clemble.casino.integration.game;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameRecord;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.action.MadeMove;
import com.clemble.casino.integration.event.EventAccumulator;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.payment.PaymentTransactionKey;
import com.clemble.casino.payment.event.FinishedPaymentEvent;
import com.clemble.casino.payment.event.PaymentEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mavarazy on 09/03/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class GameRecordOperationsITest {

    @Autowired
    public GameScenarios gameScenarios;

    @Test
    public void testRoundRecordSaving(){
        List<RoundGamePlayer<NumberState>> sessionPlayers = gameScenarios.match(Game.num);
        // Step 1. Preparing game session listener
        EventAccumulator<PaymentEvent> paymentListener = new EventAccumulator<PaymentEvent>();
        RoundGamePlayer<NumberState> A = sessionPlayers.get(0);
        A.playerOperations().paymentOperations().subscribe(paymentListener);
        RoundGamePlayer<NumberState> B = sessionPlayers.get(1);
        A.waitForStart();
        B.waitForStart();
        // Step 2. Make a surrender by player B
        A.perform(new SelectNumberAction(A.getPlayer(), 2));
        B.perform(new SelectNumberAction(B.getPlayer(), 1));
        // Step 3. Synching players
        B.syncWith(A);
        // Step 4. Checking payment transaction complete
        PaymentEvent event = paymentListener.poll(5, TimeUnit.SECONDS);
        assertNotNull(event);
        assertTrue(event instanceof FinishedPaymentEvent);
        // Step 5. Checking transaction key is the same as game construction
        PaymentTransactionKey transactionKey = ((FinishedPaymentEvent) event).getTransactionKey();
        GameSessionKey sessionKey = A.getSession();
        assertEquals(sessionKey.getSession(), transactionKey.getTransaction());
        assertEquals(sessionKey.getGame().name(), transactionKey.getSource());
        // Step 6. Checking game record
        GameRecord AgameRecord = A.playerOperations().gameRecordOperations().get(sessionKey);
        GameRecord BgameRecord = A.playerOperations().gameRecordOperations().get(sessionKey);
        // Step 7. Checking game records are same
        assertEquals(AgameRecord, BgameRecord);
        assertEquals(AgameRecord.getMadeMoves().size(), 2);
        Iterator<MadeMove> moveIterator = AgameRecord.getMadeMoves().iterator();
        assertEquals(moveIterator.next().getRequest(), new SelectNumberAction(A.getPlayer(), 2));
        assertEquals(moveIterator.next().getRequest(), new SelectNumberAction(B.getPlayer(), 1));
    }

}
