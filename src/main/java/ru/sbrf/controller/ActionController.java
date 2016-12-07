package ru.sbrf.controller;

import org.springframework.stereotype.Controller;
import ru.sbrf.dao.RecipeDao;
import ru.sbrf.domain.Ingredient;
import ru.sbrf.domain.Recipe;
import ru.sbrf.util.Pair;

import java.util.List;

/**
 * Created by Ivan on 06/12/2016.
 */
@Controller
public class ActionController {
    private final RecipeDao recipeDao;

    public ActionController(RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    public List<Recipe> findRecipeByNamePart(String part) {
        return recipeDao.findByNamePart(part);
    }

    public Pair<Recipe, List<Ingredient>> addRecipe(String name, List<Ingredient> ingredientList) {
        return recipeDao.addRecipe(name, ingredientList);
    }

    public boolean removeRecipe(Recipe recipe) {
        return recipeDao.removeRecipe(recipe);
    }

    public List<Ingredient> getIngredientList(Recipe recipe) {
        return recipeDao.getRecipeIngredients(recipe);
    }
}
