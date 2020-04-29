package zero.network.petgarden.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import zero.network.petgarden.ui.user.owner.OwnerActivity;

public class ClasePruebas {

    private LoginActivity activity;

    public ClasePruebas(LoginActivity activity) {
        this.activity = activity;
        intentAMapa();

    }

    private void intentAMapa(){
        Button buttonPruebas;
        buttonPruebas = activity.getBinding().buttonPruebas;
        buttonPruebas.setOnClickListener(
                (view)->{
                    Intent i = new Intent(activity, OwnerActivity.class);
                    activity.startActivity(i);
                }
        );

    }

}
