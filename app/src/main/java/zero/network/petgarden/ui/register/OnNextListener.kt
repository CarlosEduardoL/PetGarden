package zero.network.petgarden.ui.register

import androidx.fragment.app.Fragment
import zero.network.petgarden.model.behaivor.IUser

interface OnNextListener {

    fun next(fragment: Fragment, state: IUser)

}