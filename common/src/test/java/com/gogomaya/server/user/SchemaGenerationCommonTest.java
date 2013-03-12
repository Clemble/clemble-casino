package com.gogomaya.server.user;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;

import com.gogomaya.server.game.session.GameSession;
import com.gogomaya.server.player.PlayerProfile;
import com.gogomaya.server.player.security.PlayerCredential;
import com.gogomaya.server.player.security.PlayerIdentity;
import com.gogomaya.server.player.wallet.PlayerWallet;

public class SchemaGenerationCommonTest extends AbstractCommonTest {

    @Test
    public void generatePlayerSchema(){
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
        .addAnnotatedClass(PlayerProfile.class)
        .addAnnotatedClass(PlayerCredential.class)
        .addAnnotatedClass(PlayerIdentity.class)
        .addAnnotatedClass(PlayerWallet.class)
        .setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect")
        .setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");

        new SchemaExport(configuration).setDelimiter(";").setFormat(true).setOutputFile("src/main/resources/sql/player-schema.sql").create(true, false);
    }

    @Test
    public void generateGameSchema(){
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
        .addAnnotatedClass(GameSession.class)
        .setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect")
        .setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");

        new SchemaExport(configuration).setDelimiter(";").setFormat(true).setOutputFile("src/main/resources/sql/game-schema.sql").create(true, false);
    }

}
