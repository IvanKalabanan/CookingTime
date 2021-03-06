package com.anna.cookingtime.utils;

import android.content.Context;
import android.util.Log;

import com.anna.cookingtime.CookingTimeApp;
import com.anna.cookingtime.models.Dish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 03.07.2016.
 */

public class CacheManager {

    public static final String SAVED_USERS_MAP = "data_cache.map";
    public static final String SAVED_FAVORITES_MAP = "favorite_cache.map";
    private static final String TAG = "CacheManager";

    private Map<String, Object> cachedMap;
    private Map<String, Dish> favorites;
    private Map<String, Boolean> refreshFragmentMap;
    private static CacheManager instance;


    private CacheManager() {
        cachedMap = java.util.Collections.synchronizedMap(new HashMap<String, Object>());
        favorites = java.util.Collections.synchronizedMap(new HashMap<String, Dish>());
        refreshFragmentMap = new HashMap<>();
    }

    public boolean isFavorite (String dish) {
        return getFavorites().containsKey(dish);
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    public void putOrUpdateCache(final String key, final Object value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCachedMap().put(key, value);
            }
        }).start();
    }

    public void addFavorite(final String key, final Dish value) {
        if (value == null) {
            getFavorites().remove(key);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                    getFavorites().put(key, value);
            }
        }).start();
    }

    public Object getObjectFromCache(String key) {
        Log.d(TAG, "getFromCache: object: " + getCachedMap().get(key));
        return getCachedMap().get(key);
    }

    private Map<String, Object> getCachedMap() {
        if (cachedMap.isEmpty()) {
            try {

                File cachedDateFile = new File(
                        CookingTimeApp.getAppContext()
                                .getFilesDir()
                                + File.separator
                                + SAVED_USERS_MAP
                );
                if (!cachedDateFile.exists()) {
                    saveCachedMapOnDeviceStorage();
                }

                FileInputStream fileInputStream = new FileInputStream(
                        cachedDateFile
                );
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                cachedMap = (Map<String, Object>) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException | ClassCastException e) {
                e.printStackTrace();
                Log.d(TAG, "getCachedMap: Exeption!: " + e);
            }
        }
        Log.d(TAG, "getCachedMap: Map: " + cachedMap);
        return cachedMap;
    }

    public ArrayList<Dish> getFavoritesList() {
        return new ArrayList<Dish>(favorites.values());
    }

    private Map<String, Dish> getFavorites() {
        if (favorites.isEmpty()) {
            try {

                File cachedDateFile = new File(
                        CookingTimeApp.getAppContext()
                                .getFilesDir()
                                + File.separator
                                + SAVED_FAVORITES_MAP
                );
                if (!cachedDateFile.exists()) {
                    saveCachedMapOnDeviceStorage();
                }

                FileInputStream fileInputStream = new FileInputStream(
                        cachedDateFile
                );
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                favorites = (Map<String, Dish>) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException | ClassCastException e) {
                e.printStackTrace();
                Log.d(TAG, "getCachedMap: Exeption!: " + e);
            }
        }
        return favorites;
    }

    public boolean saveCachedMapOnDeviceStorage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fos = CookingTimeApp.getAppContext().openFileOutput(SAVED_USERS_MAP, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(cachedMap);
                    oos.close();
                    Log.d(TAG, "saveCachedMapOnDeviceStorage: success");
                } catch (IOException e) {
                    Log.d(TAG, "saveCachedMapOnDeviceStorage: IOException!: " + e);
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    public boolean saveFavoritesMapOnDeviceStorage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fos = CookingTimeApp.getAppContext().openFileOutput(SAVED_FAVORITES_MAP, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(favorites);
                    oos.close();
                    Log.d(TAG, "saveCachedMapOnDeviceStorage: success");
                } catch (IOException e) {
                    Log.d(TAG, "saveCachedMapOnDeviceStorage: IOException!: " + e);
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    public Map<String, Boolean> getRefreshFragmentMap() {
        return refreshFragmentMap;
    }

    boolean deleteAllCache() {
        boolean isRemoved;
        File fileInputStream = new File(
                CookingTimeApp.getAppContext()
                        .getFilesDir()
                        + File.separator
                        + SAVED_USERS_MAP
        );
        isRemoved = fileInputStream.delete();
        cachedMap = java.util.Collections.synchronizedMap(new HashMap<String, Object>());
        refreshFragmentMap = new HashMap<>();
        Log.d(TAG, "deleteAllCache: isRemoved: " + isRemoved);
        return isRemoved;
    }

}
