package zero.network.petgarden.ui.register.pet

import androidx.fragment.app.Fragment
import zero.network.petgarden.model.entity.Pet

interface OnNextListener {
    fun next(fragment: Fragment, state: Pet)
}