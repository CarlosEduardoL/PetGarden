package zero.network.petgarden.ui.user.owner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.ActivityOwnerBinding;
import zero.network.petgarden.databinding.FragmentDockBinding;
import zero.network.petgarden.model.entity.Location;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.ui.element.ActionBarFragment;

public class OwnerActivity extends AppCompatActivity implements OwnerView{

    FragmentManager fragmentManager;
    private Owner owner;
    private List<Sitter> sitters;
    MapFragment fragmentMap;
    ListSitterFragment sittersFragment;


    private ActivityOwnerBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        loadInitialFragments();


        sitters = new ArrayList<>();


        loadDataFromActivity();

        //Check Permissions
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
        }, 11);




    }

    private void loadInitialFragments(){
        ActionBarFragment topBarFragment = new ActionBarFragment("",false);
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.topBar,topBarFragment, null);
        transaction1.commit();


        DockFragment dockFragment = new DockFragment(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dock_container,dockFragment, null);
        transaction.commit();
    }


    private void loadDataFromActivity() {

        //PONER A CARGAR LOS DATOS DEL EXTRA
        Bundle extras = getIntent().getExtras();
        owner =(Owner) extras.getSerializable("user");
        /*owner = new Owner();*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        showMap();
    }

    public void showMap(){
        getSittersFromDB();


        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fragmentMap = new MapFragment();
            fragmentManager =getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.actualFragmentContainer,fragmentMap, null);
            fragmentTransaction.commit();
        }


    }

    public void updateOwnerLocation(Location location){

        // owner
        owner.setLocation(location);
        owner.saveInDB();


    }


    public void getSittersFromDB(){

        //Obtener lista de sitters mediante la clase
        Query query =FirebaseDatabase.getInstance()
                .getReference()
                .child("sitters");
        Log.e(">>>Query:",query.toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Sitter sitterChild= child.getValue(Sitter.class);
                    sitters.add(sitterChild);
                    Log.e(">>>","" +sitterChild.getId()+ ":"+sitterChild.getName());
                }

                fragmentMap.addSittersMarkers((ArrayList<Sitter>)sitters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @NotNull
    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @NotNull
    public List<Sitter> getSitters() {
        return sitters;
    }

    public void setSitters(List<Sitter> sitters) {
        this.sitters = sitters;
    }


}
