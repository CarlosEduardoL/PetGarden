package zero.network.petgarden.ui.element.picture

import zero.network.petgarden.model.behaivor.Entity

interface PictureListener {

    fun onPictureCaptured(entity: Entity)

}