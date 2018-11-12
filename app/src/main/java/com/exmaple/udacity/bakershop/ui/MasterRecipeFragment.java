package com.exmaple.udacity.bakershop.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exmaple.udacity.bakershop.RecipeViewModel;
import com.exmaple.udacity.bakershop.pojo.Ingredient;
import com.exmaple.udacity.bakershop.pojo.Procedure;
import com.exmaple.udacity.bakershop.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MasterRecipeFragment extends Fragment {
    private static final String ARG_RECIPE_ID = "argRecipeID";

    @BindView(R.id.masterRecipeList)
    RecyclerView masterRecyclerView;

    RecipeMasterViewAdapter.RecipeDataClickListener recipeDataClickListener;

    RecipeViewModel recipeViewModel;

    private int recipeId;

    private OnFragmentInteractionListener mListener;

    public MasterRecipeFragment() {}


    public static MasterRecipeFragment newInstance(int recipeId) {
        MasterRecipeFragment fragment = new MasterRecipeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getInt(ARG_RECIPE_ID);
        }
        else {
            if(savedInstanceState!=null) {
                if(savedInstanceState.containsKey(ARG_RECIPE_ID)) {
                    recipeId=savedInstanceState.getInt(ARG_RECIPE_ID);
                }
            }
        }
        recipeViewModel=ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ARG_RECIPE_ID,recipeId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_master_recipe, container, false);
        ButterKnife.bind(this,view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        ArrayList<Ingredient> ingredients= recipeViewModel.getIngredients(recipeId);
        ArrayList<Procedure> procedures=recipeViewModel.getProcedures(recipeId);
        RecipeMasterViewAdapter recipeMasterViewAdapter=new RecipeMasterViewAdapter(getContext(),
                procedures,ingredients,recipeDataClickListener);
        masterRecyclerView.setLayoutManager(linearLayoutManager);
        masterRecyclerView.setAdapter(recipeMasterViewAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeMasterViewAdapter.RecipeDataClickListener) {
            recipeDataClickListener = (RecipeMasterViewAdapter.RecipeDataClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
