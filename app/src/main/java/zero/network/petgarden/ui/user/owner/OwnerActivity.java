package zero.network.petgarden.ui.user.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import zero.network.petgarden.R;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.model.entity.User;

public class OwnerActivity extends AppCompatActivity{

    FragmentManager fragmentManager;
    private Owner owner;
    private ArrayList<Sitter> sitters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        sitters = new ArrayList<>();

        loadDataFromActivity();


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
        }, 11);


    }

    private void loadDataFromActivity() {
        Bundle extras = getIntent().getExtras();
        owner =(Owner) extras.getSerializable("user");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        showMap();
    }

    public void showMap(){
        getSittersFromDB();

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MapFragment fragmentMap = new MapFragment();

            fragmentManager =getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.activity_owner_container,fragmentMap, null);
            fragmentTransaction.commit();
        }


    }

    public void updateOwnerLocation(Location location){

        /*User temp = new User();
        temp.setName("Santiago");
        temp.setPassword("mypass123");
        temp.setBirthDay(new Date(2000,9,11));
        temp.setEmail("chasquicrack@gmail.com");
        temp.setLastName("Chasqui");
        temp.setLocation(new Location(3,-76));*/

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child("owners")
                .child(owner.getId())
                .child("location")
                .setValue(location);


    }

    public void getSittersFromDB(){

        //Obtener lista de sitters
        Query query =FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public ArrayList<Sitter> getSitters() {
        return sitters;
    }

    public void setSitters(ArrayList<Sitter> sitters) {
        this.sitters = sitters;
    }
}
