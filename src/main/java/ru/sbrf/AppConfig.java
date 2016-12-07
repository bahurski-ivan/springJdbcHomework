package ru.sbrf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import ru.sbrf.dao.IngredientDao;
import ru.sbrf.dao.RecipeDao;
import ru.sbrf.dao.impl.h2db.H2DbIngredientDaoImpl;
import ru.sbrf.dao.impl.h2db.H2DbRecipeDao;

import javax.sql.DataSource;


/**
 * Created by Ivan on 06/12/2016.
 */
@Configuration
public class AppConfig {
    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource ds = new DriverManagerDataSource("jdbc:h2:tcp://localhost/~/recipe_db", "sa", "");

        Resource resource = new ClassPathResource("schema.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(ds);

        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public IngredientDao ingredientDao(JdbcTemplate jdbcTemplate) {
        return new H2DbIngredientDaoImpl(jdbcTemplate);
    }

    @Bean
    public RecipeDao recipeDao(JdbcTemplate jdbcTemplate, IngredientDao ingredientDao) {
        return new H2DbRecipeDao(jdbcTemplate, ingredientDao);
    }
}
