package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IUser
import java.io.Serializable

data class Owner(private val user: User = User()): IUser by user, Serializable