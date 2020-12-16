package com.example.kokako

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.databinding.ActivityMainBinding
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.model.ItemWordDTO
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_my_word_list.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private lateinit var binding : ActivityMainBinding
    //    var backPressedTime: Long = 0
    private var recyclerview: RecyclerView? = null
    private lateinit var myWordRecyclerAdapter: MyWordRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbarBinding = binding.includeToolbar
    /*
        *//*---- Hooks ----*//*
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
    */

        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        toolbarBinding.toolbarTitle.setText("단어장 목록")

        /*---- Navigation Drawer Menu ----*/
        /*
        * ActionBarDrawerToggle은 액션 바 아이콘과 네비게이션 드로어 사이의 적절한 상호작용을 가능하게 한다.*/

        // Hide or show items
//        var menu  = binding.navView.menu
//        menu.findItem(R.id.nav_logout)?.isVisible = false
//        menu.findItem(R.id.nav_profile)?.isVisible = false

        binding.navView.bringToFront()  // Bring view in front of everything
        var toggle = ActionBarDrawerToggle(this,binding.drawerLayout,toolbarBinding.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()  // syncState()는 드로어를 왼쪽 또는 오른쪽으로 돌릴 때 회전하는 드로어 아이콘을 동기화하며, syncState()를 제거하려고 하면 동기화가 실패하여 버그가 회전하거나 작동되지도 않는다.
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.nav_home)   // 제대로 모르겠음




        recyclerview = binding.rvMyItem

        var data = ArrayList<ItemWordDTO>()
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.HEADER, "N1"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "단어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "문법"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "회화표현"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.HEADER, "영어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "토익 단어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "숙어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "헷갈리는 단어"))
        data.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "회화표현"))

        var places : ItemWordDTO = ItemWordDTO(MyWordRecyclerAdapter2.HEADER, "일본어")
        places.invisibleChildren = ArrayList<ItemWordDTO>() // 원래 <> 안에 없엇는데 내가 추가함
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "1~100"))
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "101~200"))
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "201~300"))
        places.invisibleChildren?.add(ItemWordDTO(MyWordRecyclerAdapter2.CHILD, "301~400"))

        data.add(places)

        rv_my_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerview?.layoutManager
            adapter = MyWordRecyclerAdapter2(data)
//            var decorator = DividerItemDecoration(view.context,LinearLayoutManager.VERTICAL)
//            decorator.setDrawable(ContextCompat.getDrawable(context,R.drawable.divider_line)!!)
//            addItemDecoration(decorator)

        }


        fab_add_note.setOnClickListener { view ->
            val selectLanguage = arrayOf("영어", "일본어", "중국어")
            val mBuilder = AlertDialog.Builder(view.context)
            var selectedRadioItem = 0
            mBuilder.setTitle("단어장 언어 선택")
                .setSingleChoiceItems(selectLanguage, selectedRadioItem,
                    DialogInterface.OnClickListener { dialog, which ->
                        selectedRadioItem = which
                    })
                .setNegativeButton("취소", null)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        when (selectedRadioItem) {
                            0 -> {
                                var intent = Intent(this, AddWordActivity::class.java)
                                startActivity(intent)
                            }
                            1 -> {
                                Toast.makeText(view.context, "일어", Toast.LENGTH_LONG).show()
                            }
                            2 -> {
                                Toast.makeText(view.context, "중국어", Toast.LENGTH_LONG).show()
                            }

                        }
                        dialog.dismiss()
                    })
//                .setNeutralButton("취소") { dialog, which ->
//                    dialog.cancel()
//                }
            val mDialog = mBuilder.create()
            mDialog.show()

        }
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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            Log.v("","네비게이션ESC")
            binding.drawerLayout.closeDrawer(GravityCompat.START);
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
        binding.drawerLayout.closeDrawer(GravityCompat.START) // 네비게이션드로우 닫히고 인텐트전환
        return true // 네이게이션 아이템이 선택되면 true
    }
//    Option Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_sort -> {
                Toast.makeText(this,"정렬하기", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_import -> {
                var intent = Intent(this, ImportActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }


}