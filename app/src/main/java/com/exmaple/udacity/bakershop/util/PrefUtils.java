package com.exmaple.udacity.bakershop.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.exmaple.udacity.bakershop.R;

public class PrefUtils {
    private static final int DEFAULT_COUNT=0;

    public static int getNumViewed(int recipeID, Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.
                        getString(R.string.ViewPreference), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Integer.toString(recipeID),DEFAULT_COUNT);
    }

    public static void updateViews(int recipeID,Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(context
                .getString(R.string.ViewPreference),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int numViews=sharedPreferences.getInt(Integer.toString(recipeID),DEFAULT_COUNT);
        editor.putInt(Integer.toString(recipeID),1+numViews);
        editor.apply();
    }

    public static void updateWidgetRecipe(int recipeId,Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.ViewPreference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.WidgetRecipeKey), recipeId);
        editor.commit();
    }

    public static int getWidgetRecipe(Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(
                context.getString(R.string.ViewPreference),Context.MODE_PRIVATE
        );
        return sharedPreferences.getInt(context.getString(R.string.WidgetRecipeKey),0);
    }
    public static void updateIngrdientListParam(boolean isVisible,Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.ViewPreference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.ingredientListDisplayParam),isVisible);
        editor.apply();
    }

    public static boolean getIngrdientListParam(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.ViewPreference), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.ingredientListDisplayParam),false);
    }
}
