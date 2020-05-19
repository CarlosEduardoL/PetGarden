package zero.network.petgarden.ui.user.owner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.ActivityOwnerBinding;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.Sitter;
import zero.network.petgarden.ui.element.ActionBarFragment;
import zero.network.petgarden.util.ActivityUtilKt;
import zero.network.petgarden.util.HTTPUtilKt;
import zero.network.petgarden.util.NotificationUtils;

public class OwnerActivity extends SitterListener implements OwnerView {

    private Owner owner;
    private List<Sitter> sitters = Collections.emptyList();

    private MapFragment fragmentMap;
    private ListSitterFragment sittersFragment;
    private OwnerProfileFragment profileFragment;

    private ActionBarFragment topBarFragment;

    private DockFragment dockFragment;
    private Fragment actualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOwnerBinding binding = ActivityOwnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadInitialFragments();

        sitters = new ArrayList<>();

        loadDataFromActivity();
        HTTPUtilKt.suscribeToTopic(owner.getId());

        showMap();

    }

    private void loadInitialFragments() {

        sittersFragment = new ListSitterFragment(this);
        fragmentMap = new MapFragment(this);
        profileFragment = new OwnerProfileFragment(this);

        // Set TopBar
        topBarFragment = new ActionBarFragment("", false, false, result -> dockFragment.selectItem(R.id.nav_map));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topBar, topBarFragment, null)
                .commit();

        // Set Dock
        dockFragment = new DockFragment(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dock_container, dockFragment)
                .commit();
    }

    private void loadDataFromActivity() {
        //PONER A CARGAR LOS DATOS DEL EXTRA
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            owner = (Owner) extras.getSerializable("user");
        else
            throw new NullPointerException();
    }

    public void showMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loadMapView();
            }
    }

    private void loadActualFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.actualFragmentContainer, actualFragment)
                .commit();
    }

    @Override
    public void onSittersUpdate(@NotNull List<Sitter> sitters) {
        this.sitters = sitters;
        if(fragmentMap == actualFragment) fragmentMap.addSittersMarkers(sitters);
        else if (sittersFragment == actualFragment) reloadView();
    }

    @Override
    public void reloadView() {
        ActivityUtilKt.show(this, "updating sitters...");
        getSupportFragmentManager().beginTransaction().detach(actualFragment).attach(actualFragment).commit();
    }

    @NotNull
    public Owner getOwner() {
        return owner;
    }

    public void setOwner(@NotNull Owner owner) {
        this.owner = owner;
    }

    @NotNull
    public List<Sitter> getSitters() {
        return sitters;
    }

    public void setSitters(@NotNull List<Sitter> sitters) {
        this.sitters = sitters;
    }

    @NotNull
    @Override
    public ActionBarFragment getTopBar() {
        return topBarFragment;
    }

    @Override
    public void loadMapView() {
        actualFragment = fragmentMap;
        loadActualFragment();
    }

    @Override
    public void loadProfileView() {
        actualFragment = profileFragment;
        loadActualFragment();
    }

    @Override
    public void loadSittersView() {
        actualFragment = sittersFragment;
        loadActualFragment();
    }
}
