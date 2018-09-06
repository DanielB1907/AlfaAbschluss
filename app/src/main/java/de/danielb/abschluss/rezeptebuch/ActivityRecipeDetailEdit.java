package de.danielb.abschluss.rezeptebuch;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;

import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class ActivityRecipeDetailEdit extends AppCompatActivity implements View.OnClickListener {
    private static int REQUEST_NEW_RECIPE = 0x4711;

    private Intent intent;

    private EditText etTitle, etCategory, etDuration, etIngredients, etInstructions;
    private ImageButton ibtnImage;

    private RecipeSqliteHelper recipeSqliteHelper;
    private Recipe recipe = null;

    private File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initControllers();
        connectViewControls();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtnImage:
                setImage();
                break;
        }
    }
    private void initControllers() {
        recipeSqliteHelper = new RecipeSqliteHelper(getApplicationContext(), "MyRecipes.db", null, 1);

        intent = getIntent();
        if (intent != null) {
            long recipeId = intent.getLongExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, -1);
            if (recipeId >= 0) {
                recipe = recipeSqliteHelper.queryRecipeByRowid(recipeId);
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
        ibtnImage.setOnClickListener(this);

    }

    private void updateViews() {
        if (recipe != null) {
            etTitle.setText(recipe.getTitle());
            etCategory.setText(recipe.getCategory());
            etDuration.setText(recipe.getDuration());
            etIngredients.setText(recipe.getIngredients());
            etInstructions.setText(recipe.getInstructions());
            this.imageFile = new File(recipe.getPathToImage());
            if(imageFile.exists()) {
                ibtnImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            }
        }
    }
    private void setImage() {
        String title = "Open Photo";
        CharSequence[] itemlist ={"Take a Photo",
                "Pick from Gallery",
                "Open from File"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.sym_def_app_icon);
        builder.setTitle(title);
        builder.setItems(itemlist, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// Take Photo
                        // Do Take Photo task here
                        break;
                    case 1:// Choose Existing Photo
                        // Do Pick Photo task here
                        break;
                    case 2:// Choose Existing File
                        // Do Pick file here
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(true);
        alert.show();
    }

    private void cancelRecipe() {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_NONE);
        setResult(RESULT_CANCELED, intent);
    }

    private void saveRecipe() {
        if (intent == null) {
            intent = new Intent();
        }

        if (recipe == null) {
            recipe = new Recipe(0,
                    etTitle.getText().toString(),
                    etCategory.getText().toString(),
                    etDuration.getText().toString(),
                    etIngredients.getText().toString(),
                    etInstructions.getText().toString(),
                    "Path");
            if (recipe.isValid()) {
                recipe = recipeSqliteHelper.insertRecipe(recipe);
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, recipe.get_id());
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_NEW);
            } else {
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, -1);
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_NONE);
            }
        } else {
            recipe.setTitle(etTitle.getText().toString());
            recipe.setCategory(etCategory.getText().toString());
            recipe.setDuration(etDuration.getText().toString());
            recipe.setIngredients(etIngredients.getText().toString());
            recipe.setInstructions(etInstructions.getText().toString());
            recipe.setPathToImage("Path");

            if (recipe.isValid()) {
                recipeSqliteHelper.modifyRecipe(recipe);
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, recipe.get_id());
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_UPDATE);
            } else {
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_ROWID, -1);
                intent.putExtra(MainActivityRecipeList.EXTRA_RECIPE_CALLBACKNOTE, MainActivityRecipeList.CALLBACKNOTE_NONE);
            }
        }
        setResult(RESULT_OK, intent);
    }

}
