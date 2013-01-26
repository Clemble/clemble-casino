package com.gogomaya.server.spring.user;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.gogomaya.server.user")
@ComponentScan(basePackages = "com.gogomaya.server.user")
@Import(value = { DataSourceConfigurations.class, JPAConfigurations.CloudFoundryConfigurations.class, JPAConfigurations.DefaultConfigurations.class,
        JPAConfigurations.TestConfigurations.class })
@EnableTransactionManagement
public class JPAConfigurations {

    @Inject
    private DataSource dataSource;

    @Inject
    private JpaVendorAdapter jpaVendorAdapter;

    @Inject
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }

    @Bean(name = "transactionManager")
    @Singleton
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory());
        transactionManager.setJpaDialect(jpaDialect());
        transactionManager.setDataSource(dataSource);
        transactionManager.setPersistenceUnitName("entityManager");
        return transactionManager;
    }

    @Bean
    @Singleton
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan(new String[] { "com.gogomaya.server.user" });
        entityManagerFactory.setPersistenceProvider(new HibernatePersistence());
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactory.setPersistenceUnitName("entityManager");
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }
    
    @Bean
    @Singleton
    public PersistenceExceptionTranslator persistenceExceptionTranslator(){
        return new HibernateExceptionTranslator();
    }

    @Configuration
    @Profile(value = "cloud")
    static class CloudFoundryConfigurations {
        @Bean
        @Singleton
        public JpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
            hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
            hibernateJpaVendorAdapter.setShowSql(false);
            return hibernateJpaVendorAdapter;
        }
    }

    @Configuration
    @Profile(value = "default")
    static class DefaultConfigurations {
        @Bean
        @Singleton
        public JpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
            hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
            hibernateJpaVendorAdapter.setShowSql(true);
            return hibernateJpaVendorAdapter;
        }
    }

    @Configuration
    @Profile(value = "test")
    static class TestConfigurations {
        @Bean
        @Singleton
        public JpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
            hibernateJpaVendorAdapter.setDatabase(Database.H2);
            hibernateJpaVendorAdapter.setShowSql(true);
            hibernateJpaVendorAdapter.setGenerateDdl(true);
            return hibernateJpaVendorAdapter;
        }
    }

}