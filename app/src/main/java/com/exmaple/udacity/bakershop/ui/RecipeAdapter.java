package com.exmaple.udacity.bakershop.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exmaple.udacity.bakershop.pojo.Recipe;
import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>  {
    private Context mContext;
    private ArrayList<Recipe> recipes;
    private CardClickListener cardClickListener;
    interface CardClickListener {
        void onCardClick(int id, String recipeName);
    }

    public RecipeAdapter(Context context,CardClickListener cardClickListener) {
        this.mContext=context;
        this.cardClickListener=cardClickListener;
    }
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.recipe_item,viewGroup,false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {
        Recipe recipe=recipes.get(position);
        recipeViewHolder.recipeNameView.setText(recipe.getName());
        String recipeImage=recipe.getImageURL();
        if(recipeImage.length()!=0 && !recipeImage.endsWith(".mp4")) {
            Picasso.with(mContext)
                    .load(recipeImage)
                    .fit()
                    .into(recipeViewHolder.recipeImage);
        }
        String numViewString="Views: "+ PrefUtils.getNumViewed(recipe.getId(),mContext);
        recipeViewHolder.numViewed.setText(numViewString);
    }

    @Override
    public int getItemCount() {
        if(recipes==null)
        return 0;
        return recipes.size();
    }

    public void setRecipes(ArrayList<Recipe> recipeArrayList) {
        this.recipes=recipeArrayList;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener
    {

        @BindView(R.id.textViewRecipeName)
        TextView recipeNameView;

        @BindView(R.id.imageViewRecipeItem)
        ImageView recipeImage;

        @BindView(R.id.textViewViewed)
        TextView numViewed;

        @BindView(R.id.recipeItemLayout)
        ConstraintLayout constraintLayout;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            if(mContext.getResources().getBoolean(R.bool.isRotated)) {
                constraintLayout.setMinWidth(RecipeUtils.getScreenHeight(mContext)/2);
            }
            else if(mContext.getResources().getBoolean(R.bool.isTablet)) {
                constraintLayout.setMinWidth(RecipeUtils.getScreenWidth(mContext)/3);
            }
            itemView.setOnClickListener(this);
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
            String key=mContext.getString(R.string.prefCrdColorKey);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(sharedPreferences,key);
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            Recipe recipe=recipes.get(position);
            cardClickListener.onCardClick(recipe.getId(),recipe.getName());
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(mContext.getString(R.string.prefCrdColorKey))) {
                String color=sharedPreferences.getString(key,mContext.getString(R.string.prefCardBlueValue));
                if(color.equals(mContext.getString(R.string.prefCardBlueValue))) {
                    constraintLayout.setBackgroundResource(R.drawable.theme_blue);
                }
                else if(color.equals(mContext.getString(R.string.prefCardRedValue))) {
                    constraintLayout.setBackgroundResource(R.drawable.theme_red);
                }
                else  {
                    constraintLayout.setBackgroundResource(R.drawable.theme_green);
                }
            }
        }
    }
}
