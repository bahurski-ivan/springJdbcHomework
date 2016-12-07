package ru.sbrf.domain;

/**
 * Created by Ivan on 06/12/2016.
 */
public class Ingredient {
    private final long id;
    private final String name;
    private final int amount;

    public Ingredient(long id, String name, int amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
