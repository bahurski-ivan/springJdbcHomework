package ru.sbrf.domain;

/**
 * Created by Ivan on 06/12/2016.
 */
public class Recipe {
    private final long id;
    private final String name;

    public Recipe(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
