package com.gogomaya.server.game.rule;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gogomaya.server.game.configuration.SelectSpecificationOptions;
import com.gogomaya.server.game.rule.bet.BetRule;
import com.gogomaya.server.game.rule.bet.FixedBetRule;
import com.gogomaya.server.game.rule.bet.LimitedBetRule;
import com.gogomaya.server.game.rule.giveup.GiveUpRule;
import com.gogomaya.server.game.rule.time.MoveTimeRule;
import com.gogomaya.server.game.rule.time.TimeBreachPunishment;
import com.gogomaya.server.game.rule.time.TimeRule;
import com.gogomaya.server.game.rule.time.TotalTimeRule;
import com.gogomaya.server.spring.common.CommonModuleSpringConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonModuleSpringConfiguration.class })
public class SeDeRelizationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void timeRule() throws JsonParseException, JsonMappingException, IOException {
        TimeRule timeRule = objectMapper.readValue("{\"punishment\":\"loose\",\"limit\":1}", TotalTimeRule.class);
        Assert.assertTrue(timeRule instanceof TotalTimeRule);
        Assert.assertEquals(timeRule.getPunishment(), TimeBreachPunishment.loose);
        Assert.assertEquals(((TotalTimeRule) timeRule).getLimit(), 1);

        timeRule = objectMapper.readValue("{\"punishment\":\"loose\",\"limit\":1}", TotalTimeRule.class);
        Assert.assertTrue(timeRule instanceof TotalTimeRule);
        Assert.assertEquals(timeRule.getPunishment(), TimeBreachPunishment.loose);
        Assert.assertEquals(((TotalTimeRule) timeRule).getLimit(), 1);

        timeRule = objectMapper.readValue("{\"punishment\":\"loose\",\"limit\":1}", MoveTimeRule.class);
        Assert.assertTrue(timeRule instanceof MoveTimeRule);
        Assert.assertEquals(timeRule.getPunishment(), TimeBreachPunishment.loose);
        Assert.assertEquals(((MoveTimeRule) timeRule).getLimit(), 1);

        timeRule = objectMapper.readValue("{\"punishment\":\"loose\",\"limit\":1}", MoveTimeRule.class);
        Assert.assertTrue(timeRule instanceof MoveTimeRule);
        Assert.assertEquals(timeRule.getPunishment(), TimeBreachPunishment.loose);
        Assert.assertEquals(((MoveTimeRule) timeRule).getLimit(), 1);
    }

    @Test
    public void betRule() throws JsonGenerationException, JsonMappingException, IOException {
        BetRule betRule = objectMapper.readValue("{\"betType\":\"fixed\",\"price\":100}", BetRule.class);
        assertTrue(betRule instanceof FixedBetRule);

        Assert.assertEquals(((FixedBetRule) betRule).getPrice(), 100);

        betRule = objectMapper.readValue("{\"betType\":\"limited\",\"minBet\":100,\"maxBet\":1000}", BetRule.class);
        assertTrue(betRule instanceof LimitedBetRule);
        Assert.assertEquals(((LimitedBetRule) betRule).getMinBet(), 100);
        Assert.assertEquals(((LimitedBetRule) betRule).getMaxBet(), 1000);

        betRule = objectMapper.readValue("{\"betType\":\"fixed\",\"price\":100}", BetRule.class);
        assertTrue(betRule instanceof FixedBetRule);
        Assert.assertEquals(((FixedBetRule) betRule).getPrice(), 100);

        betRule = objectMapper.readValue("{\"betType\":\"limited\",\"minBet\":100,\"maxBet\":1000}", BetRule.class);
        assertTrue(betRule instanceof LimitedBetRule);
        Assert.assertEquals(((LimitedBetRule) betRule).getMinBet(), 100);
        Assert.assertEquals(((LimitedBetRule) betRule).getMaxBet(), 1000);
    }

    @Test
    public void giveUpRule() throws JsonGenerationException, JsonMappingException, IOException {
        GiveUpRule giveUpRule = objectMapper.readValue("\"all\"", GiveUpRule.class);
        Assert.assertEquals(giveUpRule, GiveUpRule.all);

        giveUpRule = objectMapper.readValue("\"lost\"", GiveUpRule.class);
        Assert.assertEquals(giveUpRule, GiveUpRule.lost);
    }
    
    @Test
    public void testReadSpecificationOptions() throws JsonParseException, JsonMappingException, IOException {
        SelectSpecificationOptions selectSpecificationOptions = objectMapper.readValue(
                "{ \"specifications\": [{\"name\":{\"name\": \"low\",\"group\": \"basic\"}," +
                "\"currency\": \"FakeMoney\",\"betRule\":{\"betType\": \"fixed\",\"price\": 50}," +
                "\"giveUpRule\": \"all\",\"moveTimeRule\": { \"punishment\": \"loose\", \"limit\": 0 }," +
                "\"totalTimeRule\": {\"punishment\": \"loose\",\"limit\": 0 }, \"matchRule\": \"automatic\"," +
                "\"privacyRule\": \"everybody\", \"numberRule\": \"two\"}] }", SelectSpecificationOptions.class);
    }

}