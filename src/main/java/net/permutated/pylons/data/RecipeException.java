package net.permutated.pylons.data;

public class RecipeException extends IllegalStateException {
    public RecipeException(String id, String message) {
        super(String.format("recipe error: %s - %s", id, message));
    }
}
