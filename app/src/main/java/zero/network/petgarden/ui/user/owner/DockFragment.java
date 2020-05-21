package zero.network.petgarden.ui.user.owner;

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

import org.jetbrains.annotations.NotNull;

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.FragmentDockBinding;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.notifications.FCMMessage;
import zero.network.petgarden.util.HTTPUtilKt;
import zero.network.petgarden.util.NotificationArriveUtil;
import zero.network.petgarden.util.NotificationUtils;

public class DockFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentDockBinding binding;
    private OwnerView ownerView;


    public DockFragment(OwnerView ownerView) {
        this.ownerView = ownerView;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ownerView.getTopBar().setVisibility(false);
        binding = FragmentDockBinding.inflate(inflater,container,false);

        binding.BottomNavigationOwner.setOnNavigationItemSelectedListener(this);
        return binding.getRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_map:
                ownerView.loadMapView();
                ownerView.getTopBar().setVisibility(false);
                ownerView.getTopBar().update("Mapa",false,false);
                break;
            case R.id.nav_sitter:
                Log.e("XXX","Sitters Selected");
                ownerView.loadSittersView();
                ownerView.getTopBar().setVisibility(true);
                ownerView.getTopBar().update("Cuidadores",true,false);
                break;
            case R.id.nav_profile:
                Log.e("XXX","Nav Selected");
                ownerView.loadProfileView();
                ownerView.getTopBar().setVisibility(true);
                ownerView.getTopBar().update("Perfil",true,true);
                break;
        }
        return true;
    }

    public void selectItem(int selection) {
        binding.BottomNavigationOwner.setSelectedItemId(selection);
    }
}
