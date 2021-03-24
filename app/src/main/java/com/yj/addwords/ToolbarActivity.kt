package com.yj.addwords

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar

open class ToolbarActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

    }

    override fun setContentView(layoutResID: Int) {
        var fullView : LinearLayout = layoutInflater.inflate(R.layout.activity_toolbar, null) as LinearLayout
        super.setContentView(fullView)
        toolbar = findViewById<Toolbar>(R.id.toolbar)

        if (useToolbar()) { // 툴바 이름도 받아야할듯  &&로
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false) // 둘 다 잇어야하나? 밑으로
            title = "툴바 예제"
        }else{
            toolbar?.visibility = View.GONE
        }
    }

    protected open fun useToolbar(): Boolean {
        return true

    }

/*    //    Option Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.menu_sort -> {
                Toast.makeText(this,"정렬하기", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_import -> {
                Toast.makeText(this,"가져오기", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }*/

}

