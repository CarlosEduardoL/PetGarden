package zero.network.petgarden.ui.user.sitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import zero.network.petgarden.R;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.ui.element.ActionBarFragment;
import zero.network.petgarden.ui.user.owner.DockFragment;
import zero.network.petgarden.ui.user.owner.MapFragment;

public class SitterActivity extends AppCompatActivity implements SitterView{

    private Sitter sitter;

    private MapSitterFragment fragmentMap;
    private ScheduleFragment fragmentSchedule;
    private SitterProfileFragment fragmentProfile;

    FragmentManager fragmentManager;
    private ActionBarFragment topBarFragment;
    private DockSitterFragment dockFragment;

    private Fragment actualView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter);

        loadInitialFragments();

        loadDataFromActivity();

        showMap();

        //Suscribe to topic
        FirebaseMessaging.getInstance().subscribeToTopic(sitter.getId()).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful())
                        Log.e(">>>>>>>>>>>", "Suscripcion exitosa");
                }
        );
    }

    private void loadInitialFragments() {

        fragmentSchedule  = new ScheduleFragment(this);
        fragmentMap = new MapSitterFragment(this);
        fragmentProfile = new SitterProfileFragment(this);

        topBarFragment = new ActionBarFragment("",false, false, (l) -> dockFragment.selectItem(R.id.nav_map));
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.topBarSitter,topBarFragment, null);
        transaction1.commit();

        dockFragment = new DockSitterFragment(this);
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
            fragmentManager =getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.actualFragmentContainerSitter,fragmentMap, null);
            fragmentTransaction.commit();
        }


    }

    private void loadActualFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.actualFragmentContainerSitter, actualView)
                .commit();
    }

    @Override @NotNull
    public Sitter getSitter(){return sitter;}

    @NotNull @Override
    public ActionBarFragment getTopBar() {
        return topBarFragment;
    }

    @Override
    public void loadMapView() {
        actualView = fragmentMap;
        loadActualFragment();
    }

    @Override
    public void loadSchedulerView() {
        actualView = fragmentSchedule;
        loadActualFragment();
    }

    @Override
    public void loadProfileView() {
        actualView = fragmentProfile;
        loadActualFragment();
    }
}
