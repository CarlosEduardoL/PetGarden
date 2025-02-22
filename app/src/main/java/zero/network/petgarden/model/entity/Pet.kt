package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.tools.downloadImage
import java.io.Serializable
import java.util.*

data class Pet(
    override var id: String = UUID.randomUUID().toString(),
    var type: String = "",
    var name: String = "",
    var breed: String = "",
    var years: Int = 0,
    var weight: Int = 0,
    var about: String = "Sin comentarios",
    var ownerID: String = "",
    var sitterID: String? = null,
    override var debug: String = ""
): Serializable, Entity{

    suspend fun loadImage() = downloadImage()

    override fun folder() = FOLDER

    companion object{
        const val FOLDER = "pets"
    }
}
