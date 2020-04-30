package zero.network.petgarden.model.entity

import zero.network.petgarden.tools.downloadImage
import java.io.Serializable
import java.util.*

data class Pet(
    val id: String = UUID.randomUUID().toString(),
    var type: String = "",
    var name: String = "",
    var breed: String = "",
    var years: Int = 0,
    var weight: Int = 0,
    var about: String = "",
    var image: String = ""
): Serializable{

    suspend fun loadImage() = downloadImage(id, PET_FOLDER)

    companion object{
        const val PET_FOLDER = "pet"
    }
}
