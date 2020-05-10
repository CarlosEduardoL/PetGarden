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

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.FragmentDockBinding;
import zero.network.petgarden.model.entity.Owner;

public class DockFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private FragmentDockBinding binding;
    private OwnerView ownerView;


    public DockFragment(OwnerView ownerView) {
        // Required empty public constructor
        this.ownerView = ownerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDockBinding.inflate(inflater,container,false);

        binding.BottomNavigationOwner.setOnNavigationItemSelectedListener(this);
        return binding.getRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        String title = null;

        switch (item.getItemId()) {
            case R.id.nav_map:
                fragment = new MapFragment();
                title ="Mapa";
                break;
            case R.id.nav_sitter:
                Log.e("XXX","Sitters Selected");
                title = "Cuidadores";
                fragment = new ListSitterFragment(ownerView);
                break;
            case R.id.nav_profile:
                Log.e("XXX","Nav Selected");
                title = "Perfil";
                fragment = new OwnerProfileFragment(ownerView);
                break;
        }

    if(fragment!=null){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.actualFragmentContainer,fragment);
        ft.commit();
    }

        return true;
    }
}
