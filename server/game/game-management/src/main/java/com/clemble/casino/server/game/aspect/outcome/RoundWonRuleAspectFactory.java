package com.clemble.casino.server.game.aspect.outcome;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import com.clemble.casino.game.RoundGameContext;
import com.clemble.casino.game.specification.RoundGameConfiguration;
import org.springframework.core.Ordered;

import com.clemble.casino.game.event.server.GameEndedEvent;
import com.clemble.casino.server.game.aspect.GameAspect;
import com.clemble.casino.server.game.aspect.RoundGameAspectFactory;
import com.clemble.casino.server.payment.ServerPaymentTransactionService;

/**
 * Created by mavarazy on 23/12/13.
 */
public class RoundWonRuleAspectFactory implements RoundGameAspectFactory<GameEndedEvent<?>> {

    final private ServerPaymentTransactionService transactionService;

    public RoundWonRuleAspectFactory(ServerPaymentTransactionService transactionService) {
        this.transactionService = checkNotNull(transactionService);
    }

    @Override
    public GameAspect<GameEndedEvent<?>> construct(RoundGameConfiguration configuration, RoundGameContext context) {
        // Step 1. Checking won rule specified
        if (configuration.getWonRule() == null)
            return null;
        // Step 2. Checking won rule
        switch (configuration.getWonRule()) {
            case price:
                return new RoundWonByPriceRuleAspect(configuration.getPrice(), transactionService);
            case spent:
                return new RoundWonBySpentRuleAspect(configuration.getPrice().getCurrency(), transactionService);
            case owned:
                return new RoundWonByOwnedRuleAspect(configuration.getPrice().getCurrency(), transactionService);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getOrder(){
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

}