package de.danielb.abschluss.rezeptebuch.controller;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import de.danielb.abschluss.rezeptebuch.R;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 05.09.2018.
 */

public class RecipeAdapter extends BaseAdapter {
    AppCompatActivity activityContext;
    List<Recipe> recipeList;

    public RecipeAdapter(AppCompatActivity activityContext, @NonNull List<Recipe> recipeList) {
        this.activityContext = activityContext;
        this.recipeList = recipeList;
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Object getItem(int i) {
        return recipeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return recipeList.get(i).get_id();
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        Recipe recipe = recipeList.get(i);
        if(recipe != null) {
            if (view == null) {
                view = activityContext.getLayoutInflater().inflate(R.layout.item_recipe_list, viewGroup, false);
            }
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvCategory = view.findViewById(R.id.tvCategory);
            //ImageButton ibtnThumb = view.findViewById(R.id.ibtnThumb);

            tvTitle.setText(recipe.getTitle());
            tvCategory.setText(recipe.getCategory());

        }
        return view;
    }

//    @Override
//    public boolean areAllItemsEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled(int i) {
//        return true;
//    }
//
//    @Override
//    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
//
//    }
//
//    @Override
//    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
//
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//

//
//    @Override
//    public int getItemViewType(int i) {
//        return IGNORE_ITEM_VIEW_TYPE;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return recipeList.isEmpty();
//    }
}
