package com.example.kokako

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var toolbar: Toolbar? = null
//    var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    fab_add_note.setOnClickListener { view ->
        val select_languge = arrayOf("영어", "일본어", "중국어")
        val mBuilder = AlertDialog.Builder(this)
        var selectedRadioItem = 0
        mBuilder.setTitle("단어장 언어 선택")
            .setSingleChoiceItems(select_languge, selectedRadioItem,
                DialogInterface.OnClickListener { dialog, which ->
                selectedRadioItem = which
                })
            .setNegativeButton("취소", null)
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->
                when(selectedRadioItem){
                    0 -> {
                        var intent = Intent(this, Bus::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        Toast.makeText(this,"일어",Toast.LENGTH_LONG).show()
                    }
                    2 -> {
                        Toast.makeText(this,"중국어",Toast.LENGTH_LONG).show()
                    }

                }
                dialog.dismiss()
            })
            .setNeutralButton("취소") { dialog, which ->
                dialog.cancel()
            }
        val mDialog = mBuilder.create()
        mDialog.show()

    }

    /*   private fun selectLanguagePopup() {
        }*/

        /*---- Hooks ----*/
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById<Toolbar>(R.id.toolbar)

        /*---- Tool Bar ----*/
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        /*---- Navigation Drawer Menu ----*/
        /*
        * ActionBarDrawerToggle은 액션 바 아이콘과 네비게이션 드로어 사이의 적절한 상호작용을 가능하게 한다.*/

        // Hide or show items

        var menu  = navigationView?.menu
        menu?.findItem(R.id.nav_logout)?.isVisible = false
        menu?.findItem(R.id.nav_profile)?.isVisible = false

        navigationView?.bringToFront()  // Bring view in front of everything
        var toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()  // syncState()는 드로어를 왼쪽 또는 오른쪽으로 돌릴 때 회전하는 드로어 아이콘을 동기화하며, syncState()를 제거하려고 하면 동기화가 실패하여 버그가 회전하거나 작동되지도 않는다.
        navigationView?.setNavigationItemSelectedListener(this)

        navigationView?.setCheckedItem(R.id.nav_home)   // 제대로 모르겠음
    }



    override fun onBackPressed() {
        /*if (System.currentTimeMillis() - backPressedTime > 2000) {
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }else if(System.currentTimeMillis() - backPressedTime <2000){
            finish()
        }
        backPressedTime = System.currentTimeMillis()*/

        /*---- When ESC is pressed in a NavigationDrawer ----*/
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            Log.v("","네비게이션ESC")
            drawerLayout!!.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed()
        }
    }

//    NavigationDrawer Menu
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_view -> return true
            R.id.nav_bus -> {
                var intent = Intent(this, Bus::class.java)
                startActivity(intent)
            }
            R.id.nav_share -> {
                Toast.makeText(this,"Share", Toast.LENGTH_SHORT).show()
                return true
            }

        }
        drawerLayout?.closeDrawer(GravityCompat.START) // 네비게이션드로우 닫히고 인텐트전환
        return true // 네이게이션 아이템이 선택되면 true
    }

//    Option Menu
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
    }
}