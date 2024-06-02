package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.CacheSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.service.ServiceRegistry;

import java.util.Map;

final class HConf {

    private HConf() {
        throw new IllegalStateException("utility configuration class");
    }

    static SessionFactory getSessionFactory() {
        final ServiceRegistry serviceRegistry = getServiceRegistry();

        final Configuration hibernateConfiguration = new Configuration();
        hibernateConfiguration.addAnnotatedClass(Student.class);

        return hibernateConfiguration
                .buildSessionFactory(serviceRegistry);
    }

    private static ServiceRegistry getServiceRegistry() {
        final Map<String, Object> hibernateSettings = Map.of(
                JdbcSettings.JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver",
                JdbcSettings.JAKARTA_JDBC_USER, "root",
                JdbcSettings.JAKARTA_JDBC_PASSWORD, "root",
                JdbcSettings.JAKARTA_JDBC_URL, "jdbc:mysql://127.0.0.1:3306/hibernate_learning",
                JdbcSettings.SHOW_SQL, true,
                JdbcSettings.FORMAT_SQL, true,
                SchemaToolingSettings.HBM2DDL_AUTO, "create",
                CacheSettings.USE_SECOND_LEVEL_CACHE, false
        );

        return new StandardServiceRegistryBuilder()
                .applySettings(hibernateSettings)
                .build();
    }

}
