package zero.network.petgarden.ui.user.sitter

import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.element.ActionBarFragment

interface SitterView {
    val sitter: Sitter
    val topBar: ActionBarFragment
}