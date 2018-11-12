package com.exmaple.udacity.bakershop.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.pojo.Ingredient;
import com.exmaple.udacity.bakershop.pojo.Procedure;
import com.exmaple.udacity.bakershop.pojo.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RecipeUtils {
    public static HashMap<Integer,JSONObject> loadJSON(String recipeJson) {
        HashMap<Integer,JSONObject> jsonHashMap=new HashMap<>();
        try {
            JSONArray jsonArray=new JSONArray(recipeJson);
            for(int i=0;i<jsonArray.length();++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                jsonHashMap.put(jsonObject.getInt("id"),jsonObject);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonHashMap;
    }
    public static ArrayList<Ingredient> loadIngredients(JSONArray ingredientJSONArray) {
        ArrayList<Ingredient> ingredientArrayList=new ArrayList<>();
        try {
            Ingredient ingredient;
            for(int i=0;i<ingredientJSONArray.length();++i) {
                ingredient=new Ingredient();
                JSONObject jsonIngredient=ingredientJSONArray.getJSONObject(i);
                ingredient.setQuantity((float)jsonIngredient.getDouble("quantity"));
                ingredient.setIngredientName(jsonIngredient.getString("ingredient"));
                ingredient.setMeasure(jsonIngredient.getString("measure"));
                ingredientArrayList.add(ingredient);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return ingredientArrayList;
    }

    public static ArrayList<Procedure> loadSteps(JSONArray stepsJSONArray) {
        ArrayList<Procedure> procedureArrayList=new ArrayList<>();
        try {
            Procedure procedure;
            for(int i=0;i<stepsJSONArray.length();++i) {
                procedure=new Procedure();
                JSONObject jsonProcedure=stepsJSONArray.getJSONObject(i);
                procedure.setId(jsonProcedure.getInt("id"));
                procedure.setDescription(jsonProcedure.getString("description"));
                procedure.setShortDescription(jsonProcedure.getString("shortDescription"));
                procedure.setVideoURL(jsonProcedure.getString("videoURL"));
                procedure.setThumbnailURL(jsonProcedure.getString("thumbnailURL"));
                procedureArrayList.add(procedure);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return procedureArrayList;
    }

    public static Recipe getRecipe(JSONObject recipeJSON) {
        Recipe recipe=new Recipe();
        try {
            recipe.setId(recipeJSON.getInt("id"));
            recipe.setName(recipeJSON.getString("name"));
            recipe.setServings(recipeJSON.getInt("servings"));
            recipe.setImageURL(recipeJSON.getString("image"));
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return recipe;
    }


    public static boolean onIsNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    public static String readJsonFromFile(Context context) {
        StringBuilder recipeJsonString=new StringBuilder();
        try {
            InputStream inputStream = context.openFileInput("jsonRecipe.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    recipeJsonString.append(receiveString);
                }
                inputStream.close();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return recipeJsonString.toString();
    }

    public static ArrayList<Recipe> sortRecipe(String order,Context context,
                                               HashMap<Integer,Recipe> recipeHashmap) {

        List<Integer> keySet=new ArrayList<>(recipeHashmap.keySet());

        if(order.equals("ASC")) {
            Collections.sort(keySet,new CompareAsc(context));
        }
        else {
            Collections.sort(keySet, new CompareDesc(context));
        }
        ArrayList<Recipe> recipes=new ArrayList<>();
        for(Integer key:keySet) {
            recipes.add(recipeHashmap.get(key));
        }
        return recipes;
    }

    static class CompareAsc implements Comparator<Integer> {
        Context context;
        CompareAsc(Context context) {
            this.context=context;
        }
        @Override
        public int compare(Integer recipeId, Integer t1) {
            int numViewRecipe=PrefUtils.getNumViewed(recipeId,context);
            int numViewt1=PrefUtils.getNumViewed(t1,context);
            if(numViewRecipe<numViewt1) {
                return -1;
            }
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
    static class CompareDesc implements Comparator<Integer> {
        Context context;
        CompareDesc(Context context) {
            this.context=context;
        }
        @Override
        public int compare(Integer recipeId, Integer t1) {
            int numViewRecipe=PrefUtils.getNumViewed(recipeId,context);
            int numViewt1=PrefUtils.getNumViewed(t1,context);
            if(numViewRecipe>numViewt1) {
                return -1;
            }
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
    public static String formatIngredient(String measure,float quantity,Context context) {
        String measureFormatted;
        Resources resources=context.getResources();
        switch(measure) {
            case "CUP":
                measureFormatted=resources.getQuantityString(R.plurals.cupMeasure,(int)quantity);
                break;
            case "K":
                measureFormatted=resources.getString(R.string.KgMeasure);
                break;
            case "G":
                measureFormatted=resources.getString(R.string.gramMeasure);
                break;
            case "UNIT":
                measureFormatted=resources.getQuantityString(R.plurals.unitMeasure,(int)quantity);
                break;
            case "TBLSP":
                measureFormatted=resources.getQuantityString(R.plurals.TBLSPMeasure,(int)quantity);
                break;
            case "TSP":
                measureFormatted=resources.getQuantityString(R.plurals.TSPMeasure,(int)quantity);
                break;
            case "OZ":
                measureFormatted=resources.getString(R.string.ounceMeasure);
                break;
            default:
                measureFormatted=measure;
        }
        return quantity+"\t\t"+measureFormatted;
    }
    public static String formatIngredientName(String ingredientName) {
        return ingredientName.substring(0,1).toUpperCase()+ingredientName.substring(1);
    }

    public static boolean checkFileExists(Context context) {
        try {
            InputStream inputStream = context.openFileInput("jsonRecipe.txt");
            inputStream.close();
        }
        catch(FileNotFoundException e) {
            return false;
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }

    public static void saveFile(String jsonString,Context context) {
        try {
            FileOutputStream fileOutputStream=context.openFileOutput("jsonRecipe.txt",Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);
            osw.write(jsonString);
            fileOutputStream.flush();
            osw.flush();
            fileOutputStream.close();
            osw.close();

        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager()
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager()
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

}
