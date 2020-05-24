package zero.network.petgarden.ui.element.picture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivityPictureBinding
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.intent

class PictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val entity = extra<Entity>(ENTITY_KEY)
        val title = extra<String>(TITLE_KEY)
        val pictureFragment = PictureFragment(object: PictureListener {
            override fun onPictureCaptured(entity: Entity) {
                setResult(Activity.RESULT_OK, Intent().putExtra(ENTITY_KEY,entity))
                finish()
            }
        } ,entity, title)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, pictureFragment)
            .commit()
    }

    companion object {
        const val ENTITY_KEY = "entity"
        const val TITLE_KEY = "title"
        fun intent(context: Context, entity: Entity, title: String) =
            context.intent(PictureActivity::class.java, ENTITY_KEY to entity, TITLE_KEY to title)
    }
}
