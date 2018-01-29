package com.example.furmansd.osmmaps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private MapView map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);

        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your applicatio
        mScaleBarOverlay.setScaleBarOffset(20, 10);
        map.getOverlays().add(mScaleBarOverlay);
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        final CacheManager manager = new CacheManager(map);
        final ArrayList<GeoPoint> geoPoints = new ArrayList();
        geoPoints.add(new GeoPoint(53.2165, 50.1779));

        map.setMaxZoomLevel(19);
        map.setMinZoomLevel(10);
        map.getController().animateTo(geoPoints.get(0));
        map.getController().setZoom(12);

        manager.downloadAreaAsync(this, geoPoints, 10, 19, new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                Toast.makeText(MainActivity.this, "Загрузка окончена", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {

            }

            @Override
            public void downloadStarted() {

            }

            @Override
            public void setPossibleTilesInArea(int total) {

            }

            @Override
            public void onTaskFailed(int errors) {

            }
        });
    }


}