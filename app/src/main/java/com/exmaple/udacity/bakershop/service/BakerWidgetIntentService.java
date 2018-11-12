package com.exmaple.udacity.bakershop.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;
import com.exmaple.udacity.bakershop.widget.BakerWidget;

import org.json.JSONArray;
import org.json.JSONException;

public class BakerWidgetIntentService extends IntentService{

    private static final String ACTION_LOAD_RECIPE = "com.exmaple.udacity.bakershop.widget.action.LOAD_RECIPE";
    private static final String ACTION_LOAD_DEFAULT_RECIPE="com.exmaple.udacity.bakershop.widget.action.LOAD_DEFAULT_RECIPE";
    private static final String EXTRA_RECIPE_NAME = "com.exmaple.udacity.bakershop.widget.extra.RECIPE_NAME";

    public BakerWidgetIntentService() {
        super("BakerWidgetIntentService");
    }



    public static void startActionLoadRecipe(Context context,String recipeName) {
        Intent intent = new Intent(context, BakerWidgetIntentService.class);
        intent.setAction(ACTION_LOAD_RECIPE);
        intent.putExtra(EXTRA_RECIPE_NAME,recipeName);
        context.startService(intent);
    }

    public static void startActionLoadDefaultRecipe(Context context) {
        Intent intent = new Intent(context, BakerWidgetIntentService.class);
        intent.setAction(ACTION_LOAD_DEFAULT_RECIPE);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD_RECIPE.equals(action)) {
                String recipeName=intent.getStringExtra(EXTRA_RECIPE_NAME);
                handleActionLoadRecipe(recipeName);
            }
            else if(ACTION_LOAD_DEFAULT_RECIPE.equals(intent.getAction())) {
                handleActionLoadDefaultrecipe();
            }
        }
    }

    //starts widget remoteviewservice to display most viewed recipe in widget(auto-started)

    private void handleActionLoadDefaultrecipe() {
        String jsonString=RecipeUtils.readJsonFromFile(this);
        if(jsonString.length()!=0) {
            int recipeId=0;
            int tempRecipeId;
            int tempNumView;
            String recipeName=null;
            int numViews=-1;
            try {
                JSONArray jsonArray=new JSONArray(jsonString);
                for(int i=0;i<jsonArray.length();++i) {
                    tempRecipeId=jsonArray.getJSONObject(i).getInt("id");
                    tempNumView=PrefUtils.getNumViewed(tempRecipeId,this);
                    if(tempNumView>numViews) {
                        numViews=tempNumView;
                        recipeId=tempRecipeId;
                        recipeName=jsonArray.getJSONObject(i).getString("name");
                        PrefUtils.updateWidgetRecipe(recipeId,this);
                    }
                }
                AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
                int []widgetIds=appWidgetManager.getAppWidgetIds(new ComponentName(this, BakerWidget.class));
                BakerWidget.onUpdate(recipeName,this,appWidgetManager,widgetIds);

            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //starts widgetremoteview service to display selected recipe in widget

    private void handleActionLoadRecipe(String recipeName) {
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
        int []widgetIds=appWidgetManager.getAppWidgetIds(new ComponentName(this, BakerWidget.class));
        BakerWidget.onUpdate(recipeName,this,appWidgetManager,widgetIds);
    }



}
