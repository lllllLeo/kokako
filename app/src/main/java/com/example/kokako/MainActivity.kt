package com.example.kokako

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.databinding.ActivityMainBinding
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.model.WordBook
import com.example.kokako.viewModel.WordBookViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_my_word_list.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MyWordListRecyclerViewInterface {
    companion object {
        private const val TAG = "TAG"
    }
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private lateinit var binding : ActivityMainBinding
    //    var backPressedTime: Long = 0
    private var recyclerview: RecyclerView? = null
    private lateinit var myWordRecyclerAdapter: MyWordRecyclerAdapter
    private lateinit var imm : InputMethodManager
    private var model : WordBookViewModel? = null
    private var wordBookDatas : ArrayList<WordBook>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        toolbarBinding = binding.includeToolbar
        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        toolbarBinding.toolbarTitle.text = "단어장 목록"

        /*---- Navigation Drawer Menu ----*/
        /*
        * ActionBarDrawerToggle은 액션 바 아이콘과 네비게이션 드로어 사이의 적절한 상호작용을 가능하게 한다.*/

        // Hide or show items
//        var menu  = binding.navView.menu
//        menu.findItem(R.id.nav_logout)?.isVisible = false
//        menu.findItem(R.id.nav_profile)?.isVisible = false

        binding.navView.bringToFront()  // Bring view in front of everything
        val toggle = ActionBarDrawerToggle(this,
            binding.drawerLayout,
            toolbarBinding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()  // syncState()는 드로어를 왼쪽 또는 오른쪽으로 돌릴 때 회전하는 드로어 아이콘을 동기화하며, syncState()를 제거하려고 하면 동기화가 실패하여 버그가 회전하거나 작동되지도 않는다.
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.nav_home)   // 제대로 모르겠음


        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(WordBookViewModel::class.java)
        model?.wordBookList?.observe(this, {
            updateWordBookList(it)
        })




        fab_add_note.setOnClickListener { view ->

            imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

            val container = LinearLayout(this)
            container.orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(50,0,50,0)

            val dialogEditText = EditText(this)
            dialogEditText.maxLines = 1
            dialogEditText.setLines(1)
            container.addView(dialogEditText, lp)
            val mBuilder = AlertDialog.Builder(view.context)
//                .setTitle("단어장 이름 추가")
                .setMessage("작성할 단어장 이름을 입력해주세요.")
                .setView(container)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", null)
                .create()
            mBuilder.setOnShowListener {
                val b: Button = mBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
                b.setOnClickListener(View.OnClickListener {
                    if (dialogEditText.text!!.trim().isEmpty()) {
                        Toast.makeText(this, "단어장 이름을 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
//                        특문제외
                    } else {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        val wordBookName = dialogEditText.text.toString()
                        val intent = Intent(this, AddWordActivity::class.java)
                        val wordBookIdForAdd = addWordBook(wordBookName)
                        Log.d("     TAG", "===== MainActivity - floating - 단어장추가 - wordBookIdForAdd 값은 : $wordBookIdForAdd")
                        intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForAdd)
                        startActivity(intent)
                        mBuilder.dismiss()
                    }
                })
            }
            mBuilder.show()
        }
    }

    private fun addWordBook(wordBookName: String): Long {
        val wordBook = WordBook(0, wordBookName, 0, 0)
        val recentInsertedWordBookId : Long = model?.insert(wordBook)!!
        Log.d("     TAG", "===== MainActivity - addWordBook - recentInsertedWordBookId 값은 : $recentInsertedWordBookId")
        return recentInsertedWordBookId
    }
    private fun deleteWordBook(position: Int) {
        model?.delete(myWordRecyclerAdapter.getItem()[position])
    }

    private fun updateWordBookList(wordBook: List<WordBook>?) {
        myWordRecyclerAdapter = MyWordRecyclerAdapter(this)
        myWordRecyclerAdapter.submitList(wordBook as ArrayList<WordBook>)
        rv_word_book.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//            recyclerview?.layoutManager
            adapter = myWordRecyclerAdapter
        }
    }

    override fun onViewClicked(v: View, adapterPosition: Int) {
        Log.d("     TAG", "===== MainActivity - onViewClicked 값은 : "+ myWordRecyclerAdapter.getItem()[adapterPosition].id)
        val intent = Intent(this, ViewWordActivity::class.java)
        intent.putExtra("wordBookIdForView", myWordRecyclerAdapter.getItem()[adapterPosition].id)
        intent.putExtra("wordBookNameForView", myWordRecyclerAdapter.getItem()[adapterPosition].title)
        startActivity(intent)
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
            Log.v("", "네비게이션ESC")
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed()
        }
    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_view -> return true
            R.id.nav_bus -> {
                val intent = Intent(this, Bus::class.java)
                startActivity(intent)
            }
            R.id.nav_share -> {
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                return true
            }

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START) // 네비게이션드로우 닫히고 인텐트전환
        return true // 네이게이션 아이템이 선택되면 true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_import -> {
                val intent = Intent(this, ImportActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sort -> {
                Toast.makeText(this, "정렬하기", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }
    override fun onRemoveClicked(view: View, position: Int) {
        Log.d("     TAG", "===== MainActivity - onRemoveClicked() IN "+myWordRecyclerAdapter.getItem()[position].toString())
        val mBuilder = AlertDialog.Builder(view.context)
        mBuilder.setTitle("삭제")
            .setMessage(myWordRecyclerAdapter.getItem()[position].title.toString() + " 단어장을 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { _, _ ->
// TODO: 2021-01-22 여기서 지우면 바로 LIveData 적용됨
                    deleteWordBook(position)
                    Log.d("TAG",
                        "MainActivity onRemoveClicked() IN " + myWordRecyclerAdapter.getItem()[position].toString() + " 삭제완료")
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
        mBuilder.show()
    }
    override fun onDestroy() {
    super.onDestroy()
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.MANUFACTURER == "samsung") {
                val systemService = getSystemService(Class.forName("com.samsung.android.content.clipboard.SemClipboardManager"))
                val mContext = systemService.javaClass.getDeclaredField("mContext")
                mContext.isAccessible = true
                mContext.set(systemService, null)
            }
        }
        catch (e: ClassNotFoundException) {}
        catch (e: NoSuchFieldException) {}
        catch (e: IllegalAccessException) {}
//ignored }}
    }




//    fab_add_note.setOnClickListener { view ->
//        val selectLanguage = arrayOf("영어", "일본어", "중국어")
//        val mBuilder = AlertDialog.Builder(view.context)
//        var selectedRadioItem = 0
//        mBuilder.setTitle("단어장 언어 선택")
//            .setSingleChoiceItems(selectLanguage, selectedRadioItem,
//                DialogInterface.OnClickListener { dialog, which ->
//                    selectedRadioItem = which
//                })
//            .setNegativeButton("취소", null)
//            .setPositiveButton("확인",
//                DialogInterface.OnClickListener { dialog, which ->
//                    when (selectedRadioItem) {
//                        0 -> {
//                            var intent = Intent(this, AddWordActivity::class.java)
//                            startActivity(intent)
//                        }
//                        1 -> {
//                            Toast.makeText(view.context, "일어", Toast.LENGTH_LONG).show()
//                        }
//                        2 -> {
//                            Toast.makeText(view.context, "중국어", Toast.LENGTH_LONG).show()
//                        }
//
//                    }
//                    dialog.dismiss()
//                })
////                .setNeutralButton("취소") { dialog, which ->
////                    dialog.cancel()
////                }
//        val mDialog = mBuilder.create()
//        mDialog.show()
//
//    }
}

