package com.clemble.casino.server.spring.payment;

import com.clemble.casino.server.spring.web.WebCommonSpringConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by mavarazy on 7/5/14.
 */
@Configuration
@Import({ WebCommonSpringConfiguration.class, PaymentBonusSpringConfiguration.class})
public class PaymentBonusWebSpringConfiguration {
}