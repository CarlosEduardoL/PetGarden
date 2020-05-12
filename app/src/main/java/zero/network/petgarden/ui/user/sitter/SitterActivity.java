package zero.network.petgarden.ui.user.sitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import zero.network.petgarden.R;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.ui.element.ActionBarFragment;
import zero.network.petgarden.ui.user.owner.DockFragment;
import zero.network.petgarden.ui.user.owner.MapFragment;

public class SitterActivity extends AppCompatActivity implements SitterView{

    private Sitter sitter;
    MapSitterFragment fragmentMap;
    FragmentManager fragmentManager;
    private ActionBarFragment topBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter);

        loadInitialFragments();

        loadDataFromActivity();

        showMap();

    }

    private void loadInitialFragments() {

        topBarFragment = new ActionBarFragment("",false);
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.topBarSitter,topBarFragment, null);
        transaction1.commit();

        DockSitterFragment dockFragment = new DockSitterFragment(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dock_container_sitter,dockFragment, null);
        transaction.commit();
    }

    private void loadDataFromActivity() {

        //PONER A CARGAR LOS DATOS DEL EXTRA
        Bundle extras = getIntent().getExtras();
        sitter =(Sitter) extras.getSerializable("user");

    }



    public void showMap(){


        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fragmentMap = new MapSitterFragment(this);

            fragmentManager =getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.actualFragmentContainerSitter,fragmentMap, null);
            fragmentTransaction.commit();
        }


    }


    public Sitter getSitter(){return sitter;}

    @NotNull
    @Override
    public ActionBarFragment getTopBar() {
        return topBarFragment;
    }
}
