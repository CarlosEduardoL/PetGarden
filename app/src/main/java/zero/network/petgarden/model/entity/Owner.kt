package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IUser

data class Owner(private val user: IUser): IUser by user