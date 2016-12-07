package ru.sbrf.dao;

import ru.sbrf.domain.Ingredient;

import java.util.Optional;

/**
 * Created by Ivan on 07/12/2016.
 */
public interface IngredientDao {
    Optional<Ingredient> getIngredientByName(String name);

    Ingredient getOrCreateIngredient(String name, int amount);
}
