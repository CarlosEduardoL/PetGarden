package zero.network.petgarden.model.entity

import java.util.*

data class Pet(val id: String, val type: String,  val name: String, val breed: String, val years: Int, val weight: Int)

class PetFactory(
    var type: String = "None",
    var name: String = "None",
    var breed: String = "None",
    var years: Int = 0,
    var weight: Int = 0
){
    fun type(type: String): PetFactory = this.apply { this.type = type }
    fun name(name: String): PetFactory = this.apply { this.name = name }
    fun breed(breed: String): PetFactory = this.apply { this.breed = breed }
    fun years(years: Int): PetFactory = this.apply { this.years = years }
    fun weight(weight: Int): PetFactory = this.apply { this.weight = weight }

    val pet: Pet
        get() = Pet(UUID.randomUUID().toString(), type, name, breed, years, weight)
}