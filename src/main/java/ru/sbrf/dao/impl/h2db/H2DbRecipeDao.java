package ru.sbrf.dao.impl.h2db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.sbrf.dao.IngredientDao;
import ru.sbrf.dao.RecipeDao;
import ru.sbrf.domain.Ingredient;
import ru.sbrf.domain.Recipe;
import ru.sbrf.util.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ivan on 07/12/2016.
 */
@Component
public class H2DbRecipeDao implements RecipeDao {
    private final JdbcTemplate jdbcTemplate;
    private final IngredientDao ingredientDao;
    private final RowMapper<Recipe> recipeRowMapper = new RecipeRowMapper();
    private final RowMapper<Ingredient> recipeIngredientRowMapper = new RecipeIngredientRowMapper();

    @Autowired
    public H2DbRecipeDao(JdbcTemplate jdbcTemplate, IngredientDao ingredientDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.ingredientDao = ingredientDao;
    }

    @Override
    public List<Recipe> findByNamePart(String part) {
        final String sql = "SELECT id, name FROM RECIPES " +
                "WHERE name regexp ?";
        return jdbcTemplate.query(sql, recipeRowMapper, ".*" + part + ".*");
    }

    @Override
    public List<Ingredient> getRecipeIngredients(Recipe recipe) {
        final String sql = "SELECT ri.recipe_id as rid, " +
                "ri.ingredient_id as iid, ri.amount as cnt, i.name as ingredient_name " +
                "FROM RECIPE_INGREDIENTS as ri " +
                "INNER JOIN INGREDIENTS as i on i.id = ri.ingredient_id " +
                "WHERE ri.recipe_id = ?";
        return jdbcTemplate.query(sql, recipeIngredientRowMapper, recipe.getId());
    }

    @Override
    public void removeIngredientFromRecipe(Recipe recipe, Ingredient ingredient) {
        final String sql = "DELETE " +
                "FROM RECIPE_INGREDIENTS " +
                "WHERE recipe_id=? AND ingredient_id=?";
        jdbcTemplate.update(sql, recipe.getId(), ingredient.getId());
    }

    @Override
    public Ingredient addIngredientToRecipe(Recipe recipe, Ingredient ingredient) {
        Ingredient ingredient1 = ingredientDao.getOrCreateIngredient(ingredient.getName(), ingredient.getAmount());

        final String sql = "INSERT " +
                "INTO RECIPE_INGREDIENTS " +
                "(recipe_id, ingredient_id, amount) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, recipe.getId(), ingredient1.getId(), ingredient.getAmount());

        return ingredient1;
    }


    @Override
    public boolean removeRecipe(Recipe recipe) {
        final String sql = "DELETE " +
                "FROM RECIPES " +
                "WHERE id=?";

        int result = jdbcTemplate.update(sql, recipe.getId());

        return result == 1;
    }

    @Override
    public Recipe addRecipe(String name) {
        final String sql = "INSERT INTO RECIPES " +
                "(name) VALUES (?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        assert result == 1;

        return new Recipe(keyHolder.getKey().longValue(), name);
    }

    @Override
    public List<Ingredient> addMultipleIngredientsToRecipe(Recipe recipe, List<Ingredient> ingredients) {
        //TODO rewrite to using single batch query
        return ingredients.stream()
                .map(i -> addIngredientToRecipe(recipe, i))
                .collect(Collectors.toList());
    }

    @Override
    public Pair<Recipe, List<Ingredient>> addRecipe(String name, List<Ingredient> ingredients) {
        Recipe recipe = addRecipe(name);
        List<Ingredient> ingredientList = addMultipleIngredientsToRecipe(recipe, ingredients);
        return new Pair<>(recipe, ingredientList);
    }


    private static class RecipeRowMapper implements RowMapper<Recipe> {
        @Override
        public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Recipe(
                    rs.getLong("id"),
                    rs.getString("name")
            );
        }
    }

    private static class RecipeIngredientRowMapper implements RowMapper<Ingredient> {
        @Override
        public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Ingredient(
                    rs.getLong("iid"),
                    rs.getString("ingredient_name"),
                    rs.getInt("cnt")
            );
        }
    }

}
