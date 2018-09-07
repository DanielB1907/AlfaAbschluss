package de.danielb.abschluss.rezeptebuch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class ActivityRecipeDetailEdit extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE = 0x4714;

    private Intent intent;

    private EditText etTitle, etCategory, etDuration, etIngredients, etInstructions;
    private ImageButton ibtnImage;

    private RecipeSqliteHelper recipeSqliteHelper;
    private Recipe recipe = null;

    private String pathToImageFile;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
            Uri fullPhotoUri = data.getData();
            this.pathToImageFile = fullPhotoUri.toString();
            updateImageButton();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtnImage:
                selectImageAction();
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
        updateViews();
    }

    private void updateViews() {
        if (recipe != null) {
            etTitle.setText(recipe.getTitle());
            etCategory.setText(recipe.getCategory());
            etDuration.setText(recipe.getDuration());
            etIngredients.setText(recipe.getIngredients());
            etInstructions.setText(recipe.getInstructions());
            this.pathToImageFile = recipe.getPathToImage();
            updateImageButton();
        }
    }

    public void selectImageAction() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    private void updateImageButton() {
        try {
            InputStream inStream = getContentResolver().openInputStream(Uri.parse(this.pathToImageFile));
            Bitmap bitmap = BitmapFactory.decodeStream(inStream);
            ibtnImage.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
                    !this.pathToImageFile.isEmpty() ? this.pathToImageFile : "noImage");
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
            recipe.setPathToImage(!this.pathToImageFile.isEmpty() ? this.pathToImageFile : "noImage");

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
