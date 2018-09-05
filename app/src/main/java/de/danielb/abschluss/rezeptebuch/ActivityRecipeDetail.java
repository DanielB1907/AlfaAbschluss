package de.danielb.abschluss.rezeptebuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class ActivityRecipeDetail extends AppCompatActivity {
    private Intent intent;

    private TextView tvTitle, tvCategory, tvDuration, tvIngredients, tvInstructions;
    private ImageButton ibtnImage;

    private RecipeSqliteHelper recipeSqliteHelper;
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.mnActivityEdit:
                break;
            case R.id.mnDelete:
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
            } else {
                finish();
            }
        }
    }

    private void connectViewControls() {
        tvTitle = findViewById(R.id.tvTitle);
        tvCategory = findViewById(R.id.tvCategory);
        tvDuration = findViewById(R.id.tvDuration);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvInstructions = findViewById(R.id.tvInstructions);
        //ibtnImage = findViewById(R.id.ibtnImage);

        if(recipe != null) {
            tvTitle.setText(recipe.getTitle());
            tvCategory.setText(recipe.getCategory());
            tvDuration.setText(recipe.getDuration());
            tvIngredients.setText(recipe.getIngredients());
            tvInstructions.setText(recipe.getInstructions());
            //ibtnImage.setText(recipe.getPathToImage());
        }
    }
}
