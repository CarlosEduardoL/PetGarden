package zero.network.petgarden.ui.user.owner

import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.SitterIMP
import zero.network.petgarden.ui.element.ActionBarFragment

interface  OwnerView {

    var owner: Owner
    var sitters: List<SitterIMP>
    val topBar: ActionBarFragment

    fun loadMapView()
    fun loadProfileView()
    fun loadSittersView()

    fun reloadView()
}