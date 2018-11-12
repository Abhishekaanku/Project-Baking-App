package com.exmaple.udacity.bakershop.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.pojo.Ingredient;
import com.exmaple.udacity.bakershop.ui.MainActivity;
import com.exmaple.udacity.bakershop.ui.RecipeActivity;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WidgetListViewRemoteService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingWidgetlistViewFactory(this.getApplicationContext());
    }
}
class BakingWidgetlistViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Ingredient> ingredientArrayList;
    private Context mContext;
    private int recipeId;
    private String recipeName;


    BakingWidgetlistViewFactory(Context context) {
        mContext=context;
    }
    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        recipeId= PrefUtils.getWidgetRecipe(mContext);
        String recipeJsonString=RecipeUtils.readJsonFromFile(mContext);
        try {
            JSONArray jsonArray=new JSONArray(recipeJsonString);
            for(int i=0;i<jsonArray.length();++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                if(jsonObject.getInt("id")==recipeId) {
                    ingredientArrayList=RecipeUtils.loadIngredients(jsonObject.getJSONArray("ingredients"));
                    recipeName=jsonObject.getString("name");
                    break;
                }
            }
        }
        catch(JSONException  e) {
            ingredientArrayList=null;
        }
    }

    @Override
    public void onDestroy() {
        if(ingredientArrayList!=null) {
            ingredientArrayList.clear();
            ingredientArrayList=null;
        }
    }

    @Override
    public int getCount() {
        if(ingredientArrayList!=null) {
            return ingredientArrayList.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(ingredientArrayList==null) {
            return null;
        }
        Ingredient ingredient=ingredientArrayList.get(position);
        RemoteViews remoteViews=new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_item);
        remoteViews.setTextViewText(R.id.textViewIngredientName1,RecipeUtils.formatIngredientName(ingredient.getIngredientName()));
        String measure=RecipeUtils.formatIngredient(ingredient.getMeasure(),ingredient.getQuantity(),mContext);
        remoteViews.setTextViewText(R.id.textViewIngredientMeasure1,measure);
        Bundle bundle=new Bundle();
        bundle.putInt(RecipeActivity.RECIPE_ID_EXTRA,recipeId);
        bundle.putString(MainActivity.RECIPE_NAME,recipeName);
        Intent intent=new Intent();
        intent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(R.id.textViewIngredientName1,intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
