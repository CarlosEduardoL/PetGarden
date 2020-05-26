package zero.network.petgarden.ui.base

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import zero.network.petgarden.ui.LifeCycleListener

open class PetGardenActivity: AppCompatActivity() {

    private var listeners = mutableListOf<LifeCycleListener>()
        get() = field.toMutableList()

    @Synchronized
    fun addListener(listener: LifeCycleListener){
        listeners = listeners.apply { add(listener) }
    }

    private val activityScope: CoroutineScope = CoroutineScope(Main)

    override fun onResume() {
        super.onResume()
        listeners.forEach {
            activityScope.launch { it.onResume() }
        }
    }

    override fun onPause() {
        super.onPause()
        listeners.forEach {
            activityScope.launch { it.onPause() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.launch {
            listeners.forEach {
                activityScope.launch { it.onDestroy() }.join()
            }
            activityScope.cancel()
        }
        listeners = mutableListOf()
    }
}