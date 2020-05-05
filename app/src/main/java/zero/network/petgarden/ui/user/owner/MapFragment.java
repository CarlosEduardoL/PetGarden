package zero.network.petgarden.ui.user.owner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import zero.network.petgarden.R;
import zero.network.petgarden.model.entity.Sitter;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker markerPosActual;
    private Location locationActual;
    private OwnerActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);
        return rootView;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.setMyLocationEnabled(true);

        initMapLocation();

    }

    @SuppressLint("MissingPermission")
    public void initMapLocation(){

        LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        //Solicitar actualizaciones de posicion
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 2, this);

        //Llevar a la ultima localizacion conocida
        Location last = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationActual = last;
        LatLng pos = new LatLng(last.getLatitude(), last.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
        markerPosActual = mMap.addMarker(  new MarkerOptions().position(pos).title("Yo").snippet("Mi ubicación")  );

        //Actualizar la ubicación del dueño sólo al inicio
        activity = (OwnerActivity)getActivity();
        activity.updateOwnerLocation(locationActual);


    }

    public synchronized void addSittersMarkers(ArrayList<Sitter> sitters){
        LatLng pos = null;

        for(Sitter sitter: sitters){

            //Si la distancia es menor que n, ponga los marcadores en el mapa

            pos = new LatLng(sitter.getLocation().getLat(),sitter.getLocation().getLongitude());
            Log.e(">>>",""+sitter.getLocation().getLat());
            mMap.addMarker(new MarkerOptions().position(pos).title(sitter.getName()).snippet("Mi ubicación"));
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        locationActual = location;
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        markerPosActual.setPosition(  pos  );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

        //REMOVER. Se necesita llamar solamente cuando se cree la activity

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void addLocations(){

    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public Marker getMarkerPosActual() {
        return markerPosActual;
    }

    public void setMarkerPosActual(Marker markerPosActual) {
        this.markerPosActual = markerPosActual;
    }

    public Location getLocationActual() {
        return locationActual;
    }

    public void setLocationActual(Location locationActual) {
        this.locationActual = locationActual;
    }
}
