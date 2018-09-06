package de.danielb.abschluss.rezeptebuch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.danielb.abschluss.rezeptebuch.controller.RecipeSqliteHelper;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 05.09.2018.
 */

public class ActivityTestButtons extends AppCompatActivity implements View.OnClickListener{
    private Button btnTest1, btnTest2, btnTest3, btnTest4, btnTest5, btnTest6, btnTest7, btnTest8, btnTest9, btnTest10, btnTest11, btnTest12, btnTest13, btnTest14, btnTest15, btnTest16;

    RecipeSqliteHelper recipeSqliteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_buttons);
        connectViewControls();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTest1: // create new instance of RecipeSqliteHelper
                recipeSqliteHelper = new RecipeSqliteHelper(getApplicationContext(), "MyRecipes.db", null, 1);
                break;
            case R.id.btnTest2: //print path to database
                if (recipeSqliteHelper != null) {
                    recipeSqliteHelper.printPath();
                }
                break;
            case R.id.btnTest3: //add testdata to table
                if (recipeSqliteHelper != null) {
                    Recipe recipe;
                    for (int i = 0; i < 10; i++) {
                        recipe = new Recipe(0,
                                "Title " + i,
                                "Category " + i,
                                "Duration " + i,
                                "Ingredients " + i,
                                "Instructions " + i,
                                "PathToImage " + i);

                        Log.d(this.getClass().getSimpleName(), recipeSqliteHelper.insertRecipe(recipe).toString());
                    }
                }
                break;
            case R.id.btnTest4:
                break;
            case R.id.btnTest5:
                break;
            case R.id.btnTest6:
                break;
            case R.id.btnTest7:
                break;
            case R.id.btnTest8:
                break;
            case R.id.btnTest9:
                break;
            case R.id.btnTest10:
                break;
            case R.id.btnTest11:
                break;
            case R.id.btnTest12:
                break;
            case R.id.btnTest13:
                break;
            case R.id.btnTest14:
                break;
            case R.id.btnTest15:
                break;
            case R.id.btnTest16:
                break;
        }
    }

    private void connectViewControls() {
        btnTest1 = findViewById(R.id.btnTest1);
        btnTest1.setOnClickListener(this);

        btnTest2 = findViewById(R.id.btnTest2);
        btnTest2.setOnClickListener(this);

        btnTest3 = findViewById(R.id.btnTest3);
        btnTest3.setOnClickListener(this);

        btnTest4 = findViewById(R.id.btnTest4);
        btnTest4.setOnClickListener(this);

        btnTest5 = findViewById(R.id.btnTest5);
        btnTest5.setOnClickListener(this);

        btnTest6 = findViewById(R.id.btnTest6);
        btnTest6.setOnClickListener(this);

        btnTest7 = findViewById(R.id.btnTest7);
        btnTest7.setOnClickListener(this);

        btnTest8 = findViewById(R.id.btnTest8);
        btnTest8.setOnClickListener(this);

        btnTest9 = findViewById(R.id.btnTest9);
        btnTest9.setOnClickListener(this);

        btnTest10 = findViewById(R.id.btnTest10);
        btnTest10.setOnClickListener(this);

        btnTest11 = findViewById(R.id.btnTest11);
        btnTest11.setOnClickListener(this);

        btnTest12 = findViewById(R.id.btnTest12);
        btnTest12.setOnClickListener(this);

        btnTest13 = findViewById(R.id.btnTest13);
        btnTest13.setOnClickListener(this);

        btnTest14 = findViewById(R.id.btnTest14);
        btnTest14.setOnClickListener(this);

        btnTest15 = findViewById(R.id.btnTest15);
        btnTest15.setOnClickListener(this);

        btnTest16 = findViewById(R.id.btnTest16);
        btnTest16.setOnClickListener(this);
    }
}
