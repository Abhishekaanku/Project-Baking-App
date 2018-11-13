package com.exmaple.udacity.bakershop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.service.BakerWidgetIntentService;
import com.exmaple.udacity.bakershop.util.PrefUtils;
import com.exmaple.udacity.bakershop.util.RecipeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements RecipeMasterViewAdapter.RecipeDataClickListener,
        ChildRecipeFragment.OnFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String RECIPE_ID_EXTRA = "recipe_id";
    private final static int ID_DEFAULT = -1;
    private final static int STEP_DEFAULT = 0;
    private final static String STEP_ID_EXTRA ="step_id";
    private int recipeID;
    private String recipeName;
    private SharedPreferences sharedPreferences;
    private final String TAG = getClass().getSimpleName();

    @Nullable
    @BindView(R.id.recipeDetailTabletView)
    LinearLayout recipeDetailTabletView;

    ChildRecipeFragment childRecipeFragment=null;
    int stepId=STEP_DEFAULT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_ID_EXTRA) && intent.hasExtra(MainActivity.RECIPE_NAME)) {
            recipeID = intent.getIntExtra(RECIPE_ID_EXTRA, ID_DEFAULT);
            recipeName=intent.getStringExtra(MainActivity.RECIPE_NAME);
            setTitle(recipeName);
        }
        if(savedInstanceState==null) {
            MasterRecipeFragment masterRecipeFragment = MasterRecipeFragment.newInstance(recipeID);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMasterRecipeView, masterRecipeFragment)
                    .commit();
        }
        else {
            stepId=savedInstanceState.getInt(STEP_ID_EXTRA);
        }
        if(recipeDetailTabletView!=null) {
            if(getSupportFragmentManager().findFragmentByTag(TAG)==null) {
                childRecipeFragment=ChildRecipeFragment.newInstance(recipeID,stepId);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.childFragmentView,childRecipeFragment,TAG)
                        .commit();
            }
            String key=getString(R.string.prefCrdColorKey);
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(sharedPreferences,key);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MainActivity.RECIPE_NAME,recipeName);
        outState.putInt(STEP_ID_EXTRA,stepId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRecipeDataClck(int position) {
        if(recipeDetailTabletView!=null) {
            if(stepId!=position-1) {
                stepId=position-1;
                childRecipeFragment=ChildRecipeFragment.newInstance(recipeID,stepId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.childFragmentView,childRecipeFragment,TAG)
                        .commit();
            }
        }
        else {
            if (position > 0) {
                Intent intent = new Intent(this, RecipeDetailStepActivity.class);
                if (recipeID != ID_DEFAULT) {
                    intent.putExtra(RecipeDetailStepActivity.RECIPE_ID_EXTRA, recipeID);
                    intent.putExtra(RecipeDetailStepActivity.STEP_ID_EXTRA, position - 1);
                    intent.putExtra(MainActivity.RECIPE_NAME,recipeName);
                    startActivity(intent);
                }
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
    public boolean onIsNetworkConnected() {
        return RecipeUtils.onIsNetworkConnected(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(recipeDetailTabletView!=null) {
            if(key.equals(getString(R.string.prefCrdColorKey))) {
                String color=sharedPreferences.getString(key,getString(R.string.prefCardBlueValue));
                if(color.equals(getString(R.string.prefCardBlueValue))) {
                    recipeDetailTabletView.setBackgroundResource(R.drawable.blue_background);
                }
                else if(color.equals(getString(R.string.prefCardRedValue))) {
                    recipeDetailTabletView.setBackgroundResource(R.drawable.red_background);
                }
                else  {
                   recipeDetailTabletView.setBackgroundResource(R.drawable.green_background);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(sharedPreferences!=null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
        super.onDestroy();
    }
}
