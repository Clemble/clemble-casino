package com.gogomaya.server.game.rule.bet;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class LimitedBetRule implements BetRule {

    /**
     * Generated 09/04/13
     */
    final static private long serialVersionUID = -5560244451652751412L;

    final private static LoadingCache<Long, LimitedBetRule> INSTANCE_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Long, LimitedBetRule>() {

        @Override
        public LimitedBetRule load(Long entry) throws Exception {
            return new LimitedBetRule((int) (entry >> 32), (int) (entry & 0x00000000FFFFFFFFL));
        }

    });

    final private int minBet;

    final private int maxBet;

    @JsonIgnore
    private LimitedBetRule(final int minBet, final int maxBet) {
        if (minBet > maxBet)
            throw new IllegalArgumentException("MIN bet can't be lesser, than MAX bet");
        if (minBet < 0)
            throw new IllegalArgumentException("MIN bet can't be lesser, than 0");
        this.minBet = minBet;
        this.maxBet = maxBet;
    }

    public int getMinBet() {
        return minBet;
    }

    public int getMaxBet() {
        return maxBet;
    }

    @JsonCreator
    public static LimitedBetRule create(@JsonProperty("min") int minBet, @JsonProperty("max") int maxBet) {
        try {
            return INSTANCE_CACHE.get(((long) minBet << 32) | maxBet);
        } catch (ExecutionException e) {
            throw GogomayaException.create(GogomayaError.ServerCriticalError);
        }
    }

}