package zero.network.petgarden.ui.user.owner

import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.element.ActionBarFragment

interface  OwnerView {

    var owner: Owner
    var sitters: List<Sitter>
    val topBar: ActionBarFragment

}