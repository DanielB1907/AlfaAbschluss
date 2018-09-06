package de.danielb.abschluss.rezeptebuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteAdapter;
import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class ActivityRecipeList extends AppCompatActivity implements ListView.OnItemClickListener {
    // next activities
    private Intent activityDetail, activityDetailEdit, activitySettings, activityTest;

    private static int REQUEST_NEW_RECIPE = 0x4711;

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

        //connect views and listeners
        connectViewControls();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            // lvRecipes.invalidate();
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
                if(activityDetailEdit == null) {
                    activityDetailEdit = new Intent(getApplicationContext(), ActivityRecipeDetailEdit.class);
                }
                activityDetailEdit.putExtra("RECIPE_ID", (long) -1);
                startActivityForResult(activityDetailEdit, REQUEST_NEW_RECIPE);
                break;
            case R.id.mnSettings:
                Toast.makeText(this, "Menu Settings Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnUpdate:

                lvRecipes.invalidate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long rowid) {
        showDetail(rowid);
    }

    private void showDetail(long rowid) {
        if(activityDetail == null) {
            activityDetail = new Intent(getApplicationContext(), ActivityRecipeDetail.class);
        }
        activityDetail.putExtra("RECIPE_ID", rowid);
        startActivity(activityDetail);
    }

    private void initControllers() {
        recipeSqliteHelper = new RecipeSqliteHelper(getApplicationContext(), "MyRecipes.db", null, 1);
        recipeSqliteAdapter = new RecipeSqliteAdapter(this, recipeSqliteHelper);
    }

    private void connectViewControls() {
        lvRecipes = findViewById(R.id.lvRecipe);
        lvRecipes.setOnItemClickListener(this);

        if(recipeSqliteAdapter != null) {
            lvRecipes.setAdapter(recipeSqliteAdapter);
        }
    }
}