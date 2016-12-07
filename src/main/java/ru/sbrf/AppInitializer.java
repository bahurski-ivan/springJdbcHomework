package ru.sbrf;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Created by Ivan on 06/12/2016.
 */
public class AppInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.refresh();
        createSchema(applicationContext.getBean(DataSource.class));
    }

    private void createSchema(DataSource dataSource) {
        Resource resource = new ClassPathResource("schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
    }

    public void afterPropertiesSet() {
        System.out.println("aps");
    }
}
