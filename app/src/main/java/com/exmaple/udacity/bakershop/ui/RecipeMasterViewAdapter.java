package com.exmaple.udacity.bakershop.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.exmaple.udacity.bakershop.pojo.Ingredient;
import com.exmaple.udacity.bakershop.pojo.Procedure;
import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeMasterViewAdapter extends RecyclerView.Adapter<RecipeMasterViewAdapter.RecipeMasterViewHolder>
{
    private Context mContext;
    private ArrayList<Procedure> procedureArrayList;
    private ArrayList<Ingredient> ingredientArrayList;
    private RecipeDataClickListener recipeDataClickListener;
    public RecipeMasterViewAdapter(Context context,ArrayList<Procedure> procedures,
                                   ArrayList<Ingredient> ingredients,RecipeDataClickListener recipeDataClickListener) {
        this.mContext=context;
        this.procedureArrayList=procedures;
        this.ingredientArrayList=ingredients;
        this.recipeDataClickListener=recipeDataClickListener;

    }
    @NonNull
    @Override
    public RecipeMasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==0) {
            view= LayoutInflater.from(mContext).inflate(R.layout.recipe_ingredient,parent,false);
        }
        else {
            view=LayoutInflater.from(mContext).inflate(R.layout.recipe_steps,parent,false);
        }
        return new RecipeMasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeMasterViewHolder holder, int position) {
        if(getItemViewType(position)==0) {
            if(PrefUtils.getIngrdientListParam(mContext)) {
                holder.ingredientListView.setVisibility(View.VISIBLE);
                holder.imageviewExapndCollapse.setImageResource(R.drawable.ic_if_ic_expand_less);
            }
            else {
                holder.ingredientListView.setVisibility(View.GONE);
                holder.imageviewExapndCollapse.setImageResource(R.drawable.ic_if_ic_expand_more);
            }
            holder.imageviewExapndCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.ingredientListView.getVisibility()==View.GONE) {
                        PrefUtils.updateIngrdientListParam(true,mContext);
                        holder.ingredientListView.setVisibility(View.VISIBLE);
                        holder.imageviewExapndCollapse.setImageResource(R.drawable.ic_if_ic_expand_less);
                    }
                    else {
                        PrefUtils.updateIngrdientListParam(false,mContext);
                        holder.ingredientListView.setVisibility(View.GONE);
                        holder.imageviewExapndCollapse.setImageResource(R.drawable.ic_if_ic_expand_more);
                    }
                }
            });
            IngredientListAdapter ingredientListAdapter=new IngredientListAdapter(mContext);
            ingredientListAdapter.setIngredientList(ingredientArrayList);
            holder.ingredientListView.setAdapter(ingredientListAdapter);
            holder.ingredientListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action=motionEvent.getAction();
                    if(action==MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    else if(action==MotionEvent.ACTION_DOWN) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    view.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }
        else {
            holder.recipeStepName.setText(procedureArrayList.get(position-1).getShortDescription());
            String thumbNailUrl=procedureArrayList.get(position-1).getThumbnailURL();
            if(thumbNailUrl.length()>3 && !thumbNailUrl.endsWith(".mp4")) {
                Log.d("MUSIC","dnmbcxnm");
                Picasso.with(mContext)
                        .load(thumbNailUrl)
                        .fit()
                        .into(holder.recipeStepThumbNail);
            }
        }

    }

    @Override
    public int getItemCount() {
        if(procedureArrayList==null && ingredientArrayList==null) {
            return 0;
        }
        if(procedureArrayList==null) {
            return 1;
        }
        return 1+procedureArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0) {
            return 0;
        }
        return 1;
    }

    public void setData(ArrayList<Ingredient> ingredients,ArrayList<Procedure> procedures) {
        ingredientArrayList=ingredients;
        procedureArrayList=procedures;
        notifyDataSetChanged();
    }

    public interface RecipeDataClickListener {
        void onRecipeDataClck(int position);
    }

    class RecipeMasterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Nullable
        @BindView(R.id.textViewIngredientTitle)
        TextView ingredientTitle;

        @Nullable
        @BindView(R.id.listRecipeIngredientView)
        ListView ingredientListView;

        @Nullable
        @BindView(R.id.imageViewExpandCollapse)
        ImageView imageviewExapndCollapse;

        @Nullable
        @BindView(R.id.textViewStepName)
        TextView recipeStepName;

        @Nullable
        @BindView(R.id.imageViewThumbnail)
        ImageView recipeStepThumbNail;

        @Nullable
        @BindView(R.id.recipeIngredientsLayout) ConstraintLayout recipeIngredientLayout;

        @Nullable
        @BindView(R.id.recipeStepsLayout) ConstraintLayout recipeStepsLayout;

        private RecipeMasterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            String key=mContext.getString(R.string.prefCrdColorKey);
            if(ingredientListView!=null && mContext.getResources().getBoolean(R.bool.isRotated)) {
                ViewGroup.LayoutParams lp=ingredientListView.getLayoutParams();
                lp.height= (int)(0.5*RecipeUtils.getScreenHeight(mContext));
                ingredientListView.setLayoutParams(lp);
            }
            onSharedPreferenceChanged(sharedPreferences,key);
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            if(position!=0)
            recipeDataClickListener.onRecipeDataClck(position);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(mContext.getString(R.string.prefCrdColorKey))) {
                String color=sharedPreferences.getString(key,mContext.getString(R.string.prefCardBlueValue));
                if(color.equals(mContext.getString(R.string.prefCardBlueValue))) {
                    if(recipeIngredientLayout!=null) {
                        recipeIngredientLayout.setBackgroundResource(R.drawable.theme_blue);
                    }
                    if(recipeStepsLayout!=null) {
                        recipeStepsLayout.setBackgroundResource(R.drawable.theme_blue);
                    }
                    if(ingredientListView!=null) {
                        ingredientListView.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundBlue));
                    }

                }
                else if(color.equals(mContext.getString(R.string.prefCardRedValue))) {
                    if(recipeIngredientLayout!=null) {
                        recipeIngredientLayout.setBackgroundResource(R.drawable.theme_red);
                    }
                    if(recipeStepsLayout!=null) {
                        recipeStepsLayout.setBackgroundResource(R.drawable.theme_red);
                    }
                    if(ingredientListView!=null) {
                        ingredientListView.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundRed));
                    }
                }
                else  {
                    if(recipeIngredientLayout!=null) {
                        recipeIngredientLayout.setBackgroundResource(R.drawable.theme_green);
                    }
                    if(recipeStepsLayout!=null) {
                        recipeStepsLayout.setBackgroundResource(R.drawable.theme_green);
                    }
                    if(ingredientListView!=null) {
                        ingredientListView.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundGreen));
                    }
                }
            }
        }
    }
}
