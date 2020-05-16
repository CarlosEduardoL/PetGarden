package zero.network.petgarden.ui.user.sitter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import zero.network.petgarden.R;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Pet;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.ui.user.owner.OwnerView;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapSitterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapSitterFragment extends SupportMapFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker markerPosActual;
    private Location locationActual;
    private SitterView sitterView;
    private boolean firstEntry;
    private LocationManager manager;

    public MapSitterFragment(SitterView sitterView){
        this.sitterView = sitterView;
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
            last.setLongitude(sitterView.getSitter().getLocation().getLongitude());
            last.setLatitude(sitterView.getSitter().getLocation().getLat());
        }
        LatLng act = new LatLng(last.getLatitude(), last.getLongitude());
        markerPosActual = mMap.addMarker(  new MarkerOptions().position(act).title("Yo").snippet("Mi ubicación")  );
        firstEntry = true;
        locationActual = last;
        googleMap.addMarker(new MarkerOptions().position(act)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(act,16));

        mMap.setMyLocationEnabled(true);

        initMapLocation();

    }

    @SuppressLint("MissingPermission")
    public void initMapLocation(){
        //Llevar marker de posicion actual con zoom la primer vez
        Location last = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (last == null) {
            last = new Location("");
            last.setLongitude(sitterView.getSitter().getLocation().getLongitude());
            last.setLatitude(sitterView.getSitter().getLocation().getLat());
        }

        //Solicitar actualizaciones de posicion
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, this);

        if(locationActual==null)
            locationActual = last;

        locationActual.setLongitude(sitterView.getSitter().getLocation().getLongitude());
        locationActual.setLatitude(sitterView.getSitter().getLocation().getLat());

        //Actualizar la ubicación del sitter sólo al inicio
        sitterView.getSitter().setLocation(new zero.network.petgarden.model.entity.Location(locationActual.getLatitude(),locationActual.getLongitude()));
        sitterView.getSitter().saveInDB();

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

    public void addOwnerMarkers(Map<Owner, Set<Pet>> clientsXpets){
        LatLng pos = null;

        for(Owner owner: clientsXpets.keySet()){


            pos = new LatLng(owner.getLocation().getLat(),owner.getLocation().getLongitude());
            Location tempLocation = new Location("");
            tempLocation.setLatitude(owner.getLocation().getLat());
            tempLocation.setLongitude(owner.getLocation().getLongitude());

            //Si la distancia es menor que 4K, ponga los marcadores en el mapa
            if(locationActual.distanceTo(tempLocation) <4000){
                MarkerOptions temp =new MarkerOptions()
                        .position(pos).title(owner.getName())
                        .snippet("Mi ubicación")
                        .draggable(false)
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_sitter_marker));
                mMap.addMarker(temp);
            }

        }

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
