package de.danielb.abschluss.rezeptebuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteAdapter;
import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class MainActivityRecipeList extends AppCompatActivity implements ListView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    // activities
    private Intent activityDetail, activityDetailEdit, activitySettings, activityTest;

    //request codes for activity call
    public final static int REQUEST_NEW_RECIPE = 0x4711;
    public final static int REQUEST_EDIT_RECIPE = 0x4712;
    public final static int REQUEST_VIEW_RECIPE = 0x4713;

    //extra keys for activity call
    public final static String EXTRA_RECIPE_ROWID = "EXTRA_RECIPE_ROWID";
    public final static String EXTRA_RECIPE_CALLBACKNOTE = "EXTRA_RECIPE_CALLBACKNOTE";

    //callback notes
    public final static String CALLBACKNOTE_NONE = "NONE";
    public final static String CALLBACKNOTE_DELETE = "DELETE";
    public final static String CALLBACKNOTE_NEW = "NEW";
    public final static String CALLBACKNOTE_UPDATE = "UPDATE";


    // view controls
    private ListView lvRecipes;

    // model controls
    private RecipeSqliteAdapter recipeSqliteAdapter;
    private RecipeSqliteHelper recipeSqliteHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        //init controllers
        initControllers();

        //connect views to controllers and listeners
        connectViewControls();
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
                String note = data.getStringExtra(EXTRA_RECIPE_CALLBACKNOTE);
                long rowid = data.getLongExtra(EXTRA_RECIPE_ROWID, -1L);
                if(note != null && rowid >= 0) {
                    switch (note) {
                        case CALLBACKNOTE_NEW:
                            if(recipeSqliteHelper != null) {
                                recipeSqliteHelper.refreshNew(rowid);
                                lvRecipes.invalidateViews();
                            }
                            break;
                        case CALLBACKNOTE_UPDATE:
                            if(recipeSqliteHelper != null) {
                                recipeSqliteHelper.refreshOld(rowid);
                                lvRecipes.invalidateViews();
                            }
                            break;
                        case CALLBACKNOTE_DELETE:
                            if(recipeSqliteHelper != null) {
                                recipeSqliteHelper.refreshDelete(rowid);
                                lvRecipes.invalidateViews();
                            }
                            break;
                        case CALLBACKNOTE_NONE:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnAdd:
                createNewRecipe();
                break;
            case R.id.mnSettings:
                showSettings();
                break;
            case R.id.mnUpdate:
                lvRecipes.invalidateViews();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long rowid) {
        showDetailRecipe(rowid);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long rowid) {
        editRecipe(rowid);
        return true;
    }
    private void createNewRecipe() {
        //make sure, the intent is instanciated only once
        if (activityDetailEdit == null) {
            activityDetailEdit = new Intent(getApplicationContext(), ActivityRecipeDetailEdit.class);
        }
        activityDetailEdit.putExtra(EXTRA_RECIPE_ROWID, (long) -1);
        activityDetailEdit.putExtra(EXTRA_RECIPE_CALLBACKNOTE, CALLBACKNOTE_NEW);

        startActivityForResult(activityDetailEdit, REQUEST_NEW_RECIPE);
    }

    private void editRecipe(long rowid) {
        //if rowid is in valid range
        if (rowid >= 0) {
            //make sure, the intent is instanciated only once
            if (activityDetailEdit == null) {
                activityDetailEdit = new Intent(getApplicationContext(), ActivityRecipeDetailEdit.class);
            }
            activityDetailEdit.putExtra(EXTRA_RECIPE_ROWID, (long) rowid);
            activityDetailEdit.putExtra(EXTRA_RECIPE_CALLBACKNOTE, CALLBACKNOTE_UPDATE);

            startActivityForResult(activityDetailEdit, REQUEST_EDIT_RECIPE);
        }
    }

    private void showDetailRecipe(long rowid) {
        //if rowid is in valid range
        if (rowid >= 0) {
            //make sure, the intent is instanciated only once
            if (activityDetail == null) {
                activityDetail = new Intent(getApplicationContext(), ActivityRecipeDetail.class);
            }
            activityDetail.putExtra(EXTRA_RECIPE_ROWID, (long) rowid);
            activityDetail.putExtra(EXTRA_RECIPE_CALLBACKNOTE, CALLBACKNOTE_NONE);

            startActivityForResult(activityDetail, REQUEST_VIEW_RECIPE);
        }
    }

    private void initControllers() {
        if(recipeSqliteHelper == null) {
            recipeSqliteHelper = new RecipeSqliteHelper(getApplicationContext(), "MyRecipes.db", null, 1);
        }

        if (recipeSqliteAdapter == null) {
            recipeSqliteAdapter = new RecipeSqliteAdapter(this, recipeSqliteHelper.getRecipeList());
        }
    }

    private void connectViewControls() {
        lvRecipes = findViewById(R.id.lvRecipe);
        lvRecipes.setOnItemClickListener(this);
        lvRecipes.setOnItemLongClickListener(this);
        if (recipeSqliteAdapter != null) {
            lvRecipes.setAdapter(recipeSqliteAdapter);
        }
    }

    private void showSettings() {
        if(activitySettings == null) {
            activitySettings = new Intent(getApplicationContext(), ActivitySettings.class);
        }
        startActivity(activitySettings);
    }

}