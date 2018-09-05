package de.danielb.abschluss.rezeptebuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class ActivitySettings extends AppCompatActivity {
    Intent nextActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mnActivityList:
                nextActivity = new Intent();
                nextActivity.setClass(getApplicationContext(), ActivityRecipeList.class);
                startActivity(nextActivity);
                break;
            case R.id.mnActivityDetail:
                nextActivity = new Intent();
                nextActivity.setClass(getApplicationContext(), ActivityRecipeDetail.class);
                startActivity(nextActivity);
                break;
            case R.id.mnActivityEdit:
                nextActivity = new Intent();
                nextActivity.setClass(getApplicationContext(), ActivityRecipeDetailEdit.class);
                startActivity(nextActivity);
                break;
            case R.id.mnActivitySettings:
                nextActivity = new Intent();
                nextActivity.setClass(getApplicationContext(), ActivitySettings.class);
                startActivity(nextActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
