package ru.sbrf;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import ru.sbrf.controller.ActionController;
import ru.sbrf.domain.Ingredient;
import ru.sbrf.domain.Recipe;
import ru.sbrf.util.Pair;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 06/12/2016.
 */
@SpringBootApplication
public class ConsoleApp {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(ConsoleApp.class)
                .web(false)
                //a.initializers(new AppInitializer())
                .run(args);

        ActionController controller = context.getBean(ActionController.class);

        String line = "";
        boolean running = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        printHelp();

        while (running) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (line == null)
                return;

            if ("exit".equals(line))
                running = false;
            else if ("find".equals(line))
                handleFind(controller, br);
            else if ("add".equals(line))
                handleAdd(controller, br);
            else if ("remove".equals(line))
                handleRemove(controller, br);
            else if ("help".equals(line))
                printHelp();

            System.out.println();
        }
    }


    private static void handleRemove(ActionController controller, BufferedReader br) {
        System.out.print("Enter recipe id to remove -> ");
        long id = readLongFromConsole(br);
        boolean result = controller.removeRecipe(new Recipe(id, null));

        if (result)
            System.out.println("successfully removed!");
        else
            System.out.println("recipe with given id not found!");
    }

    private static void handleAdd(ActionController controller, BufferedReader br) {
        System.out.print("Enter recipe name -> ");
        String name = readLineFromConsole(br);

        System.out.println("Enter recipe ingredients (type 'stop' to finish)");

        List<Ingredient> ingredients = new ArrayList<>();
        String line = "Y";

        while (line.toLowerCase().equals("y")) {
            System.out.print("  Enter ingredient name -> ");
            String ingredientName = readLineFromConsole(br);
            System.out.print("  Enter amount of ingredient used in recipe -> ");
            int amount = readPositiveIntFromConsole(br);

            ingredients.add(new Ingredient(-1, ingredientName, amount));

            System.out.println("Do you want to add another ingredient (Y/N)?");
            line = readLineFromConsole(br);
        }

        Pair<Recipe, List<Ingredient>> recipeAddResult = controller.addRecipe(name, ingredients);

        System.out.println("recipe added!");
        System.out.println(recipeAddResult.getFirst());
        System.out.println("ingredients: ");
        System.out.println(recipeAddResult.getSecond());
    }

    private static void handleFind(ActionController controller, BufferedReader br) {
        System.out.print("Enter part of recipe name -> ");
        String namePart = readLineFromConsole(br);

        boolean result = controller.findRecipeByNamePart(namePart).stream().peek(r -> {
            System.out.println(r);
            List<Ingredient> ingredientList = controller.getIngredientList(r);
            System.out.println("Ingredients: ");
            System.out.println(ingredientList);

            System.out.println();
        }).allMatch(v -> true);

        if (!result)
            System.out.println("Recipes with given name not found");
    }


    @NotNull
    private static String readLineFromConsole(BufferedReader br) {
        String line = null;

        boolean firstTry = true;

        do {
            if (!firstTry) {
                System.out.println("please enter not empty string!");
                System.out.print("> ");
            }

            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            firstTry = false;

        } while (line.isEmpty());

        return line;
    }

    private static long readLongFromConsole(BufferedReader br) {
        long result = 0;
        boolean nextTry = false;

        do {

            if (nextTry) {
                System.out.println("please enter not negative and parsable integral number");
                System.out.print("> ");
            }

            nextTry = false;
            String line = readLineFromConsole(br);

            try {
                result = Long.parseLong(line);
            } catch (NumberFormatException e) {
                nextTry = true;
            }

            if (!nextTry)
                if (result < 0)
                    nextTry = true;

        } while (nextTry);

        return result;
    }

    private static int readPositiveIntFromConsole(BufferedReader br) {
        int result = 0;
        boolean nextTry = false;

        do {

            if (nextTry) {
                System.out.println("please enter not negative and parsable integral number");
            }

            nextTry = false;
            String line = readLineFromConsole(br);

            try {
                result = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                nextTry = true;
            }

            if (!nextTry)
                if (result < 0)
                    nextTry = true;

        } while (nextTry);

        return result;
    }

    private static void printHelp() {
        System.out.println("command list:");
        System.out.println(" 'add'    - add recipe");
        System.out.println(" 'remove' - remove recipe");
        System.out.println(" 'find'   - find recipe");
    }
}
