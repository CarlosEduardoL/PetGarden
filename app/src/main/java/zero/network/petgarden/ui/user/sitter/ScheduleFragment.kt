package zero.network.petgarden.ui.user.sitter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentScheduleBinding
import zero.network.petgarden.util.endOfDay
import zero.network.petgarden.util.monthToText
import zero.network.petgarden.util.simplify
import zero.network.petgarden.util.startOfDay
import java.lang.System.currentTimeMillis
import java.util.*

class ScheduleFragment(view: SitterView) : Fragment(), SitterView by view {

    private lateinit var addAvailabilityFragment: AddAvailabilityFragment
    private lateinit var dateSalected: Calendar
    private val scope = CoroutineScope(Main)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentScheduleBinding.inflate(inflater, container, false)!!.apply {
        addAvailabilityFragment =
            AddAvailabilityFragment(sitter, Calendar.getInstance())
        val adapter = ScheduleAdapter()
        val task = scope.launch { }
        listClients.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
        }

        dateSalected =Calendar.getInstance()
        initSchedule(adapter, selectDay)


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectDay.text = "$dayOfMonth de ${monthToText(month)}"

            dateSalected.set(year, month, dayOfMonth)

            Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
                if (task.isActive) task.cancel()
                scope.launch {
                    val start = time.startOfDay().time
                    val end = time.endOfDay().time
                    val dayTasks = sitter.planner.tasks
                        .asSequence()
                        .filter { it.duration.start >= start && it.duration.end <= end }
                        .map { it.petID to it.duration.start }.toMap()
                    adapter.tasks = sitter.clientsXPets()
                        .map { "${it.key.name} ${it.key.lastName}" to it.value.map { pet -> pet.id } }
                        .map { it.first to it.second.toSet().intersect(dayTasks.keys) }
                        .simplify()
                        .map { it.first to (dayTasks[it.second] ?: 0) }
                }
            }
        }

        addAvailability.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            addAvailabilityFragment.dateSelected = dateSalected
            fragmentTransaction.replace(R.id.actualFragmentContainerSitter, addAvailabilityFragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }

        calendarView.date = currentTimeMillis()
    }.root

    private fun initSchedule(adapter: ScheduleAdapter, selectDay: TextView){
        val today = Calendar.getInstance()
        val month = today.get(Calendar.MONTH)
        val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)
        val year  = today.get(Calendar.YEAR)

        selectDay.text = "$dayOfMonth de ${monthToText(month)}"
        dateSalected.set(year, month, dayOfMonth)

        Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
            scope.launch {
                val start = time.startOfDay().time
                val end = time.endOfDay().time
                val dayTasks = sitter.planner.tasks
                    .asSequence()
                    .filter { it.duration.start >= start && it.duration.end <= end }
                    .map { it.petID to it.duration.start }.toMap()
                adapter.tasks = sitter.clientsXPets()
                    .map { "${it.key.name} ${it.key.lastName}" to it.value.map { pet -> pet.id } }
                    .map { it.first to it.second.toSet().intersect(dayTasks.keys) }
                    .simplify()
                    .map { it.first to (dayTasks[it.second] ?: 0) }
            }
        }
    }
}