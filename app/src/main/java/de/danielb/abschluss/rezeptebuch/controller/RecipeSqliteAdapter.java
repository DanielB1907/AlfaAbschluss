package de.danielb.abschluss.rezeptebuch.controller;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.danielb.abschluss.rezeptebuch.R;
import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 05.09.2018.
 */

public class RecipeSqliteAdapter extends BaseAdapter {
    Context context;
    List<Recipe> recipeSqliteList;

    public RecipeSqliteAdapter(Context context, @NonNull List<Recipe> recipeSqliteList) {
        this.context = context;
        this.recipeSqliteList = recipeSqliteList;
    }

    @Override
    public int getCount() {
        return recipeSqliteList != null ? recipeSqliteList.size() : -1;
    }

    @Override
    public Object getItem(int i) {
        return recipeSqliteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return recipeSqliteList.get(i).get_id();
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        Recipe recipe = recipeSqliteList.get(i);
        if(recipe != null) {
            if (view == null) {
                view = ((Activity) context).getLayoutInflater().inflate(R.layout.item_recipe_list, viewGroup, false);
            }

            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvCategory = view.findViewById(R.id.tvCategory);
            //ImageButton ibtnThumb = view.findViewById(R.id.ibtnThumb);

            tvTitle.setText(recipe.getTitle());
            tvCategory.setText(recipe.getCategory());

        }
        return view;
    }
}
