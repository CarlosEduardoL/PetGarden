package zero.network.petgarden.ui.user.sitter

import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.behaivor.Sitter
import zero.network.petgarden.model.component.Task
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.ui.element.ActionBarFragment

interface SitterView {
    val sitter: Sitter
    val topBar: ActionBarFragment

    fun loadMapView()
    fun loadSchedulerView()
    fun loadProfileView()
    fun notifyArrivalToOwner(owner: Owner, cost: Double, petName: String)
    fun checkTaskTimeOfOwner(owner: Owner, callback: CallBack<Boolean>)
    fun showPay(TaskByID: Task)

}