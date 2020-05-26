package zero.network.petgarden.ui.user.sitter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.FragmentDockSitterBinding;

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
