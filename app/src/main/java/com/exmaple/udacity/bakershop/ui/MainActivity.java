package com.exmaple.udacity.bakershop.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.RecipeViewModel;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;
import com.exmaple.udacity.bakershop.retro.RecipeRetroController;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.CardClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,Callback<String>
{
    @BindView(R.id.recipeRecyclerView)
    RecyclerView mRecipeListView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.TextViewNetworkErrorMessage)
    TextView networkErrorTextView;
    @BindView(R.id.mainActivityLayout)
    FrameLayout mainActivityView;
    RecipeAdapter recipeAdapter=null;
    SharedPreferences sharedPreferences;
    RecipeViewModel recipeViewModel;
    public static final String RECIPE_NAME="recipe_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        RecipeRetroController.loadRecipeJson(this);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    private void showRecipeFromJson() {
        recipeViewModel= ViewModelProviders.of(this).get(RecipeViewModel.class);
        Resources resources=getResources();
        if(resources.getBoolean(R.bool.isTablet)) {
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
            mRecipeListView.setLayoutManager(gridLayoutManager);
        }
        else if(resources.getBoolean(R.bool.isRotated)) {
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
            mRecipeListView.setLayoutManager(gridLayoutManager);
        }
        else {
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
            mRecipeListView.setLayoutManager(linearLayoutManager);
        }
        recipeAdapter=new RecipeAdapter(this,this);
        mRecipeListView.setAdapter(recipeAdapter);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        onSharedPreferenceChanged(sharedPreferences,getString(R.string.prefSortkey));
        onSharedPreferenceChanged(sharedPreferences,getString(R.string.prefCrdColorKey));
    }

    @Override
    public void onCardClick(int id,String recipeName) {
        PrefUtils.updateViews(id,this);
        recipeAdapter.notifyDataSetChanged();
        Intent intent=new Intent(this,RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID_EXTRA,id);
        intent.putExtra(RECIPE_NAME,recipeName);
        startActivity(intent);
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        String recipeJsonString;
        progressBar.setVisibility(View.GONE);
        networkErrorTextView.setVisibility(View.INVISIBLE);
        if(response.isSuccessful()) {
            recipeJsonString=response.body();
            RecipeUtils.saveFile(recipeJsonString,this);
            showRecipeFromJson();
        }
        else {
            Log.d("RetroError",response.message());
            if(RecipeUtils.checkFileExists(this)){
                networkErrorTextView.setVisibility(View.INVISIBLE);
                showRecipeFromJson();
            }
            else {
                networkErrorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        progressBar.setVisibility(View.GONE);
        if(RecipeUtils.checkFileExists(this)) {
            showRecipeFromJson();
        }
        else {
            networkErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID=item.getItemId();
        switch (itemID) {
            case R.id.menuRefresh:
                RecipeRetroController.loadRecipeJson(this);
                break;
            case R.id.menuSetting:
                Intent intent=new Intent(this,SettingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.prefCrdColorKey))) {
            String color=sharedPreferences.getString(key,getString(R.string.prefCardBlueValue));
            if(color.equals(getString(R.string.prefCardBlueValue))) {
                mainActivityView.setBackgroundResource(R.drawable.blue_background);
            }
            else if(color.equals(getString(R.string.prefCardRedValue))) {
                mainActivityView.setBackgroundResource(R.drawable.red_background);
            }
            else  {
                mainActivityView.setBackgroundResource(R.drawable.green_background);
            }
        }
        else if(key.equals(getString(R.string.prefSortkey))) {
            String sortOrder=sharedPreferences.getString(key,getString(R.string.prefRecipeNoSortValue));
            if(sortOrder.equals(getString(R.string.prefRecipeAscValue))) {
                recipeAdapter.setRecipes(recipeViewModel.getAscSortedRecipeArrayList());
            }
            else if(sortOrder.equals(getString(R.string.prefRecipeDescValue))) {
                recipeAdapter.setRecipes(recipeViewModel.getDescSortedRecipeArrayList());
            }
            else {
                recipeAdapter.setRecipes(recipeViewModel.getRecipes());
            }
        }
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

}
