package advent2020;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent21 {
    private static final Map<String, String> finalAllergens = newHashMap();
    private static final Map<String, Set<String>> allergenMap = newHashMap();
    private static final List<Food> foods = newArrayList();

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2020/advent21")
                                 .collect(Collectors.toList());

        input.stream()
             .map(s -> s.replace("contains", ""))
             .map(s -> s.replace(",", ""))
             .map(s -> s.replace(")", ""))
             .map(s -> s.split("\\( "))
             .forEach(line -> {
                 String[] stringIngredients = line[0].split(" ");
                 String[] stringAllergens = line[1].split(" ");
                 foods.add(new Food(stringIngredients, stringAllergens));
             });

        foods.stream()
             .map(food -> food.allergens)
             .flatMap(Collection::stream)
             .forEach(allergen -> {
                 Set<String> potentialIngredient = newHashSet();
                 AtomicBoolean initialised = new AtomicBoolean(false);
                 foods.stream()
                      .filter(food -> food.allergens.contains(allergen))
                      .collect(Collectors.toList())
                      .forEach(food -> findAllergenicIngredients(potentialIngredient, initialised, food));

                 allergenMap.put(allergen, potentialIngredient);
             });

        Set<String> nonAllergenic = foods.stream()
                                         .map(food -> food.ingredients)
                                         .flatMap(Collection::stream)
                                         .filter(Advent21::isNonAllergenic)
                                         .collect(Collectors.toSet());

        Integer count = foods.stream()
                             .map(food -> food.ingredients)
                             .map(foodIngredients -> {
                                 Set<String> temp = newHashSet(foodIngredients);
                                 temp.retainAll(nonAllergenic);
                                 return temp.size();
                             })
                             .reduce(Integer::sum)
                             .orElse(-1);
        log.info("Counting ingredients: {}", count);

        while (allergenMap.size() > 0) {
            allergenMap.entrySet()
                       .stream()
                       .filter(entry1 -> entry1.getValue()
                                               .size() == 1)
                       .collect(Collectors.toList())
                       .forEach(entry -> {
                           finalAllergens.put(entry.getKey(), getValue(entry));
                           allergenMap.remove(entry.getKey());
                           allergenMap.values()
                                      .forEach(strings -> strings.remove(getValue(entry)));
                       });
        }

        String dangerous = finalAllergens.entrySet()
                                         .stream()
                                         .sorted(Map.Entry.comparingByKey())
                                         .map(Map.Entry::getValue)
                                         .collect(Collectors.joining(","));
        log.info("Dangerous List: {}", dangerous);
    }

    private static String getValue(Map.Entry<String, Set<String>> entry) {
        return entry.getValue()
                    .stream()
                    .findFirst()
                    .orElse(null);
    }

    private static boolean isNonAllergenic(String ingredient) {
        Set<String> allergenic = allergenMap.values()
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .collect(Collectors.toSet());
        return !allergenic.contains(ingredient);
    }

    private static void findAllergenicIngredients(Set<String> potentialIngredient, AtomicBoolean initialised, Food food) {
        if (potentialIngredient.size() == 0 && !initialised.get()) {
            potentialIngredient.addAll(food.ingredients);
            initialised.set(true);
        } else {
            potentialIngredient.retainAll(food.ingredients);
        }
    }
}

@ToString
class Food {
    Set<String> ingredients;
    Set<String> allergens;

    public Food(String[] ingredient, String[] allergens) {
        this.ingredients = newHashSet(ingredient);
        this.allergens = newHashSet(allergens);
    }
}