package com.exmaple.udacity.bakershop.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.exmaple.udacity.bakershop.pojo.Ingredient;
import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.util.RecipeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> {
    private Context mContext;
    @BindView(R.id.textViewIngredientName)TextView ingredientNameView;
    @BindView(R.id.textViewIngredientMeasure) TextView ingredientMeasureView;
    @BindView(R.id.recipeIngredientItemLayout)
    ConstraintLayout recipeIngredientItemLayout;

    public IngredientListAdapter(Context context) {
        super(context,0);
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.recipe_ingredient_item,
                    parent,false);
        }
        ButterKnife.bind(this,convertView);
        String key=mContext.getString(R.string.prefCrdColorKey);
        Ingredient ingredient=getItem(position);
        if(ingredient!=null) {
            ingredientNameView.setText(RecipeUtils.formatIngredientName(ingredient.getIngredientName()));
            String textMaasure= RecipeUtils.formatIngredient(ingredient.getMeasure(),ingredient.getQuantity(),mContext);
            ingredientMeasureView.setText(textMaasure);
        }
        return convertView;
    }
    public void setIngredientList(ArrayList<Ingredient> ingredientArrayList) {
        clear();
        if(ingredientArrayList!=null) {
            addAll(ingredientArrayList);
        }
        notifyDataSetChanged();
    }

}
