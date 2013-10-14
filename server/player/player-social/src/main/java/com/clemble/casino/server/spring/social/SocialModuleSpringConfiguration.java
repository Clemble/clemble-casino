package com.clemble.casino.server.spring.social;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

import com.clemble.casino.server.repository.player.PlayerProfileRepository;
import com.clemble.casino.server.social.SocialConnectionAdapterRegistry;
import com.clemble.casino.server.social.SocialConnectionDataAdapter;
import com.clemble.casino.server.social.SocialPlayerProfileCreator;
import com.clemble.casino.server.social.adapter.FacebookSocialAdapter;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.server.spring.player.PlayerManagementSpringConfiguration;

@Configuration
@Import(value = { PlayerManagementSpringConfiguration.class })
public class SocialModuleSpringConfiguration implements SpringConfiguration {

    @Autowired
    @Qualifier("dataSource")
    public DataSource dataSource;

    @Autowired
    @Qualifier("playerProfileRepository")
    public PlayerProfileRepository playerProfileRepository;

    @Bean
    public SocialConnectionDataAdapter socialConnectionDataAdapter() {
        return new SocialConnectionDataAdapter(connectionFactoryLocator(), usersConnectionRepository(), socialAdapterRegistry());
    }

    @Bean
    public ConnectionFactoryRegistry connectionFactoryLocator() {
        ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
        connectionFactoryRegistry.addConnectionFactory(facebookConnectionFactory());
        return connectionFactoryRegistry;
    }

    @Bean
    public FacebookConnectionFactory facebookConnectionFactory() {
        FacebookConnectionFactory facebookConnectionFactory = new FacebookConnectionFactory("486714778051999", "cd4976a1ae74d3e70e804e1ae82b7eb6");
        return facebookConnectionFactory;
    }

    @Bean
    public SocialConnectionAdapterRegistry socialAdapterRegistry() {
        SocialConnectionAdapterRegistry socialAdapterRegistry = new SocialConnectionAdapterRegistry();
        socialAdapterRegistry.register(new FacebookSocialAdapter());
        return socialAdapterRegistry;
    }

    @Bean
    public ConnectionSignUp connectionSignUp() {
        return new SocialPlayerProfileCreator(socialAdapterRegistry());
    }

    @Bean
    public UsersConnectionRepository usersConnectionRepository() {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator(), Encryptors.noOpText());
        repository.setConnectionSignUp(connectionSignUp());
        return repository;
    }

}
