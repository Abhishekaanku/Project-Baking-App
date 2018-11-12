package com.exmaple.udacity.bakershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.service.BakerWidgetIntentService;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailStepActivity extends AppCompatActivity implements ChildRecipeFragment.
        OnFragmentInteractionListener,SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener {
    public final static String RECIPE_ID_EXTRA="recipe_id";
    public final static String STEP_ID_EXTRA="step_id";
    private final static int ID_DEFAULT=-1;
    private int recipeID;
    private int stepID;
    ChildRecipeFragment childRecipeFragment;
    @BindView(R.id.recipeDetailView)
    ScrollView recipeDetailView;
    @BindView(R.id.buttonNextRecipeStep)
    Button nextButton;
    @BindView(R.id.buttonPrevRecipeStep)
    Button prevButton;
    String recipeName;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_step);
        ButterKnife.bind(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        String key=getString(R.string.prefCrdColorKey);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(sharedPreferences,key);
        Intent intent=getIntent();
        if(savedInstanceState==null) {
            if(intent.hasExtra(RECIPE_ID_EXTRA) && intent.hasExtra(STEP_ID_EXTRA)
                    && intent.hasExtra(MainActivity.RECIPE_NAME)) {
                recipeName = intent.getStringExtra(MainActivity.RECIPE_NAME);
                recipeID = intent.getIntExtra(RECIPE_ID_EXTRA, ID_DEFAULT);
                stepID = intent.getIntExtra(STEP_ID_EXTRA, ID_DEFAULT);
            }
        }
        else {
            recipeName=savedInstanceState.getString(MainActivity.RECIPE_NAME);
            recipeID=savedInstanceState.getInt(RECIPE_ID_EXTRA);
            stepID=savedInstanceState.getInt(STEP_ID_EXTRA);
        }
        if(savedInstanceState==null) {
            if (recipeID != ID_DEFAULT && stepID != ID_DEFAULT) {
                childRecipeFragment = ChildRecipeFragment.newInstance(recipeID, stepID);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentRecipeStepDetail, childRecipeFragment)
                        .commit();
            }
        }
        customiseActionBar(recipeName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MainActivity.RECIPE_NAME,recipeName);
        outState.putInt(RECIPE_ID_EXTRA,recipeID);
        outState.putInt(STEP_ID_EXTRA,stepID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.buttonNextRecipeStep) {
            stepID+=1;
            childRecipeFragment=ChildRecipeFragment.newInstance(recipeID,stepID);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentRecipeStepDetail,childRecipeFragment)
                    .commit();
        }
        else if(id==R.id.buttonPrevRecipeStep) {
            if(stepID-1>=0) {
                stepID-=1;
                childRecipeFragment=ChildRecipeFragment.newInstance(recipeID,stepID);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentRecipeStepDetail,childRecipeFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onIsNetworkConnected() {
        return RecipeUtils.onIsNetworkConnected(this);
    }

    private void customiseActionBar(String name) {
        setTitle(name);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        int orientation=((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getOrientation();
        if(orientation == Surface.ROTATION_90 || orientation==Surface.ROTATION_270) {
            actionBar.hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.prefCrdColorKey))) {
            String color=sharedPreferences.getString(key,getString(R.string.prefCardBlueValue));
            if(color.equals(getString(R.string.prefCardBlueValue))) {
               recipeDetailView.setBackgroundResource(R.drawable.blue_background);
                nextButton.setBackgroundResource(R.drawable.theme_blue);
                prevButton.setBackgroundResource(R.drawable.theme_blue);
            }
            else if(color.equals(getString(R.string.prefCardRedValue))) {
                recipeDetailView.setBackgroundResource(R.drawable.red_background);
                nextButton.setBackgroundResource(R.drawable.theme_red);
                prevButton.setBackgroundResource(R.drawable.theme_red);
            }
            else  {
                recipeDetailView.setBackgroundResource(R.drawable.green_background);
                nextButton.setBackgroundResource(R.drawable.theme_green);
                prevButton.setBackgroundResource(R.drawable.theme_green);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        switch (itemId) {
            case R.id.menuSetting:
                Intent intent=new Intent(this,SettingActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menuAddRecipeWidget:
                PrefUtils.updateWidgetRecipe(recipeID,this);
                BakerWidgetIntentService.startActionLoadRecipe(this,recipeName);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
