package zero.network.petgarden.ui.user.sitter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.FragmentDockBinding;
import zero.network.petgarden.databinding.FragmentDockSitterBinding;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.model.notifications.FCMMessage;
import zero.network.petgarden.model.notifications.FCMMessageArrival;
import zero.network.petgarden.model.notifications.MessageArrival;
import zero.network.petgarden.ui.user.owner.ListSitterFragment;
import zero.network.petgarden.ui.user.owner.MapFragment;
import zero.network.petgarden.ui.user.owner.OwnerProfileFragment;
import zero.network.petgarden.ui.user.owner.OwnerView;
import zero.network.petgarden.util.HTTPUtilKt;
import zero.network.petgarden.util.NotificationArriveUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DockSitterFragment} factory method to
 * create an instance of this fragment.
 */
public class DockSitterFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentDockSitterBinding binding;
    private SitterView sitterView;

    public DockSitterFragment(SitterView sitter) {
        // Required empty public constructor
        sitterView = sitter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDockSitterBinding.inflate(inflater,container,false);
        sitterView.getTopBar().setVisibility(false);

        binding.bottomNavSitter.setOnNavigationItemSelectedListener(this);
        return binding.getRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = new Fragment();
        String title = null;

        switch (item.getItemId()) {
            case R.id.nav_map:
                sitterView.loadMapView();
                sitterView.getTopBar().setVisibility(false);
                sitterView.getTopBar().update("Mapa",false,false);

                sitterView.notifyArrivalToOwner("A9h5keqPIvOFSiyhI7X7MeQWP6O2");
                MessageArrival message = new MessageArrival();
                message.setBody("Llego mascota");
                message.setTitulo("Tu mascota afuera");
                message.setPetName("Max");
                FCMMessageArrival fcm = new FCMMessageArrival();
                fcm.setTo("/topics/dgRKg3FdbwTh8YIXS0BfHMeFbE72");
                fcm.setData(message);
                Gson gson = new Gson();
                String json =gson.toJson(fcm);

                new Thread(
                        ()->{
                             HTTPUtilKt.POSTtoFCM(FCMMessageArrival.API_KEY,json);
                        }
                ).start();

                break;
            case R.id.nav_sitter:
                sitterView.loadSchedulerView();
                sitterView.getTopBar().setVisibility(true);
                sitterView.getTopBar().update("Planeador",true,false);
                break;
            case R.id.nav_profile:
                sitterView.loadProfileView();
                sitterView.getTopBar().setVisibility(true);
                sitterView.getTopBar().update("Perfil",true,true);
                break;
        }

        return true;
    }

    public void selectItem(int selection) {
        binding.bottomNavSitter.setSelectedItemId(selection);
    }
}
