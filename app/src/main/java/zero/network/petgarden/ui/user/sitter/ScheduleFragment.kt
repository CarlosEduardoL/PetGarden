package zero.network.petgarden.ui.user.sitter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.FragmentScheduleBinding
import zero.network.petgarden.util.endOfDay
import zero.network.petgarden.util.monthToText
import zero.network.petgarden.util.simplify
import zero.network.petgarden.util.startOfDay
import java.lang.System.currentTimeMillis
import java.util.*

class ScheduleFragment(view: SitterView) : Fragment(), SitterView by view {

    private val scope = CoroutineScope(Main)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentScheduleBinding.inflate(inflater, container, false)!!.apply {
        val adapter = ScheduleAdapter()
        val task = scope.launch {  }
        listClients.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
        }
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectDay.text = "$dayOfMonth de ${monthToText(month)}"
            Calendar.getInstance().apply {
                set(year,month, dayOfMonth)
                if (task.isActive) task.cancel()
                scope.launch {
                    val start = time.startOfDay().time
                    val end = time.endOfDay().time
                    val dayTasks = sitter.tasks
                        .asSequence()
                        .filter { it.duration.start >= start && it.duration.end <= end }
                        .map { it.petID to it.duration.start }.toMap()
                    adapter.tasks = sitter.clientsXPets()
                        .map { "${it.key.name} ${it.key.lastName}" to it.value.map { pet -> pet.id } }
                        .map { it.first to it.second.toSet().intersect(dayTasks.keys) }
                        .simplify()
                        .map { it.first to (dayTasks[it.second] ?:0) }

                }
            }
        }
        calendarView.date = currentTimeMillis()
    }.root
}