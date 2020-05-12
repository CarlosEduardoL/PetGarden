package zero.network.petgarden.ui.user.sitter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentScheduleBinding
import zero.network.petgarden.util.monthToText
import java.lang.System.currentTimeMillis
import java.util.*

class ScheduleFragment(view: SitterView) : Fragment(), SitterView by view {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentScheduleBinding.inflate(inflater, container, false)!!.apply {
        calendarView.date = currentTimeMillis()
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectDay.text = "$dayOfMonth de ${monthToText(month)}"
            Calendar.getInstance().apply {
                set(year,month, dayOfMonth)

            }
        }
    }.root
}