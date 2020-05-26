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
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import zero.network.petgarden.R;
import zero.network.petgarden.model.component.Task;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.model.notifications.FCMMessage;
import zero.network.petgarden.model.notifications.FCMMessageArrival;
import zero.network.petgarden.model.notifications.Message;
import zero.network.petgarden.model.notifications.MessageArrival;
import zero.network.petgarden.ui.element.ActionBarFragment;
import zero.network.petgarden.ui.user.owner.DockFragment;
import zero.network.petgarden.ui.user.owner.MapFragment;
import zero.network.petgarden.util.HTTPUtilKt;
import zero.network.petgarden.util.NotificationArriveUtil;
import zero.network.petgarden.util.NotificationUtils;

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
                        Log.e(">>>>>>>>>>>", "Suscripcion sitter exitosa");
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


    @Override
    public void notifyArrivalToOwner(Owner owner,double cost, String petName) {
        /*
        Lanzar la push notification para avisarle al dueño que ya llegó su mascota
         */
        MessageArrival message = new MessageArrival();
        message.setCost(cost);
        message.setTitulo("Tu mascota está afuera");
        message.setPetName(petName);
        message.setSitterID(sitter.getId());
        FCMMessageArrival fcm = new FCMMessageArrival();
        Log.e(">>>ntfyarrown",owner.getId());
        fcm.setTo("/topics/"+owner.getId());
        fcm.setData(message);
        Gson gson = new Gson();
        String json =gson.toJson(fcm);

        new Thread(
                ()->{
                    HTTPUtilKt.POSTtoFCM(FCMMessageArrival.API_KEY,json);
                }
        ).start();
    }

    @Override
    public double checkTaskTimeOfOwner(Owner owner) {
        Date currentDate = new Date(System.currentTimeMillis());

        /*Obtener las tareas para ver cual coincide con la del owner del parametro para sacarle el Date y compararlo con el
        momento actual*/
        List<Task> tasks =getSitter().getPlanner().getTasks();
        for(Task task: tasks){


        }

        return 0;
    }
}
