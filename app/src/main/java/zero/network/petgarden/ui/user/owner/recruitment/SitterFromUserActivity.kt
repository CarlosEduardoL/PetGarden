package zero.network.petgarden.ui.user.owner.recruitment

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivitySitterFromUserBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.util.extra
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SitterFromUserActivity : AppCompatActivity(), RecruitmentView {

    private lateinit var _sitter: Sitter
    private lateinit var _owner: Owner
    lateinit var binding: ActivitySitterFromUserBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySitterFromUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load sitter and owner
        _sitter = extra("sitter")
        _owner = extra("owner")

        supportFragmentManager.beginTransaction()
            .replace(R.id.topBar, ActionBarFragment("Recruitment",true){onBackPressed()})
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, RecruitmentFragment(this))
            .commit()

    }

    override val sitter: Sitter
        get() = _sitter
    override val owner: Owner
        get() = _owner

    private var continuation : Continuation<Pet?>? = null

    override fun onBackPressed() {
        super.onBackPressed()
        continuation?.resume(null)
    }

    override suspend fun selectPet(): Pet? = suspendCoroutine<Pet?> {
        continuation?.resume(null)
        continuation = it
        CoroutineScope(Main).launch {
            if (owner.pets().size == 1) it.resume(owner.pets().first())
            else {
                println("---------------- Estoy Pasando -------------------------------")

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, SelectPetFragment(owner.pets().filter { it.sitterID == null }, it))
                    .addToBackStack("Default")
                    .commit()
            }
        }
    }.also {
        continuation = null
    }


}