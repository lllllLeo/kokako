package com.example.kokako

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.databinding.ActivityMainBinding
import com.example.kokako.model.Word
import com.example.kokako.model.WordBook
import com.example.kokako.viewModel.WordBookViewModel
import com.example.kokako.viewModel.WordViewModel
import com.google.android.material.navigation.NavigationView
import com.opencsv.CSVReaderBuilder
import com.opencsv.enums.CSVReaderNullFieldIndicator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.modal_bottom_sheet.view.*
import kotlinx.android.synthetic.main.rv_wordbook_list.view.*
import java.io.*


// TODO: 2021-01-23 종료할 떄 키보드 넣기
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MyWordListRecyclerViewInterface {
    private var backPressedTime: Long = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var myWordRecyclerAdapter: MyWordRecyclerAdapter
    private lateinit var imm: InputMethodManager
    private var wordBookModel: WordBookViewModel? = null
    private var wordModel: WordViewModel? = null
    private var wordBookIdForAdd: Long? = null
    private var excelWordBookId: Long? = null
    private var language = 0

    private var folderName = "12121212121212123" // 어플이름 폴더이름

    companion object {
        const val TAG = "TAG MainActivity"
        const val ADD_WORDBOOK_CODE = 100
        const val COMPLETE_CODE = 10
        const val CANCEL_CODE = 11
        const val GET_WORD_VIEW_CODE = 105
        const val GET_FILE_CODE = 102
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())  // DefaultHandler 지정
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        /*---- Tool Bar ----*/
        binding.toolbarTitle.text = "단어장 목록"
        binding.toolbarTitle.gravity = Gravity.LEFT

        makeFolder()

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
            binding.toolbarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()  // syncState()는 드로어를 왼쪽 또는 오른쪽으로 돌릴 때 회전하는 드로어 아이콘을 동기화하며, syncState()를 제거하려고 하면 동기화가 실패하여 버그가 회전하거나 작동되지도 않는다.
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.nav_home)   // 제대로 모르겠음



        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, -1) as T
            }
        }).get(WordViewModel::class.java)
        wordBookModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
            this.application)).get(WordBookViewModel::class.java)
        wordBookModel?.wordBookListLivedata?.observe(this, { updateWordBookList(it)})


        myWordRecyclerAdapter = MyWordRecyclerAdapter(this)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0){
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }
            override fun onMove(recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder,): Boolean {
                myWordRecyclerAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
            override fun isLongPressDragEnabled(): Boolean {
                return true // true시 롱클릭 감지
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                wordBookModel?.updateAll(myWordRecyclerAdapter.getItem())
                val v : View = (viewHolder as MyWordViewHolder).itemView.my_word_book_list
                v.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorWhite))
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    val v : View = (viewHolder as MyWordViewHolder).itemView.my_word_book_list
                    v.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorTouch))
                }
            }
        }).attachToRecyclerView(binding.rvWordBook)

        binding.rvWordBook.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = myWordRecyclerAdapter
        }

        binding.popUpMenu.setOnClickListener { view ->
            when (view.id) {
                R.id.pop_up_menu -> {
                    onSubMenuPopupClicked(view)
                }
            }
        }
        /*val btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.pop_up_menu -> {
                    onSubMenuPopupClicked(view)
                }
            }
        }
        binding.popUpMenu.setOnClickListener(btnListener)*/



        fab_add_note.setOnClickListener { view ->
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            val container = LinearLayout(this)
            container.orientation = LinearLayout.VERTICAL
            val editTextLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            editTextLp.setMargins(50, 20, 50, 0) // editText Margin
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(50, 0, 50, 0) // editText Margin

            val dialogEditText = EditText(this)
            dialogEditText.requestFocus()
            dialogEditText.maxLines = 1
            dialogEditText.setLines(1)

            val languageItems = resources.getStringArray(R.array.language_array)
//            val languageArray = ArrayList<String>()
            val dialogSpinner = Spinner(this)
            val languageArrayAdapter = ArrayAdapter<String>(this, R.layout.language_spinner, R.id.language_item_spinner)
            languageArrayAdapter.addAll(languageItems.toMutableList())
            dialogSpinner.adapter = languageArrayAdapter
            dialogSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    language = position
                    Log.d(TAG, "onItemSelected: language / position $language / $position")
                    /*
                    영어 0
                    일본어 1
                    중국어 2
                    */
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
//            dialogSpinner.setSelection(0)


            container.addView(dialogEditText, editTextLp)
            container.addView(dialogSpinner, lp)
            val mBuilder = AlertDialog.Builder(view.context)
                .setMessage("작성할 단어장 이름을 입력해주세요.")
                .setView(container)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", null)
                .create()
            mBuilder.setOnShowListener {
                val positiveBtn: Button = mBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeBtn: Button = mBuilder.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeBtn.setOnClickListener(View.OnClickListener {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    mBuilder.dismiss()
                })
                positiveBtn.setOnClickListener(View.OnClickListener {
                    if (dialogEditText.text!!.trim().isEmpty()) {
                        Toast.makeText(this, "단어장 이름을 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
//                        특문제외
                    } else {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        val wordBookName = dialogEditText.text.toString()
                        val intent = Intent(this, AddWordActivity::class.java)
                        wordBookIdForAdd = addWordBook(wordBookName)
                        Log.d("     TAG",
                            "===== MainActivity - floating - 단어장추가 - wordBookIdForAdd 값은 : $wordBookIdForAdd")
                        intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForAdd)
                        startActivityForResult(intent, ADD_WORDBOOK_CODE)
                        mBuilder.dismiss()
                    }
                })
            }
            mBuilder.show()
        }
    }

//    override fun onPause() {
//        super.onPause()
//        Log.d(TAG, "onPause: called")
//        Log.d(TAG, "onPause: myWordRecyclerAdapter.getItem() : ${myWordRecyclerAdapter.getItem()}")
//        wordBookModel?.updateAll(myWordRecyclerAdapter.getItem())
//    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun onSubMenuPopupClicked(view: View) {
        val popup: PopupMenu = PopupMenu(this, view)
        menuInflater.inflate(R.menu.sub_menu, popup.menu)
        popup.setOnMenuItemClickListener(
            PopupMenu.OnMenuItemClickListener {
                Log.d(ViewWordActivity.TAG, "onExportPopupClicked: ${it.itemId}")
                when (it.itemId) {
                    R.id.menu_sort -> {
                    Toast.makeText(this, "정렬하기", Toast.LENGTH_SHORT).show()
                }
                    R.id.menu_excel -> {
                        setupPermissions()
                    }
                }
                true
            })
        popup.show()
    }

    private fun addWordBook(wordBookName: String): Long {
        val wordBook = WordBook(0, wordBookName, 0, 0, (wordBookModel?.getMaxOrder()!! + 1), language)
        val recentInsertedWordBookId: Long = wordBookModel?.insert(wordBook)!!
        Log.d("     TAG",
            "===== MainActivity - addWordBook - recentInsertedWordBookId 값은 : $recentInsertedWordBookId")
        return recentInsertedWordBookId
    }

    private fun deleteSelectedWordBook(position: Int) {
        wordBookModel?.delete(myWordRecyclerAdapter.getItem()[position])
    }

    private fun updateWordBookList(wordBook: List<WordBook>?) {
        Log.d(TAG, "===== MainActivity - updateWordBookList called")
        Log.d(TAG, "===== MainActivity - updateWordBookList wordBook : $wordBook")
        myWordRecyclerAdapter.submitList(wordBook as ArrayList<WordBook>)
        if (wordBook.isEmpty()) {
            binding.emptyWordbook.visibility = View.VISIBLE
        } else {
            binding.emptyWordbook.visibility = View.GONE
        }

    }

    override fun onViewClicked(v: View, adapterPosition: Int) {
        Log.d(TAG, "===== MainActivity - onViewClicked 값은 : " + myWordRecyclerAdapter.getItem()[adapterPosition].id)
        val intent = Intent(this, ViewWordActivity::class.java)
        intent.putExtra("wordBookIdForView", myWordRecyclerAdapter.getItem()[adapterPosition].id)
        intent.putExtra("wordBookNameForView",
        myWordRecyclerAdapter.getItem()[adapterPosition].title)
        startActivityForResult(intent, GET_WORD_VIEW_CODE)
    }

    override fun onPopupMenuWordBookClicked(v: View,myWordBtnViewOption: Button,adapterPosition: Int,) {
        Log.d("     TAG", "===== MainActivity - onPopupMenuClicked called")
        val popup: PopupMenu = PopupMenu(this, myWordBtnViewOption)
        popup.inflate(R.menu.main_item_menu)
        popup.setOnMenuItemClickListener(
            PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        val container = LinearLayout(this)
                        container.orientation = LinearLayout.VERTICAL
                        val editTextLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                        editTextLp.setMargins(50, 20, 50, 0) // editText Margin
                        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.setMargins(50, 0, 50, 0)
                        val dialogEditText = EditText(this)

                        dialogEditText.maxLines = 1
                        dialogEditText.setText(myWordRecyclerAdapter.getItem()[adapterPosition].title.toString())
                        dialogEditText.setSelection(myWordRecyclerAdapter.getItem()[adapterPosition].title!!.length)
                        dialogEditText.setLines(1)
                        dialogEditText.requestFocus()

                        val languageItems = resources.getStringArray(R.array.language_array)
//            val languageArray = ArrayList<String>()
                        val dialogSpinner = Spinner(this)
                        val languageArrayAdapter =
                            ArrayAdapter<String>(this, R.layout.language_spinner, R.id.language_item_spinner)
                        languageArrayAdapter.addAll(languageItems.toMutableList())
                        dialogSpinner.adapter = languageArrayAdapter
                        dialogSpinner.setSelection(myWordRecyclerAdapter.getItem()[adapterPosition].language)
                        dialogSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                language = position
                                Log.d(TAG, "onItemSelected: language / position $language / $position")
                                /*
                                영어 0
                                일본어 1
                                중국어 2
                                */
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                        container.addView(dialogEditText, editTextLp)
                        container.addView(dialogSpinner, lp)
                        val mBuilder = AlertDialog.Builder(this)
                            .setTitle("단어장 수정")
//                            .setMessage("변경할 단어장 이름을 입력해주세요.")
                            .setView(container)
                            .setNegativeButton("취소", null)
                            .setPositiveButton("확인", null)
                            .create()
                        mBuilder.setOnShowListener {
                            val b: Button = mBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
                            b.setOnClickListener(View.OnClickListener {
                                if (dialogEditText.text!!.trim().isEmpty()) {
                                    Toast.makeText(this, "단어장 이름을 정확히 입력해주세요", Toast.LENGTH_SHORT)
                                        .show()
//                        TODO 특문제외
                                } else {
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                                    myWordRecyclerAdapter.getItem()[adapterPosition].title =
                                        dialogEditText.text.toString()
                                    myWordRecyclerAdapter.getItem()[adapterPosition].language = dialogSpinner.selectedItemPosition
                                    Log.d("     TAG",
                                        "===== MainActivity - 편집하기 - else문" + myWordRecyclerAdapter.getItem()[adapterPosition].toString())
                                    updateWordBook(myWordRecyclerAdapter.getItem()[adapterPosition])
                                    Log.d(TAG,
                                        "onPopupMenuWordBookClicked: ${myWordRecyclerAdapter.getItem()[adapterPosition]}")
                                    mBuilder.dismiss()
                                }
                            })
                        }
                        mBuilder.show()
                    }
                    R.id.menu_delete -> {
                        Log.d("     TAG",
                            "===== MainActivity - onRemoveClicked() IN " + myWordRecyclerAdapter.getItem()[adapterPosition].toString())
                        val mBuilder = AlertDialog.Builder(this)
                        mBuilder.setTitle("삭제")
                            .setMessage(myWordRecyclerAdapter.getItem()[adapterPosition].title.toString() + " 단어장을 삭제하시겠습니까?")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { _, _ ->
                                    deleteSelectedWordBook(adapterPosition)
                                    Log.d("TAG",
                                        "MainActivity onRemoveClicked() IN " + myWordRecyclerAdapter.getItem()[adapterPosition].toString() + " 삭제완료")
                                })
                            .setNegativeButton("취소",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.cancel()
                                })
                        mBuilder.show()
                    }
                }
                true
            })
        popup.show()
    }

    private fun updateWordBook(wordBook: WordBook) {
        wordBookModel?.update(wordBook)
    }

    private fun updateWordBookCount(wordBookIdForComplete: Long) {
        wordBookModel?.updateWordBookCount(wordBookIdForComplete)
    }

    private fun deleteCanceledWordBook(wordBookIdForCancel: Long) {
        Log.d(TAG, "deleteCanceledWordBook: In $wordBookIdForCancel")
        wordBookModel?.deleteWordBookById(wordBookIdForCancel)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $requestCode /  $resultCode")
        when (requestCode) {
            ADD_WORDBOOK_CODE -> { // 100
                when (resultCode) {
                    COMPLETE_CODE -> { // 10
                        val wordBookIdForComplete = data!!.getLongExtra("wordBookIdForAddOrEdit", 0)
                        updateWordBookCount(wordBookIdForComplete)
                    }
                    CANCEL_CODE -> { // 11
                        val wordBookIdForCancel = data!!.getLongExtra("wordBookIdForAddOrEdit", 0)
                        Log.d(TAG, "onActivityResult: In $wordBookIdForCancel")
                        deleteCanceledWordBook(wordBookIdForCancel)
                        updateWordBookList(wordBookModel!!.getRecentOrder())
                        // FIXME: 2021-03-10 여기서 라이브데이터 업뎃시켜주는거 만들어야함 get~~근데 정렬메서드 만들면 해결됨. 일단 등록순으로 뽑는거 해야겠다.
                    }
                }
            }
            GET_WORD_VIEW_CODE -> { // 105
                val wordBookIdForView = data!!.getLongExtra("wordBookIdForView", 0)
                updateWordBookCount(wordBookIdForView)
            }
            GET_FILE_CODE -> { // 102
                try {
                    if (data != null) {
                        readCsvFile(data)
                        Toast.makeText(this, "단어장 가져오기 완료", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun makeFolder(): File {
        if (Build.VERSION.SDK_INT > 29) {
                Log.d(TAG, "makeFolder: 1 나는야 30")
//            val dir = File(Environment.getExternalStoragePublicDirectory())
            val dir = applicationContext.getExternalFilesDir(folderName)
            if (!dir!!.exists()) {
                Log.d(TAG, "makeFolder: 1 ${dir.absolutePath}")
                Log.d(TAG, "getSaveFolder:1  폴더 만듬")
                dir.mkdirs()
            }
            Log.d(TAG, "getSaveFolder: 1 dir ${dir.toString()}")
//            /storage/emulated/0/Android/data/com.example.kokako/files/12121212121212123
            return dir
        } else {
            val dir = File(Environment.getExternalStorageDirectory()
                .absolutePath + "/" + folderName)
            if (!dir.exists()) {
                Log.d(TAG, "makeFolder: 2 ${dir.absolutePath}")
                Log.d(TAG, "getSaveFolder: 2 폴더 만듬")
                dir.mkdirs()
            }
            Log.d(TAG, "getSaveFolder: 2 dir ${dir.toString()}")
            return dir
        }

    }

    private fun readCsvFile(data: Intent?) {
        try {
//            if(Build.VERSION.SDK_INT <25) {
//    val folderAndName = "1212121212121212"+
            /*val importWordList = ArrayList<Word>()
            val uri: Uri = data!!.data!!
            Log.d(TAG, "readCsvFile: uri ${uri.path}")
            val fileName = getFileName(uri)!!.split(".")[0]
            Log.d(TAG, "readCsvFile: fileName $fileName")
            val folderAndName = uri.path!!.split(":")[1]
            Log.d(TAG, "readCsvFile: folderAndName $folderAndName")*/
            // TODO: 2021-03-11 뒤로가기해서 파일노선택이면 에러뜸
            val importWordList = ArrayList<Word>()
            val uri: Uri = data!!.data!!
            Log.d(TAG, "readCsvFile: uri ${uri.path}")
            val fileName = getFileName(uri)!!.split(".")[0]
            val fileNameFilenameExtension = getFileName(uri)!!.split(".")[0] + ".csv"
            Log.d(TAG, "readCsvFile: fileName $fileName")
            // TODO: 2021-03-11 단어장 이름 어플 정해서 폴더가 없으면 이걸로 만들어 놓기. 그리고 여기서만 내보내기 ㄱㄴ,
            // TODO: 2021-03-12 도움말에 가져오기 할 때 ~~폴더를 만들고 구글스프레드시트에서 만든 .csv 파일을 넣어주세요
            val folderAndName = "$folderName/$fileNameFilenameExtension"

            val reader =
                CSVReaderBuilder(FileReader(getExternalPath(folderAndName))).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build()
            if (reader != null) {
                excelWordBookId = addWordBook(fileName)
                for (cell in reader.iterator()) {
                    if (cell[0].isNotEmpty() && cell[1].isNotEmpty()) {
                        val word = Word(0, "default word", "default mean", "",0, 9999, excelWordBookId!!)
                        word.word = cell[0]
                        word.mean = cell[1]
                        importWordList.add(word)
                    }
                }
                wordModel?.insertAllDatas(importWordList)
                updateWordBookCount(excelWordBookId!!)
//                Toast.makeText(this, "$fileName 가져오기 완료", Toast.LENGTH_SHORT).show()
            }
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "예상치 못한 오류로 종료되었습니다.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                applicationContext?.contentResolver?.query(uri,
                    null,
                    null,
                    null,
                    null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onBackPressed() {
        val curTime : Long = System.currentTimeMillis();
        val gapTime : Long = curTime - backPressedTime;

        if(0 <= gapTime && 2000 >= gapTime) { super.onBackPressed(); }
        else {
            backPressedTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }


        /*---- When ESC is pressed in a NavigationDrawer ----*/
       /* if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            Log.v("", "네비게이션ESC")
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed()
        }*/
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_view -> return true
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START) // 네비게이션드로우 닫히고 인텐트전환
        return true // 네이게이션 아이템이 선택되면 true
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), GET_FILE_CODE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.MANUFACTURER == "samsung") {
                val systemService =
                    getSystemService(Class.forName("com.samsung.android.content.clipboard.SemClipboardManager"))
                val mContext = systemService.javaClass.getDeclaredField("mContext")
                mContext.isAccessible = true
                mContext.set(systemService, null)
            }
        } catch (e: ClassNotFoundException) {
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }
//ignored }}
    }

    private fun getExternalPath(folderName: String): String? {
        var sdPath = ""
        val ext = Environment.getExternalStorageState()
        sdPath = if (ext == Environment.MEDIA_MOUNTED) {
            Environment.getExternalStorageDirectory().absolutePath + "/" + folderName
        } else {
            "$filesDir/$folderName"
        }
        return sdPath
    }

//    inner class ExceptionHandler : Thread.UncaughtExceptionHandler {
//        override fun uncaughtException(t: Thread?, e: Throwable?) {
//            // 여기에 원하는 동작 구현
//            Log.e(TAG, "uncaughtException: ==================== 비정상 종료 ====================")
//            e?.printStackTrace()
//            android.os.Process.killProcess(android.os.Process.myPid())
//            exitProcess(10)
//        }
//    }
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


