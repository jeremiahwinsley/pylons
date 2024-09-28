package net.permutated.pylons.builder;

public class RecipeException extends IllegalStateException {
    public RecipeException(String id, String message) {
        super(String.format("recipe error: %s - %s", id, message));
    }
}
