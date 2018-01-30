package com.example.furmansd.osmmaps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
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
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        geoPoints.add(new GeoPoint(53.5165, 50.1779));
    }

    @Override
    protected void onStart() {
        super.onStart();
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setBuiltInZoomControls(true);

        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your applicatio
        mScaleBarOverlay.setScaleBarOffset(20, 10);
        map.getOverlays().add(mScaleBarOverlay);

        if (!((ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) == PackageManager.PERMISSION_GRANTED))) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission_group.LOCATION,
                            Manifest.permission_group.STORAGE
                    },
                    0);
        } else {
            cacheMap();
        }
    }

    private final ArrayList<GeoPoint> geoPoints = new ArrayList<>();

    private void cacheMap() {
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        final CacheManager manager = new CacheManager(map);

        manager.downloadAreaAsync(
                this,
                new BoundingBox(50.483908, 53.433165, 49.907340, 53.056807), //Samara
                10,
                19,
                new CacheManager.CacheManagerCallback() {
                    @Override
                    public void onTaskComplete() {
                        map.setMaxZoomLevel(19);
                        map.setMinZoomLevel(10);
                        map.getController().animateTo(geoPoints.get(0));
                        map.getController().setZoom(12);
                    }

                    private long prevVol;

                    @Override
                    public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                        Toast.makeText(MainActivity.this, String.valueOf(manager.currentCacheUsage()),
                                Toast.LENGTH_SHORT).show();
                        long curVol = manager.currentCacheUsage();
                        if (curVol > prevVol) {
                            Toast.makeText(MainActivity.this, String.valueOf(curVol - prevVol),
                                    Toast.LENGTH_SHORT).show();
                            prevVol = curVol;
                        }
                    }

                    @Override
                    public void downloadStarted() {
                        prevVol = manager.currentCacheUsage();
                    }

                    @Override
                    public void setPossibleTilesInArea(int total) {

                    }

                    @Override
                    public void onTaskFailed(int errors) {

                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.getController().animateTo(geoPoints.get(0));
        map.getController().setZoom(12);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            cacheMap();
        }
    }
}