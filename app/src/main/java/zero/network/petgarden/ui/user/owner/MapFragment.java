package zero.network.petgarden.ui.user.owner;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import zero.network.petgarden.R;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker markerPosActual;
    private Location locationActual;
    private OwnerView ownerView;
    private boolean firstEntry;
    private LocationManager manager;

    public MapFragment(OwnerView owner){
        ownerView = owner;
    }

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
        manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //Llevar marker de posicion actual con zoom la primer vez
        Location last = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (last == null) {
            last = new Location("");
            last.setLongitude(ownerView.getOwner().getLocation().getLongitude());
            last.setLatitude(ownerView.getOwner().getLocation().getLat());
        }
        LatLng act = new LatLng(last.getLatitude(), last.getLongitude());
        markerPosActual = mMap.addMarker(  new MarkerOptions().position(act).title("Yo").snippet("Mi ubicación")  );
        firstEntry = true;
        locationActual = last;
        googleMap.addMarker(new MarkerOptions().position(act)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(act,18));

        mMap.setMyLocationEnabled(true);

        initMapLocation();

    }

    @SuppressLint("MissingPermission")
    public void initMapLocation(){


        //Solicitar actualizaciones de posicion
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, this);
        locationActual = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationActual.setLongitude(ownerView.getOwner().getLocation().getLongitude());
        locationActual.setLatitude(ownerView.getOwner().getLocation().getLat());

        //Actualizar la ubicación del dueño sólo al inicio
        ownerView.getOwner().setLocation(new zero.network.petgarden.model.entity.Location(locationActual.getLatitude(),locationActual.getLongitude()));
        ownerView.getOwner().saveInDB();

    }

    public void addSittersMarkers(List<Sitter> sitters){
        LatLng pos = null;

        for(Sitter sitter: sitters){


            pos = new LatLng(sitter.getLocation().getLat(),sitter.getLocation().getLongitude());
            Location tempLocation = new Location("");
            tempLocation.setLatitude(sitter.getLocation().getLat());
            tempLocation.setLongitude(sitter.getLocation().getLongitude());

            //Si la distancia es menor que 4K, ponga los marcadores en el mapa
            if(locationActual.distanceTo(tempLocation) <4000){
                MarkerOptions temp =new MarkerOptions()
                        .position(pos).title(sitter.getName())
                        .snippet("Mi ubicación")
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_sitter_map));
                mMap.addMarker(temp);
            }

        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onLocationChanged(Location location) {
        locationActual = location;
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        markerPosActual.setPosition(  pos  );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));


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
