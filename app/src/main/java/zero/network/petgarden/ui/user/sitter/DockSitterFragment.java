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

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.FragmentDockBinding;
import zero.network.petgarden.databinding.FragmentDockSitterBinding;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.ui.user.owner.ListSitterFragment;
import zero.network.petgarden.ui.user.owner.MapFragment;
import zero.network.petgarden.ui.user.owner.OwnerProfileFragment;
import zero.network.petgarden.ui.user.owner.OwnerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DockSitterFragment} factory method to
 * create an instance of this fragment.
 */
public class DockSitterFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match

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
                fragment = new MapSitterFragment(sitterView);
                sitterView.getTopBar().setVisibility(false);
                sitterView.getTopBar().update("Mapa",false,false);
                break;
            case R.id.nav_sitter:
                Log.e("XXX","Sitters Selected");
                title = "Cuidadores";
                fragment = new ScheduleFragment(sitterView);
                sitterView.getTopBar().setVisibility(true);
                sitterView.getTopBar().update("Planeador",true,false);
                break;
            case R.id.nav_profile:
                Log.e("XXX","Nav Selected");
                title = "Perfil";
                fragment = new SitterProfileFragment(sitterView);
                sitterView.getTopBar().setVisibility(true);
                sitterView.getTopBar().update("Perfil",true,true);
                break;
        }

        if(fragment!=null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.actualFragmentContainerSitter,fragment);
            ft.commit();
        }

        return true;
    }
}
