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
    // activities
    private Intent activityDetailEdit;

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

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(data != null) {
                String note = data.getStringExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE);
                long rowid = data.getLongExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, -1L);
                if(note != null && rowid >= 0) {
                    switch (note) {
                        case MainActivityRecipeList.CALLBACKNOTE_NEW:
                            if(recipeSqliteHelper != null) {
                                recipe = recipeSqliteHelper.queryRecipeByRowid(rowid);
                                updateView();

                                if (intent == null) {
                                    intent = new Intent();
                                }
                                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, rowid);
                                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_NEW);
                                setResult(RESULT_OK, intent);
                            }
                            break;
                        case MainActivityRecipeList.CALLBACKNOTE_UPDATE:
                            if(recipeSqliteHelper != null) {
                                recipe = recipeSqliteHelper.queryRecipeByRowid(rowid);
                                updateView();

                                if (intent == null) {
                                    intent = new Intent();
                                }
                                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, rowid);
                                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_UPDATE);
                                setResult(RESULT_OK, intent);
                            }
                            break;
                        case MainActivityRecipeList.CALLBACKNOTE_DELETE:
                            if (intent == null) {
                                intent = new Intent();
                            }
                            intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, rowid);
                            intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_DELETE);
                            setResult(RESULT_OK, intent);
                            finish();
                            break;
                        case MainActivityRecipeList.CALLBACKNOTE_NONE:
                            break;
                    }
                }
            }
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
                editRecipe(recipe.get_id());
                break;
            case R.id.mnDelete:
                deleteRecipe(recipe.get_id());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initControllers() {
        recipeSqliteHelper = new RecipeSqliteHelper(getApplicationContext(), "MyRecipes.db", null, 1);

        intent = getIntent();
        if(intent != null) {
            long recipeId = intent.getLongExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, -1);
            if(recipeId >= 0) {
                recipe = recipeSqliteHelper.queryRecipeByRowid(recipeId);
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

        updateView();
    }

    private void updateView() {
        if(recipe != null) {
            tvTitle.setText(recipe.getTitle());
            tvCategory.setText(recipe.getCategory());
            tvDuration.setText(recipe.getDuration());
            tvIngredients.setText(recipe.getIngredients());
            tvInstructions.setText(recipe.getInstructions());
            //ibtnImage.setText(recipe.getPathToImage());
        }
    }

    private void editRecipe(long rowid) {
        //if rowid is in valid range
        if (rowid >= 0) {
            //make sure, the intent is instanciated only once
            if (activityDetailEdit == null) {
                activityDetailEdit = new Intent(getApplicationContext(), ActivityRecipeDetailEdit.class);
            }
            activityDetailEdit.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, (long) rowid);
            activityDetailEdit.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_UPDATE);

            startActivityForResult(activityDetailEdit, MainActivityRecipeList.REQUEST_EDIT_RECIPE);
        }
    }

    private void deleteRecipe(long rowid) {
        //if rowid is in valid range
        if (rowid >= 0) {
            recipeSqliteHelper.deleteRecipeByRowid(rowid);

            if(intent == null) {
                intent = new Intent();
            }

            intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, (long) rowid);
            intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_DELETE);
            setResult(RESULT_OK, intent);
        }
    }

}
