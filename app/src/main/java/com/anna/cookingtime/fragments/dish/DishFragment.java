package com.anna.cookingtime.fragments.dish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anna.cookingtime.R;
import com.anna.cookingtime.activities.MainActivity;
import com.anna.cookingtime.adapters.DishViewPagerAdapter;
import com.anna.cookingtime.fragments.BaseFragment;
import com.anna.cookingtime.models.Dish;
import com.anna.cookingtime.utils.Constants;
import com.anna.cookingtime.utils.Utils;
import com.anna.cookingtime.views.BetterImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iva on 17.03.17.
 */

public class DishFragment extends BaseFragment {
    public static final String TAG = "DishFragment";
    private static final int CACHED_FRAGMENT_COUNT = 1;

    @BindView(R.id.dishTabLayout)
    TabLayout dishTabLayout;
    @BindView(R.id.dishViewPager)
    ViewPager dishViewPager;
    @BindView(R.id.dishPhoto)
    BetterImageView dishPhoto;
    @BindView(R.id.dishDifficult)
    TextView dishDifficult;
    @BindView(R.id.dishCalories)
    TextView dishCalories;
    @BindView(R.id.dishTime)
    TextView dishTime;
    @BindView(R.id.dishProgressBar)
    ProgressBar dishProgressBar;

    private TabLayout.Tab recipe;
    private TabLayout.Tab ingredients;

    protected Dish dish;

    private long idDish;
    private RefreshData detailsListener, ingredientsListener;
    private Menu mainMenu;

    public static DishFragment newInstance(long id) {

        Bundle args = new Bundle();

        DishFragment fragment = new DishFragment();
        args.putLong(Constants.DISH_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dish, container, false);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // empty click caused adding fragments not replacing!!!
            }
        });

        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Utils.setRootFragment(true);

        idDish = getArguments().getLong(Constants.DISH_ID);

        dish = (Dish) cacheManager.getObjectFromCache(TAG + idDish);

        getDish(idDish);

        PagerAdapter mainViewPagerAdapter = new DishViewPagerAdapter(
                getChildFragmentManager()
        );
        dishViewPager.setOffscreenPageLimit(CACHED_FRAGMENT_COUNT);
        dishViewPager.setAdapter(mainViewPagerAdapter);

        recipe = dishTabLayout.newTab();
        ingredients = dishTabLayout.newTab();

        recipe.setText(getString(R.string.recipe));
        ingredients.setText(getString(R.string.ingredients));

        dishTabLayout.addTab(recipe, Constants.RECIPE);
        dishTabLayout.addTab(ingredients, Constants.INGREDIENTS);

        dishTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                dishViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        dishViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(dishTabLayout));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.favorite_menu, menu);
        mainMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem favoriteItem = menu.findItem(R.id.action_favorite);
        favoriteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (cacheManager.isFavorite(TAG + idDish)) {
                    cacheManager.addFavorite(TAG + idDish, null);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_favorite, null));
                } else {
                    cacheManager.addFavorite(TAG + idDish, dish);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_select, null));
                }
                return true;
            }
        });

    }

    private void getDish(final long id) {
        getCalls().getDish(id).enqueue(new Callback<Dish>() {
            @Override
            public void onResponse(Call<Dish> call, Response<Dish> response) {
                DishFragment.this.dish = response.body();
                if (dish != null) {
                    ((MainActivity) getActivity()).getSupportActionBar()
                            .setTitle(dish.getName());
                    detailsListener.onRefresh(dish);
                    ingredientsListener.onRefresh(dish);
                    dishPhoto.loadImage(dish.getImageUrl(), dishProgressBar);
                    dishCalories.setText(String.valueOf(dish.getCalories()));
                    dishTime.setText(String.valueOf(dish.getCookingTime()));
                    setDifficultyLevel(dish);
                    if (cacheManager.isFavorite(TAG + idDish)) {
                        mainMenu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_favorite_select, null));
                    }
                    cacheManager.putOrUpdateCache(TAG + idDish, dish);
                }
            }

            @Override
            public void onFailure(Call<Dish> call, Throwable t) {

            }
        });
    }

    private void setDifficultyLevel(Dish dish) {
        switch (dish.getDifficultyLevel()) {
            case 1:
                dishDifficult.setText(getString(R.string.easy));
                break;
            case 2:
                dishDifficult.setText(getString(R.string.medium));
                break;
            case 3:
                dishDifficult.setText(getString(R.string.hard));
                break;
        }
    }

    protected void setDetailsListener(RefreshData refreshData) {
        this.detailsListener = refreshData;
    }

    protected void setIngredientsListener(RefreshData refreshData) {
        this.ingredientsListener = refreshData;
    }

    interface RefreshData {
        void onRefresh(Dish dish);
    }
}
