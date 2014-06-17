package com.clemble.casino.integration;

import static com.clemble.test.random.ObjectGenerator.register;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.clemble.casino.base.ExpectedEvent;
import com.clemble.casino.game.*;
import com.clemble.casino.game.event.schedule.InvitationAcceptedEvent;
import com.clemble.casino.game.event.schedule.InvitationResponseEvent;
import com.clemble.casino.game.rule.RoundRule;
import com.clemble.casino.game.specification.RoundGameConfiguration;
import com.clemble.casino.game.unit.GameUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth.common.signature.RSAKeySecret;

import com.clemble.casino.VersionAware;
import com.clemble.casino.base.ActionLatch;
import com.clemble.casino.game.RoundGameContext;
import com.clemble.casino.game.action.BetAction;
import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.game.action.surrender.GiveUpAction;
import com.clemble.casino.game.construct.AutomaticGameRequest;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.construct.GameConstructionState;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.game.outcome.GameOutcome;
import com.clemble.casino.game.rule.bet.FixedBetRule;
import com.clemble.casino.game.rule.bet.LimitedBetRule;
import com.clemble.casino.game.rule.bet.UnlimitedBetRule;
import com.clemble.casino.game.rule.construct.PlayerNumberRule;
import com.clemble.casino.game.rule.construct.PrivacyRule;
import com.clemble.casino.game.specification.GameConfiguration;
import com.clemble.casino.game.specification.GameConfigurationKey;
import com.clemble.casino.game.specification.MatchGameConfiguration;
import com.clemble.casino.game.specification.TournamentGameConfiguration;
import com.clemble.casino.integration.game.NumberState;
import com.clemble.casino.integration.game.NumberUnit;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.PaymentTransactionKey;
import com.clemble.casino.payment.PlayerAccount;
import com.clemble.casino.payment.money.Currency;
import com.clemble.casino.payment.money.Money;
import com.clemble.casino.payment.money.Operation;
import com.clemble.casino.player.PlayerCategory;
import com.clemble.casino.player.PlayerGender;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.player.security.PlayerCredential;
import com.clemble.casino.server.game.action.MatchGameState;
import com.clemble.casino.utils.ClembleConsumerDetailUtils;
import com.clemble.test.random.AbstractValueGenerator;
import com.clemble.test.random.ObjectGenerator;
import com.clemble.test.random.ValueGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class IntegrationObjectTest {

    static {
        register(InvitationResponseEvent.class, new AbstractValueGenerator<InvitationResponseEvent>() {
            @Override
            public InvitationResponseEvent generate() {
                return new InvitationAcceptedEvent("d", new GameSessionKey(Game.go, "b"));
            }
        });
        register(ExpectedEvent.class, new AbstractValueGenerator<ExpectedEvent>() {
            @Override
            public ExpectedEvent generate() {
                return new ExpectedEvent("S", InvitationAcceptedEvent.class);
            }
        });
        register(NumberState.class, new AbstractValueGenerator<NumberState>() {
            @Override
            public NumberState generate() {
                GameInitiation initiation = new GameInitiation(GameSessionKey.DEFAULT_SESSION, ImmutableList.of("A", "B"), RoundGameConfiguration.DEFAULT);
                return new NumberState(new RoundGameContext(initiation), null, 0);
            }
        });
        register(RoundGameContext.class, new AbstractValueGenerator<RoundGameContext>(){
            @Override
            public RoundGameContext generate() {
                GameInitiation initiation = new GameInitiation(GameSessionKey.DEFAULT_SESSION, ImmutableList.of("A", "B"), RoundGameConfiguration.DEFAULT);
                return new RoundGameContext(initiation);
            }
            
        });
        register(GameSessionKey.class, new AbstractValueGenerator<GameSessionKey>() {
            @Override
            public GameSessionKey generate() {
                return new GameSessionKey(ObjectGenerator.generate(Game.class), ObjectGenerator.generate(String.class));
            }
        });
        register(FixedBetRule.class, new AbstractValueGenerator<FixedBetRule>() {
            @Override
            public FixedBetRule generate() {
                return FixedBetRule.create(new long[] { 10 });
            }
        });
        register(GameAction.class, new AbstractValueGenerator<GameAction>() {
            @Override
            public GameAction generate() {
                return new GiveUpAction(RandomStringUtils.random(5));
            }
        });
        register(BetAction.class, new AbstractValueGenerator<BetAction>() {
            @Override
            public BetAction generate() {
                return new BetAction(RandomStringUtils.random(5), 100);
            }
        });
        register(PlayerAccount.class, new AbstractValueGenerator<PlayerAccount>() {
            @Override
            public PlayerAccount generate() {
                return new PlayerAccount(RandomStringUtils.random(5), ImmutableSet.of(Money.create(Currency.FakeMoney, 500)));
            }
        });
        register(GameUnit.class, new AbstractValueGenerator<GameUnit>() {
            @Override
            public GameUnit generate() {
                return new NumberUnit(Collections.<GameUnit>emptyList());
            }
        });
        register(GameState.class, new AbstractValueGenerator<GameState>() {
            @Override
            public GameState generate() {
                return new NumberState(
                    ObjectGenerator.generate(RoundGameContext.class),
                    ObjectGenerator.generate(GameUnit.class),
                    0);
            }
        });
        register(PaymentTransaction.class, new AbstractValueGenerator<PaymentTransaction>() {
            @Override
            public PaymentTransaction generate() {
                return new PaymentTransaction()
                        .setTransactionKey(new PaymentTransactionKey().setSource("TicTacToe").setTransaction(RandomStringUtils.random(5)))
                        .setTransactionDate(new Date())
                        .setProcessingDate(new Date())
                        .addPaymentOperation(
                                new PaymentOperation().setAmount(Money.create(Currency.FakeMoney, 50)).setOperation(Operation.Credit)
                                        .setPlayer(RandomStringUtils.random(5)))
                        .addPaymentOperation(
                                new PaymentOperation().setAmount(Money.create(Currency.FakeMoney, 50)).setOperation(Operation.Debit)
                                        .setPlayer(RandomStringUtils.random(5)));
            }
        });
        register(PlayerCredential.class, new AbstractValueGenerator<PlayerCredential>() {
            @Override
            public PlayerCredential generate() {
                return new PlayerCredential().setEmail(RandomStringUtils.randomAlphabetic(10) + "@gmail.com").setPassword(RandomStringUtils.random(10))
                        .setPlayer(RandomStringUtils.random(5));
            }
        });
        register(PlayerProfile.class, new AbstractValueGenerator<PlayerProfile>() {
            @Override
            public PlayerProfile generate() {
                return new PlayerProfile().setBirthDate(new Date(0)).setCategory(PlayerCategory.Amateur).setFirstName(RandomStringUtils.randomAlphabetic(10))
                        .setGender(PlayerGender.M).setLastName(RandomStringUtils.randomAlphabetic(10)).setNickName(RandomStringUtils.randomAlphabetic(10))
                        .setPlayer(RandomStringUtils.random(5));
            }
        });
        register(RoundRule.class, new AbstractValueGenerator<RoundRule>() {
            @Override
            public RoundRule generate() {
                return UnlimitedBetRule.INSTANCE;
            }
        });
        register(GameConstruction.class, new AbstractValueGenerator<GameConstruction>() {
            @Override
            public GameConstruction generate() {
                return new GameConstruction()
                        .setSession(new GameSessionKey(Game.pic, "0"))
                        .setRequest(new AutomaticGameRequest(RandomStringUtils.random(5), RoundGameConfiguration.DEFAULT))
                        .setResponses(new ActionLatch().expectNext(ImmutableList.<String> of(RandomStringUtils.random(5), RandomStringUtils.random(5)), InvitationResponseEvent.class))
                        .setState(GameConstructionState.pending);
            }
        });
        register(LimitedBetRule.class, new AbstractValueGenerator<LimitedBetRule>() {
            @Override
            public LimitedBetRule generate() {
                return LimitedBetRule.create(10, 200);
            }
        });
        register(RoundGameConfiguration.class, new AbstractValueGenerator<RoundGameConfiguration>() {
            @Override
            public RoundGameConfiguration generate() {
                return RoundGameConfiguration.DEFAULT;
            }
        });
        register(VersionAware.class, "version", new ValueGenerator<Integer>() {
            @Override
            public Integer generate() {
                return 0;
            }

            @Override
            public int scope() {
                return 1;
            }

            public ValueGenerator<Integer> clone() {
                return this;
            }
        });
        register(GameConfiguration.class, new AbstractValueGenerator<GameConfiguration>() {
            @Override
            public GameConfiguration generate() {
                return RoundGameConfiguration.DEFAULT;
            }
        });
        register(TournamentGameConfiguration.class, new AbstractValueGenerator<TournamentGameConfiguration>() {
            @Override
            public TournamentGameConfiguration generate() {
                return new TournamentGameConfiguration(new GameConfigurationKey(Game.pic, "AAA"), new Money(Currency.FakeMoney, 50), PrivacyRule.players, PlayerNumberRule.two, RoundGameConfiguration.DEFAULT, null, null, null, null, null);
            }
        });

        final RSAKeySecret rsaKey = ClembleConsumerDetailUtils.randomKey();
        register(PrivateKey.class, new AbstractValueGenerator<PrivateKey>() {
            @Override
            public PrivateKey generate() {
                return rsaKey.getPrivateKey();
            }
        });
        register(PublicKey.class, new AbstractValueGenerator<PublicKey>() {
            @Override
            public PublicKey generate() {
                return rsaKey.getPublicKey();
            }
        });
        register(MatchGameContext.class, new AbstractValueGenerator<MatchGameContext>() {
            @Override
            public MatchGameContext generate() {
                return new MatchGameContext(GameSessionKey.DEFAULT_SESSION, null, Collections.<MatchGamePlayerContext>emptyList(), null, 0, Collections.<GameOutcome>emptyList());
            }
        });
        register(TournamentGameContext.class, new AbstractValueGenerator<TournamentGameContext>() {
            @Override
            public TournamentGameContext generate() {
                return new TournamentGameContext(GameSessionKey.DEFAULT_SESSION, null, null, null);
            }
        });
        try {
            final KeyGenerator AES = KeyGenerator.getInstance("AES");
            AES.init(256, new SecureRandom());
            register(SecretKey.class, new AbstractValueGenerator<SecretKey>() {
                @Override
                public SecretKey generate() {
                    return AES.generateKey();
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}