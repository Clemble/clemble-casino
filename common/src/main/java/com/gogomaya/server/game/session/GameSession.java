package com.gogomaya.server.game.session;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.gogomaya.server.game.rule.GameRuleSpecification;

@Entity
@Table(name = "GAME_SESSION")
public class GameSession implements GameSessionAware<GameSession> {

    /**
     * Generated 16/02/13
     */
    private static final long serialVersionUID = -6572596573895530995L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "SESSION_STATE")
    private GameSessionState sessionState;

    @Embedded
    private GameRuleSpecification ruleSpecification;

    @Transient
    private GameState state;

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public GameSession sesSessionId(String sessionId) {
        this.sessionId = checkNotNull(sessionId);
        return this;
    }

    public GameSessionState getSessionState() {
        return sessionState;
    }

    public void setSessionState(GameSessionState gameSessionState) {
        this.sessionState = gameSessionState;
    }

    public GameRuleSpecification getGameRuleSpecification() {
        return ruleSpecification;
    }

    public void setGameRuleSpecification(GameRuleSpecification gameRuleSpecification) {
        this.ruleSpecification = gameRuleSpecification;
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState gameState) {
        this.state = gameState;
    }

}