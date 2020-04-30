package zero.network.petgarden.ui.register.pet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Pet.Companion.PET_FOLDER
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show

class PetRegisterActivity : AppCompatActivity(), OnNextListener {

    private lateinit var pet: Pet

    private lateinit var typeFragment: PetTypeFragment
    private lateinit var nameFragment: PetNameFragment
    private lateinit var breedFragment: PetBreedFragment
    private lateinit var ageFragment: PetAgeFragment
    private lateinit var weightFragment: PetWeightFragment
    private lateinit var picFragment: PetPicFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_register)

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.header,
                ActionBarFragment(
                    extra(TITLE_KEY) { onError(it); return },
                    true
                ) { onBackPressed() })
            .commit()


        pet = extra(PET_KEY) { onError(it); return }

        requestPermission()

    }

    override fun next(fragment: Fragment) {
        when (fragment) {
            typeFragment -> changeTo(nameFragment)
            nameFragment -> changeTo(breedFragment)
            breedFragment -> changeTo(ageFragment)
            ageFragment -> changeTo(weightFragment)
            weightFragment -> changeTo(picFragment)
            picFragment -> GlobalScope.launch(Main) {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(PET_KEY, pet) })
                uploadImage(picFragment.imageDir(), PET_FOLDER)
                FirebaseDatabase.getInstance().reference.child("pets").child(pet.id).setValue(pet)
                    .await()
                finish()
            }
        }

    }

    private fun onError(error: String) {
        show(error)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun changeTo(fragment: Fragment) =
        supportFragmentManager.beginTransaction().replace(R.id.body, fragment)
            .addToBackStack(REGISTER_STACK).commit()

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), REQUEST_PERMISSION_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.all { it == PERMISSION_GRANTED }) {
            typeFragment = PetTypeFragment(this, pet)
            nameFragment = PetNameFragment(this, pet)
            breedFragment = PetBreedFragment(this, pet)
            ageFragment = PetAgeFragment(this, pet)
            weightFragment = PetWeightFragment(this, pet)
            picFragment = PetPicFragment(this, pet)

            supportFragmentManager.beginTransaction()
                .replace(R.id.body, typeFragment)
                .commit()
        } else {
            requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK) {
           supportFragmentManager.popBackStack()
            picFragment =PetPicFragment(this,pet, BitmapFactory.decodeFile(picFragment.imageDir().path)
                .let { Bitmap.createScaledBitmap(it, it.width / 4, it.height / 4, false) })
            changeTo(picFragment)
        }
    }

    companion object {
        const val PET_KEY = "PET"
        const val TITLE_KEY = "TITLE"
        const val CAMERA_INTENT = 290
        private const val REGISTER_STACK = "REGISTER_STACK"
        private const val REQUEST_PERMISSION_CODE = 0
    }
}
