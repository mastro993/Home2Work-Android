package it.gruppoinfor.home2work.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.EditText

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R

class EditProfileActivity : AppCompatActivity() {

    @BindView(R.id.input_fb)
    internal var inputFb: EditText? = null
    @BindView(R.id.input_tw)
    internal var inputTw: EditText? = null
    @BindView(R.id.input_tg)
    internal var inputTg: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        ButterKnife.bind(this)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        initUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initUI() {

    }

}

