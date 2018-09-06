package de.danielb.abschluss.rezeptebuch.model;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class Recipe {
    private long _id = 0;
    private String title = "";
    private String category = "";
    private String duration = "";
    private String ingredients = "";
    private String instructions = "";
    private String pathToImage = "";

    public Recipe(long _id, String title, String category, String duration, String ingredients, String instructions, String pathToImage) {
        this._id = _id;
        this.title = title;
        this.category = category;
        this.duration = duration;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.pathToImage = pathToImage;
    }

    public boolean isValid() {
        return this._id >= 0 &&
                (this.title != null && !this.title.isEmpty()) &&
                (this.category != null && !this.category.isEmpty()) &&
                (this.duration != null && !this.duration.isEmpty()) &&
                (this.ingredients != null && !this.ingredients.isEmpty()) &&
                (this.instructions != null && !this.instructions.isEmpty()) &&
                (this.pathToImage != null && !this.pathToImage.isEmpty());
    }

    public void setId(long id) {
        this._id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public long get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDuration() {
        return duration;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    @Override
    public String toString() {
        return "_id:" + _id + "\n" +
                "title:" + title + "\n" +
                "category:" + category + "\n" +
                "duration:" + duration + "\n" +
                "ingredients:" + ingredients + "\n" +
                "instructions:" + instructions + "\n" +
                "pathToImage:" + pathToImage + "\n" +
                super.toString();
    }


}
