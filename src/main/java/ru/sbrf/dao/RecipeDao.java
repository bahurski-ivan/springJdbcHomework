package ru.sbrf.dao;

import ru.sbrf.domain.Ingredient;
import ru.sbrf.domain.Recipe;
import ru.sbrf.util.Pair;

import java.util.List;

/**
 * Created by Ivan on 07/12/2016.
 */
public interface RecipeDao {
    List<Recipe> findByNamePart(String part);

    boolean removeRecipe(Recipe recipe);

    Recipe addRecipe(String name);

    Pair<Recipe, List<Ingredient>> addRecipe(String name, List<Ingredient> ingredients);


    List<Ingredient> getRecipeIngredients(Recipe recipe);

    void removeIngredientFromRecipe(Recipe recipe, Ingredient ingredient);

    Ingredient addIngredientToRecipe(Recipe recipe, Ingredient ingredient);

    List<Ingredient> addMultipleIngredientsToRecipe(Recipe recipe, List<Ingredient> ingredients);
}
