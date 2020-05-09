package zero.network.petgarden.tools

import zero.network.petgarden.model.entity.Pet

interface OnPetClickListener {
     fun onPetClick(pet: Pet)
}