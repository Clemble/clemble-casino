package com.clemble.casino.server.spring.social;

import com.clemble.casino.server.spring.social.PlayerSocialSpringConfiguration;
import com.clemble.casino.server.spring.web.AbstractWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class PlayerSocialApplicationInitializer extends AbstractWebApplicationInitializer {

    @Override
    protected void doInit(ServletContext container) throws ServletException {
        // Step 2. Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(PlayerSocialWebSpringConfiguration.class);
        // Step 3. Registering appropriate Dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("player", new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}