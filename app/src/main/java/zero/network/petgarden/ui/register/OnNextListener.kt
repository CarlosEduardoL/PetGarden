package zero.network.petgarden.ui.register

import androidx.fragment.app.Fragment

interface OnNextListener<EXTRA> {

    fun next(fragment: Fragment, vararg extra: EXTRA)

}