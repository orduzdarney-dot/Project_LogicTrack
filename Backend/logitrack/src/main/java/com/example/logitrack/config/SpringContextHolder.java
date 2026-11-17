package com.example.logitrack.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    // Método mágico para obtener cualquier Bean de Spring desde fuera (como un Listener de JPA)
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}