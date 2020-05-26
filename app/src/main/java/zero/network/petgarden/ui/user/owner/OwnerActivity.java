package zero.network.petgarden.ui.user.owner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zero.network.petgarden.R;
import zero.network.petgarden.databinding.ActivityOwnerBinding;
import zero.network.petgarden.model.behaivor.Sitter;
import zero.network.petgarden.model.entity.Owner;
import zero.network.petgarden.model.entity.SitterIMP;
import zero.network.petgarden.model.notifications.MessageArrival;
import zero.network.petgarden.ui.element.ActionBarFragment;
import zero.network.petgarden.util.ActivityUtilKt;
import zero.network.petgarden.util.HTTPUtilKt;

public class OwnerActivity extends SitterListener {

    private Owner owner;
    private List<? extends Sitter> sitters = Collections.emptyList();

    private MapFragment fragmentMap;
    private ListSitterFragment sittersFragment;
    private OwnerProfileFragment profileFragment;

    private ActionBarFragment topBarFragment;

    private DockFragment dockFragment;
    private Fragment actualFragment;

    private static boolean active = false;

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

        Intent intent = getIntent();

        SharedPreferences sp = getSharedPreferences("dialog", Context.MODE_PRIVATE);

        if(sp.contains("show_dialog")){

            Gson gson = new Gson();
            MessageArrival msgArrival= gson.fromJson(sp.getString("messageArrival",""), MessageArrival.class);
            showArrivalDialog(msgArrival);
        }
        sp.edit().clear().apply();

    }


    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
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
        else {
            throw new NullPointerException();
        }
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
    public void onSittersUpdate(@NotNull List<? extends Sitter> sitters) {
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
        return (List<Sitter>) sitters;
    }

    public void setSitters(@NotNull List<? extends Sitter> sitters) {
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


    private void showArrivalDialog(MessageArrival msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_service_from_client,null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();



        TextView txtSitterName = view.findViewById(R.id.sitter_name);
        EditText commentaryET = view.findViewById(R.id.commentaryET);
        String commentaryString = commentaryET.getText().toString();
/*        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setOnTouchListener(
            (v, event) -> {
            float rating = ratingBar.getRating();

            if(msg.getSitterID() != null){
                Sitter sitter =sitterByID(msg.getSitterID());
                sitter.setRating(rating);
            }

            return ratingBar.onTouchEvent(event);
        });*/

        //Mandar al sitter estrellas y comentarios
        TextView txtCost = view.findViewById(R.id.costTV);


        txtCost.setText("$"+msg.getCost());
        txtSitterName.setText("NameSitter");

        Button acceptBtn = view.findViewById(R.id.okBtn);
        acceptBtn.setOnClickListener(
                (event)->{
                    dialog.dismiss();
                }
        );
    }

    public static boolean isActive() {
        return active;
    }
}
