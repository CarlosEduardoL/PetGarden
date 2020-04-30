package zero.network.petgarden.ui.user.sitter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zero.network.petgarden.databinding.ActivitySitterBinding
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show

class SitterActivity : AppCompatActivity() {

    private lateinit var sitter: Sitter
    private lateinit var binding: ActivitySitterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySitterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sitter = extra("user"){ show(it);finish();return }
    }
}
