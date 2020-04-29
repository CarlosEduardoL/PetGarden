package zero.network.petgarden.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

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
                    Toast.makeText(activity,"Hola",Toast.LENGTH_SHORT);
                }
        );

    }

}
