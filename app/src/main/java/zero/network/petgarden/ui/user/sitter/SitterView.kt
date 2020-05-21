package zero.network.petgarden.ui.user.sitter

import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.element.ActionBarFragment

interface SitterView {
    val sitter: Sitter
    val topBar: ActionBarFragment

    fun loadMapView()
    fun loadSchedulerView()
    fun loadProfileView()
    fun notifyArrivalToOwner(ownerID: String)
    fun checkTaskTimeOfOwner(owner: Owner): Double
}