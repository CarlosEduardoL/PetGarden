package zero.network.petgarden.ui.user.owner

import zero.network.petgarden.model.behaivor.Sitter
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.ui.element.ActionBarFragment

interface  OwnerView {

    var owner: Owner
    var sitters: List<Sitter>
    val topBar: ActionBarFragment

    fun loadMapView()
    fun loadProfileView()
    fun loadSittersView()

    fun reloadView()

    fun sitterByID(id: String) = sitters.firstOrNull { it.id == id }


}