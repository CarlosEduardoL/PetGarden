package zero.network.petgarden.model.entity

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import zero.network.petgarden.model.behaivor.Sitter
import zero.network.petgarden.model.component.Location
import zero.network.petgarden.model.component.Planner
import zero.network.petgarden.model.entity.SitterIMP.Companion.FOLDER
import zero.network.petgarden.ui.LifeCycleListener
import zero.network.petgarden.ui.base.PetGardenActivity
import zero.network.petgarden.util.times
import java.util.*

class SitterWatcher private constructor(private var sitter: SitterIMP) : Sitter {
    override suspend fun clients(): Set<Owner> = sitter.clients()

    override suspend fun pets(): Set<Pet> = sitter.pets()

    override suspend fun clientsXPets(): Map<Owner, Set<Pet>> = sitter.clientsXPets()

    override suspend fun image() = sitter.image()

    override var rating: Double
        get() = sitter.rating
        set(value) {
            sitter.rating = value
        }
    override var kindPets: String
        get() = sitter.kindPets
        set(value) {
            sitter.kindPets = value
        }
    override var additional: String
        get() = sitter.additional
        set(value) {
            sitter.additional = value
        }
    override var planner: Planner
        get() = sitter.planner
        set(value) {
            sitter.planner = value
        }

    override var id: String
        get() = sitter.id
        set(value) {
            sitter.id = value
        }

    override fun folder(): String = sitter.folder()

    override var name: String
        get() = sitter.name
        set(value) {
            sitter.name = value
        }

    override var lastName: String
        get() = sitter.lastName
        set(value) {
            sitter.lastName = value
        }
    override var email: String
        get() = sitter.email
        set(value) {
            sitter.email = value
        }
    override var password: String
        get() = sitter.password
        set(value) {
            sitter.password = value
        }
    override var birthDay: Date
        get() = sitter.birthDay
        set(value) {
            sitter.birthDay = value
        }
    override var imageURL: String?
        get() = sitter.imageURL
        set(value) {
            sitter.imageURL = value
        }
    override var location: Location
        get() = sitter.location
        set(value) {
            sitter.location = value
        }

    override fun toString(): String {
        return sitter.toString()
    }

    private fun map(map: Map<*, *>): Map<*,*> {
        return map.map {
            when (val second = it.value) {
                is String -> it.key to "\"${it.value}\""
                is Map<*, *> -> it.key to map(second)
                else -> it.key to it.value
            }
        }.toMap()
    }

    companion object {

        fun PetGardenActivity.bind(sitter: SitterIMP): Sitter {
            val watcher =
                SitterWatcher(sitter)

            addListener(object : LifeCycleListener {
                private lateinit var listener: ChildEventListener
                private val gson = Gson()
                override fun onResume() {
                    println(watcher.sitter)
                    listener = FirebaseDatabase.getInstance()
                        .reference.child(FOLDER).child(sitter.id)
                        .addChildEventListener(object : ChildEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                Log.d("Error", error.message)
                            }

                            override fun onChildMoved(data: DataSnapshot, id: String?) {
                                println("onChildMoved-"*50)
                                println(data)
                            }

                            override fun onChildRemoved(data: DataSnapshot) {
                                println("onChildRemoved-"*50)
                                println(data)
                                println(watcher.sitter)
                            }

                            override fun onChildChanged(data: DataSnapshot, id: String?) {
                                val field = when (val value = data.value) {
                                    is String -> data.key to "\"${value}\""
                                    is Map<*,*> -> data.key to watcher.map(value)
                                    else -> data.key to value
                                }
                                val pair = gson.fromJson("{\"${field.first}\":${field.second}}", SitterIMP::class.java)!! to field.first!!
                                watcher.sitter = watcher.sitter + pair

                            }

                            override fun onChildAdded(data: DataSnapshot, id: String?) {
                                val field = when (val value = data.value) {
                                    is String -> data.key to "\"${value}\""
                                    is Map<*,*> -> data.key to watcher.map(value)
                                    else -> data.key to value
                                }
                                val pair = gson.fromJson("{\"${field.first}\":${field.second}}", SitterIMP::class.java)!! to field.first!!
                                watcher.sitter = watcher.sitter + pair
                            }
                        })
                }

                override fun onPause() {
                    FirebaseDatabase.getInstance()
                        .reference.child(FOLDER).child(sitter.id)
                        .removeEventListener(listener)
                }

                override fun onDestroy() {}
            })

            return watcher
        }
    }

}