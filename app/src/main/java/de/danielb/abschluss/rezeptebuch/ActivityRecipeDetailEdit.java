package de.danielb.abschluss.rezeptebuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import de.danielb.abschluss.rezeptebuch.controller.RecipeAdapter;
import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class ActivityRecipeDetailEdit extends AppCompatActivity {
    private static int REQUEST_NEW_RECIPE = 0x4711;

    private Intent intent;

    private EditText etTitle, etCategory, etDuration, etIngredients, etInstructions;
    private ImageButton ibtnImage;

    private RecipeSqliteHelper recipeSqliteHelper;
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_edit);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initControllers();
        connectViewControls();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(recipeSqliteHelper != null) {
            recipeSqliteHelper.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnSubmit:
                saveRecipe();
                finish();
                break;
            case android.R.id.home:
                cancelRecipe();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initControllers() {
        recipeSqliteHelper = new RecipeSqliteHelper(getApplicationContext(), "MyRecipes.db", null, 1);

        intent = getIntent();
        if(intent != null) {
            long recipeId = intent.getLongExtra("RECIPE_ID", -1);
            if(recipeId >= 0) {
                recipe = recipeSqliteHelper.getRecipe(recipeId);
            }
        }
    }

    private void connectViewControls() {
        etTitle = findViewById(R.id.etTitle);
        etCategory = findViewById(R.id.etCategory);
        etDuration = findViewById(R.id.etDuration);
        etIngredients = findViewById(R.id.etIngredients);
        etInstructions = findViewById(R.id.etInstructions);
        ibtnImage = findViewById(R.id.ibtnImage);

        if(recipe != null) {
            etTitle.setText(recipe.getTitle());
            etCategory.setText(recipe.getCategory());
            etDuration.setText(recipe.getDuration());
            etIngredients.setText(recipe.getIngredients());
            etInstructions.setText(recipe.getInstructions());
            //ibtnImage.setText(recipe.getPathToImage());
        }
    }

    private void cancelRecipe() {
        setResult(RESULT_CANCELED);
    }

    private void saveRecipe() {
        setResult(RESULT_OK);

        if(recipe != null) {
            recipe = new Recipe(0,
                    etTitle.getText().toString(),
                    etCategory.getText().toString(),
                    etDuration.getText().toString(),
                    etIngredients.getText().toString(),
                    etInstructions.getText().toString(),
                    "Path");
            if(recipe.isValid()) {
                recipe = recipeSqliteHelper.addNewRecipe(recipe);
            }
        } else {
            if(recipe.isValid()) {
                recipeSqliteHelper.editRecipe(recipe);
            }
        }
    }
}
