package com.exmaple.udacity.bakershop;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;

import com.exmaple.udacity.bakershop.pojo.Ingredient;
import com.exmaple.udacity.bakershop.pojo.Procedure;
import com.exmaple.udacity.bakershop.pojo.Recipe;
import com.exmaple.udacity.bakershop.util.RecipeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeViewModel extends AndroidViewModel {
    private Application app;
    private HashMap<Integer,JSONObject> jsonHashMap=null;
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Recipe> ascSortedRecipeArrayList;
    private ArrayList<Recipe> descSortedRecipeArrayList;
    private HashMap<Integer,Recipe> recipeHashMap=new HashMap<>();
    private HashMap<Integer,ArrayList<Ingredient>> ingredientHashMap=new HashMap<>();
    private HashMap<Integer,ArrayList<Procedure>> procedureHashMap=new HashMap<>();

    public RecipeViewModel(Application application) {
        super(application);
        app=application;
        if(jsonHashMap==null) {
            String recipeJson=RecipeUtils.readJsonFromFile(application);
            if(recipeJson.length()!=0) {
                jsonHashMap=RecipeUtils.loadJSON(recipeJson);
            }
        }
        for(Integer key:jsonHashMap.keySet()) {
            recipeHashMap.put(key,RecipeUtils.getRecipe(jsonHashMap.get(key)));
        }
    }

    public ArrayList<Recipe> getRecipes() {
        if(recipeArrayList==null) {
            recipeArrayList=new ArrayList<>();
            for(Integer key:recipeHashMap.keySet()) {
                recipeArrayList.add(RecipeUtils.getRecipe(jsonHashMap.get(key)));
            }
        }
        return recipeArrayList;
    }

    public Recipe getRecipe(int recipeId) {
        if(recipeHashMap.get(recipeId)==null) {
            recipeHashMap.put(recipeId,RecipeUtils.getRecipe(jsonHashMap.get(recipeId)));
        }
        return recipeHashMap.get(recipeId);
    }

    public ArrayList<Recipe> getAscSortedRecipeArrayList() {
        if(ascSortedRecipeArrayList==null) {
            ascSortedRecipeArrayList=RecipeUtils.sortRecipe("ASC",app,recipeHashMap);
        }
        return ascSortedRecipeArrayList;
    }

    public ArrayList<Recipe> getDescSortedRecipeArrayList() {
        if(descSortedRecipeArrayList==null) {
            descSortedRecipeArrayList=RecipeUtils.sortRecipe("DESC",app,recipeHashMap);
        }
        return descSortedRecipeArrayList;
    }

    public ArrayList<Ingredient> getIngredients(int recipeId) {
        if(ingredientHashMap.get(recipeId)==null) {
            try {
                JSONArray ingredientJsonArray=jsonHashMap.get(recipeId).getJSONArray("ingredients");
                ingredientHashMap.put(recipeId,RecipeUtils.loadIngredients(ingredientJsonArray));
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return ingredientHashMap.get(recipeId);
    }

    public ArrayList<Procedure> getProcedures(int recipeId) {
        if(procedureHashMap.get(recipeId)==null) {
            try {
                JSONArray stepsJsonArray=jsonHashMap.get(recipeId).getJSONArray("steps");
                procedureHashMap.put(recipeId,RecipeUtils.loadSteps(stepsJsonArray));
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return procedureHashMap.get(recipeId);
    }
}
