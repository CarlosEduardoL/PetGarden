package zero.network.petgarden.ui.user.owner.recruitment

import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.SitterIMP

interface RecruitmentView {
    val sitter: SitterIMP
    val owner: Owner

    suspend fun selectPet(): Pet?

}