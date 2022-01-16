package bg.softuni.webbookstore.utils;

import bg.softuni.webbookstore.model.entity.CategoryEntity;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    public static List<String> getCategoriesAsStrings(List<CategoryEntity> categoryEntities) {
        return categoryEntities
                .stream()
                .map(categoryEntity -> categoryEntity.getCategory().name())
                .map(s -> s.charAt(0) + s.substring(1).toLowerCase().replaceAll("_", " "))
                .collect(Collectors.toList());
    }

    public static String getFullNameAsString(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public static String capitalizeEnum(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase()
                .replaceAll("_", " ");
    }
}
