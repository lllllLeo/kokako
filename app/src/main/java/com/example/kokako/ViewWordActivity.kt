package com.example.kokako

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.databinding.ActivityViewWordBinding
import com.example.kokako.model.CheckBoxData
import com.example.kokako.model.VisibleCheckBoxData
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordBookViewModel
import com.example.kokako.viewModel.WordViewModel
import com.google.android.material.snackbar.Snackbar
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.activity_view_word.*
import kotlinx.android.synthetic.main.activity_view_word.view.*
import kotlinx.android.synthetic.main.modal_bottom_sheet.view.*
import kotlinx.android.synthetic.main.rv_word_list.view.*
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


//  Log.d("     TAG", "===== AddWordActivity")
// TODO: 2021-02-04  단어장 삭제를 만들어말아
// TODO: 2021-02-04 터치하면 색깔바뀌게    98
// FIXME: 2021-02-08
class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface, BottomSheetDialog.BottomSheetInterface {
    //    private lateinit var            toolbarBinding: ActivityToolbarBinding
    private var                     toolbar: Toolbar? = null

    private var                     _binding: ActivityViewWordBinding? = null
    private val                     binding get() = _binding!!
    private lateinit var            viewRecyclerAdapter: ViewWordRecyclerAdapter
    private var                     wordModel: WordViewModel? = null
    private var                     wordBookModel: WordBookViewModel? = null
    private var                     wordBookIdForView: Long = 0
    private var                     wordBookNameForView: String? = null
    private lateinit var            imm : InputMethodManager
    private var                     checkboxCount : Int = 1
    private var                     isDelete : Boolean = false
    private var                     wordList : ArrayList<Word>? = null
    private var                     sortId : Int = 0
    private var                     isWordShowed = false
    private var                     itemVisivled : String? = null
    private var                     folderName = "1111단어장" // 어플이름 폴더이름
    /*private var                     currentShowSnackBarTime : Long = 0
    private var                     mLastClickTime : Long = 0L*/
    companion object {
//        const val                     MIN_CLICK_INTERVAL = 600
        val                         TAG = "TAG ViewWordActivity"
        var                         checkboxList = ArrayList<CheckBoxData>()
        var                         visibleCheckboxList = ArrayList<VisibleCheckBoxData>()
        const val                   EDIT_WORD_CODE = 100
        const val                   GET_WORD_VIEW_CODE = 101
        const val                   COMPLETE_CODE = 10
        const val                   CANCEL_CODE = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityViewWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        wordBookIdForView = intent.getLongExtra("wordBookIdForView", 0)
        wordBookNameForView = intent.getStringExtra("wordBookNameForView")
        val sortItems = resources.getStringArray(R.array.sort_array)
        val hideItems = resources.getStringArray(R.array.hide_array) // 발음 가리기 (3 items)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        /*---- Tool Bar ----*/
        setSupportActionBar(toolbar!!.toolbar)

        toolbar!!.toolbar_title.gravity = Gravity.LEFT
        toolbar!!.toolbar_title.text = wordBookNameForView
        toolbar

        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom).apply {
                ViewCompat.onApplyWindowInsets(view, this)
            }
        }

        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        viewRecyclerAdapter = ViewWordRecyclerAdapter(this)

        checkboxList.clear()
        isDeleteMode(0, -2)


        wordBookModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(
            WordBookViewModel::class.java)
        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForView) as T
            }
        }).get(WordViewModel::class.java)

        wordList = wordModel?.getWordFromWordBookForAddAndEdit(wordBookIdForView)

        wordModel?.wordList?.observe(this, { updateWordList(it, sortId, wordList!!) })
        rv_list_word_view.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(viewRecyclerAdapter.itemCount,0)
//            scrollToPosition(viewRecyclerAdapter.itemCount-1)  안되네 ㅅㅂ
//            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            setHasFixedSize(true)
            adapter = viewRecyclerAdapter
        }


//        wordList = viewRecyclerAdapter.getItem()
        Log.d(TAG, "onCreate: $wordList")


        val btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.view_back_btn -> {
                    goBackToPreviousActivity()
                }
                R.id.view_cancel_btn -> {
                    isDeleteMode(0, -1)
                }
                R.id.view_add_or_edit_btn -> {
                    val intent = Intent(this, AddWordActivity::class.java)
                    intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForView)
                    intent.putExtra("checkActivity", true)
                    startActivityForResult(intent, EDIT_WORD_CODE)
                }
                R.id.view_delete_btn -> {
                    deleteCheckedWordDialog(checkboxList)
                }
            }
        }

        view_back_btn.setOnClickListener(btnListener)
        view_cancel_btn.setOnClickListener(btnListener)
        view_add_or_edit_btn.setOnClickListener(btnListener)
        view_delete_btn.setOnClickListener(btnListener)


        sort_spinner.adapter = ArrayAdapter(this, R.layout.sort_spinner, sortItems)
        sort_spinner.dropDownVerticalOffset = dipToPixels(30f).toInt()
        sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                println(sortItems[position])
                getSortWhen(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println(sortItems[0])
            }
        }
        hide_spinner.adapter = ArrayAdapter(this, R.layout.hide_spinner, hideItems)
        hide_spinner.dropDownVerticalOffset = dipToPixels(30f).toInt()
        hide_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                println(hideItems[position])
                when (position) {
                    0 -> {
                        viewRecyclerAdapter.showAndHide(0)
                    } // 전체보기
                    1 -> {
                        viewRecyclerAdapter.showAndHide(1)
                    } // 단어 가리기
                    2 -> {
                        viewRecyclerAdapter.showAndHide(2)
                    } // 뜻 가리기
                    3 -> {
                        viewRecyclerAdapter.showAndHide(3)
                    } // 랜덤
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println(hideItems[0])
            }
        }


/*        val sortArrayAdapter = object : ArrayAdapter<String>(this, R.layout.sort_spinner) {
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).text = ""
                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).hint = getItem(count)
//                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).height = ViewGroup.LayoutParams.MATCH_PARENT
                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).gravity = Gravity.CENTER
                }
                return v
            }
            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }
        sortArrayAdapter.addAll(sortItems.toMutableList())
        sortArrayAdapter.add("정렬")
        sort_spinner.adapter = sortArrayAdapter
        sort_spinner.setSelection(sortArrayAdapter.count)
        sort_spinner.dropDownVerticalOffset = dipToPixels(30f).toInt()
        sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                val v = sortArrayAdapter.getView(position, view, parent!!)
                (v.findViewById<View>(R.id.sort_item_spinner) as TextView).gravity = Gravity.CENTER
                getSortWhen(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val hideArrayAdapter = object : ArrayAdapter<String>(this, R.layout.hide_spinner) {
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById<View>(R.id.hide_item_spinner) as TextView).text = ""
                    (v.findViewById<View>(R.id.hide_item_spinner) as TextView).hint = getItem(count)
//                    (v.findViewById<View>(R.id.hide_item_spinner) as TextView).height = ViewGroup.LayoutParams.MATCH_PARENT
                    (v.findViewById<View>(R.id.hide_item_spinner) as TextView).gravity = Gravity.CENTER
                }
                return v
            }
            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }
        hideArrayAdapter.addAll(hideItems.toMutableList())
        hideArrayAdapter.add("보기/가리기")
        hide_spinner.adapter = hideArrayAdapter
        hide_spinner.setSelection(hideArrayAdapter.count)
        hide_spinner.dropDownVerticalOffset = dipToPixels(30f).toInt()
        hide_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long,) {
                val v = hideArrayAdapter.getView(position, view, parent!!)
                (v.findViewById<View>(R.id.hide_item_spinner) as TextView).gravity = Gravity.CENTER
                when (position) {
                    0 -> { viewRecyclerAdapter.showAndHide(0) } // 전체보기
                    1 -> { viewRecyclerAdapter.showAndHide(1) } // 단어 가리기
                    2 -> { viewRecyclerAdapter.showAndHide(2) } // 뜻 가리기
                    3 -> { viewRecyclerAdapter.showAndHide(3) } // 랜덤
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }*/

            // TODO: 2021-01-23  putExtra(wordBookIdForView) -> 순서 정하는 Activity
//        fab_add_note.setOnClickListener { view -> }

    }

//  폴더생성
    private fun getSaveFolder(): File? {
        val dir = File(Environment.getExternalStorageDirectory()
            .absolutePath + "/" + folderName)
        if (!dir.exists()) {
            Log.d(TAG, "getSaveFolder: 폴더 만듬")
            dir.mkdirs()
        }
        Log.d(TAG, "getSaveFolder: dir ${dir.toString()}")
        return dir
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
    @SuppressLint("SimpleDateFormat")
    private fun writeCsvFile(filePath: String) {
        val time = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = Date(time)
        val strTime = simpleDateFormat.format(date)

        val folderPath = getExternalPath(folderName)
        val wordListForCSV = wordModel?.getWordFromWordBookForAddAndEdit(wordBookIdForView)
        Log.d(TAG, "writeCsvFile: $wordListForCSV")
        val csv = ArrayList<Array<String>>()
        for (i in 0 until wordListForCSV!!.size) {
            val item : Array<String> = arrayOf<String>(wordListForCSV[i].word.toString(), wordListForCSV[i].mean.toString(), wordListForCSV[i].option.toString())
            csv.add(item)
        }
        Log.d(TAG, "writeCsvFile: $folderPath/내보내기_${strTime}_$filePath")
        val writer = CSVWriter(FileWriter("$folderPath/내보내기_${strTime}_$filePath"))
        writer.writeAll(csv)
        writer.close()
        Log.d(TAG, "writeCsvFile: 완료")
        Toast.makeText(this, "내보내기 완료", Toast.LENGTH_SHORT).show()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
//            val path = "/storage/emulated/0"
//            val file = File("$path/number.txt")
//            val pln = file.readText()
//            plnText.text = pln
            getSaveFolder()
            writeCsvFile("$wordBookNameForView.csv")
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            Toast.makeText(this, "파일 접근 권한이 없습니다. 권한을 허용해주세요 ", Toast.LENGTH_SHORT).show()
        }
    }












    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater : MenuInflater = menuInflater
        menuInflater.inflate(R.menu.view_sub_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> {

            }
            R.id.menu_export -> {
                setupPermissions()

            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun updateWordList(word: List<Word>, sortId: Int, wordList: ArrayList<Word>) {
        Log.d(TAG, "updateWordList: word $word")
        Log.d(TAG, "updateWordList: sortId $sortId")
        Log.d(TAG, "updateWordList: wordList $wordList")
        // TODO: 2021-02-15 별표 개수에서 막히네  별표순으로 바꾸면 별표개수만 나오는데 지우면 단어개수0되고
        if (sortId == 0) {
            Log.d(TAG, "updateWordList: if문")
            viewRecyclerAdapter.submitList(word as ArrayList<Word>)
            binding.currentCount.text = "입력한 단어  " + word.size.toString()
            if(word.isNotEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyIcon.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyIcon.visibility = View.VISIBLE
            }
        } else {
            Log.d(TAG, "updateWordList: else문") // 리사이클러뷰만 살아잇는거임(이전데이터) 실제데이터는 사라짐
            viewRecyclerAdapter.submitList(wordList)
            binding.currentCount.text = "입력한 단어  " + wordList.size.toString()
            if(wordList.isNotEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyIcon.visibility = View.GONE
            } else {
                if(sortId == 1) { binding.emptyText.text = "북마크한 단어가 없습니다" }
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyIcon.visibility = View.VISIBLE
            }
        }
    }
    // FIXME: 2021-02-08 체크박스 클릭시 색상변경
    @SuppressLint("SetTextI18n")
    override fun onCheckboxClicked(v: View, adapterPosition: Int) {
        if(v.view_check.isChecked) {
            checkboxList[adapterPosition].checked = true
            checkboxCount += 1
        } else {
            checkboxList[adapterPosition].checked = false
            checkboxCount -= 1
        }
        v.view_word_book_list.isSelected = checkboxList[adapterPosition].checked
        binding.ckboxCountTv.text = "${checkboxCount.toString()} 개 선택됨"
    }
    override fun onViewClicked(v: View, adapterPosition: Int) {
        if(v.view_check.visibility==View.VISIBLE) {
            v.view_check.isChecked = !v.view_check.isChecked
            if (v.view_check.isChecked) {
                checkboxList[adapterPosition].checked = true
                /*v.view_word_book_list.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSelectItem))
                Log.d(TAG, "onViewClicked: adapterPosition 는 $adapterPosition")*/
                checkboxCount += 1
            } else {
                checkboxList[adapterPosition].checked = false
//                v.view_word_book_list.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
                checkboxCount -= 1
            }
            v.view_word_book_list.isSelected = checkboxList[adapterPosition].checked
            binding.ckboxCountTv.text = "${checkboxCount.toString()} 개 선택됨"
        } else {
            val wordForBottomSheet = viewRecyclerAdapter.getItem()[adapterPosition]
            val bottomSheetDialog = BottomSheetDialog(wordForBottomSheet, adapterPosition)
            bottomSheetDialog.show(supportFragmentManager, "example-bottom-sheet")
        }
    }
    private fun deleteCheckedWordDialog(checkboxList: ArrayList<CheckBoxData>) {
        Log.d(TAG, "deleteWordDialog 사이즈 : ${checkboxList.size}")
        for(i in 0 until checkboxList.size) {
            Log.d(TAG, "deleteWordDialog: 뭡니까? $i 는 ${checkboxList[i].checked}")
        }
        val mBuilder = AlertDialog.Builder(this)
//        mBuilder.setTitle("삭제")
            // TODO: 2021-02-12 0개 예외처리
            .setMessage("선택된 $checkboxCount 개의 단어를 삭제합니다.\n정말 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { _, _ ->
                    var i = 0
                    var j = 0
                    while (checkboxList.size != 0 && i < checkboxList.size) {
                        if (checkboxList[i].checked) {
                            // TODO: 2021-02-07 deleteAll(ArrayList<Word>) 만들기
                            wordModel?.delete(viewRecyclerAdapter.getItem()[i])
                            checkboxList.drop(i)
                            j = 1
                        }
                        i += 1
                    }
                    if (j == 1) {
                        getSortWhen(sortId)
                        Toast.makeText(this, "단어 삭제 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "삭제할 단어를 체크해주세요", Toast.LENGTH_SHORT).show()
                    }

                    isDeleteMode(0, -1)
                }
            )
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
        mBuilder.show()
    }
    override fun onWordLongClicked(v: View, adapterPosition: Int) {
        if (v.view_check.visibility != View.VISIBLE) {
            isDeleteMode(1, adapterPosition)
//            v.view_word_book_list.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSelectItem))
        }
    }

    override fun onVisibilityCheckboxClicked(v: View,_visibilityOptions: Int,wordLayout: LinearLayout,meanLayout: LinearLayout,adapterPosition: Int) {
        Log.d(TAG, "onVisibilityCheckboxClicked: ${v.visible_check.isChecked}")
        if(v.visible_check.isChecked) {
            visibleCheckboxList[adapterPosition].checked = true
            Log.d(TAG, "onVisibilityCheckboxClicked: if")
        } else {
            visibleCheckboxList[adapterPosition].checked = false
            Log.d(TAG, "onVisibilityCheckboxClicked: else")
        }
        if (_visibilityOptions == 1) {
            wordLayout.isSelected = visibleCheckboxList[adapterPosition].checked
        } else if (_visibilityOptions == 2){
            meanLayout.isSelected = visibleCheckboxList[adapterPosition].checked
        }

    /* if (!isWordShowed) {
            if ((v.findViewById<View>(R.id.view_word) as TextView).visibility == View.INVISIBLE) {
                (v.findViewById<View>(R.id.view_word) as TextView).visibility = View.VISIBLE
                v.findViewById<ImageView>(R.id.word_show_and_hide).setBackgroundResource(R.drawable.ic_baseline_visibility_24)
                itemVisivled = "word"
            } else if ((v.findViewById<View>(R.id.view_mean) as TextView).visibility == View.INVISIBLE) {
                (v.findViewById<View>(R.id.view_mean) as TextView).visibility = View.VISIBLE
                v.findViewById<ImageView>(R.id.word_show_and_hide).setBackgroundResource(R.drawable.ic_baseline_visibility_24)
                itemVisivled = "mean"
            }
            isWordShowed = true
        } else {
            if (itemVisivled.equals("word")) {
                v.view_word.visibility = View.INVISIBLE
                v.findViewById<ImageView>(R.id.word_show_and_hide).setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
            } else {
                v.view_mean.visibility = View.INVISIBLE
                v.findViewById<ImageView>(R.id.word_show_and_hide).setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
            }
            isWordShowed = false
        }*/
    }



    @SuppressLint("SetTextI18n")
    private fun isDeleteMode(num: Int, adapterPosition: Int) {
        viewRecyclerAdapter.updateCheckbox(num, adapterPosition) // 처음 LongClick하면 들어옴
        if (num == 1) {
            isDelete = true
            viewRecyclerAdapter.showAndHide(0)
            binding.viewAddOrEditBtn.visibility = View.GONE
            binding.viewBackBtn.visibility = View.GONE
            binding.viewDeleteBtn.visibility = View.VISIBLE
            binding.viewCancelBtn.visibility = View.VISIBLE
            binding.ckboxCountTv.visibility = View.VISIBLE
            binding.ckboxCountTv.text = "${checkboxCount.toString()} 개 선택됨"
//            toolbar!!.toolbar_title.text = "${checkboxCount.toString()} 개 선택됨"

//            binding.fabTestWord.visibility = View.INVISIBLE
            binding.hideSpinner.visibility = View.INVISIBLE
            binding.sortSpinner.visibility = View.INVISIBLE
            binding.currentCount.visibility = View.GONE
        } else {
            isDelete = false
            checkboxCount = 1
            binding.viewAddOrEditBtn.visibility = View.VISIBLE
            binding.viewBackBtn.visibility = View.VISIBLE
            binding.viewDeleteBtn.visibility = View.GONE
            binding.viewCancelBtn.visibility = View.GONE
            binding.ckboxCountTv.visibility = View.GONE
//            toolbar!!.toolbar_title.text = wordBookNameForView

//            binding.fabTestWord.visibility = View.VISIBLE
            binding.hideSpinner.visibility = View.VISIBLE
            binding.sortSpinner.visibility = View.VISIBLE
            binding.currentCount.visibility = View.VISIBLE
        }
        viewRecyclerAdapter.notifyDataSetChanged()
    }
    override fun onBackPressed() {
        if(isDelete) { // 롱클릭에서 뒤로가기하면 2번 호출되는지 확인하기
            isDeleteMode(0, -1)
            binding.hideSpinner.setSelection(0)
        } else
            goBackToPreviousActivity()
    }
    private fun goBackToPreviousActivity() {
        val intent = Intent()
        intent.putExtra("wordBookIdForView", wordBookIdForView)
        setResult(GET_WORD_VIEW_CODE, intent)
        finish()
    }
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("     TAG", "===== ViewWordActivity - onActivityResult called")
        super.onActivityResult(requestCode, resultCode, data)
            when (requestCode) {
                EDIT_WORD_CODE -> {
                    when (resultCode) {
                        COMPLETE_CODE -> {
                            val wordBookIdForAddOrEdit = data!!.getLongExtra("wordBookIdForAddOrEdit", 0)
                            val word = data.getParcelableArrayListExtra<Word>("word")
                            wordModel?.deleteWordById(wordBookIdForAddOrEdit)
                            wordModel?.insertAllDatas(word)
                            wordBookModel?.updateWordBookCount(wordBookIdForAddOrEdit)
                            getSortWhen(sortId)
                        }
                        CANCEL_CODE -> {
                        }
                    }
                }
            }
    }
    override fun onFavoriteButtonClicked(v: View, adapterPosition: Int) {
       /* val elapsedTime = currentShowSnackBarTime - mLastClickTime
        Log.d(TAG, "onStarClicked: currentShowSnackBarTime - mLastClickTime $currentShowSnackBarTime - $mLastClickTime = $elapsedTime")
        mLastClickTime = currentShowSnackBarTime

        Log.d(TAG, "onStarClicked: elapsedTime > MIN_CLICK_INTERVAL $elapsedTime  $MIN_CLICK_INTERVAL")
        if (elapsedTime > MIN_CLICK_INTERVAL) {*/
            val word: Word = viewRecyclerAdapter.getItem()[adapterPosition]
            if (word.bookMarkCheck == 0) {
                word.bookMarkCheck = 1
            } else {
                word.bookMarkCheck = 0
            }
            updateFavorite(word)
    }
    private fun updateFavorite(word: Word) {
        wordModel?.updateFavoriteChecked(word)
    }
















    override fun onPopupMenuWordClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int) {
        Log.d("     TAG", "===== MainActivity - onPopupMenuClicked called")
        val popup: PopupMenu = PopupMenu(this, myWordBtnViewOption)
        val currentValue = viewRecyclerAdapter.getItem()[adapterPosition]
        popup.inflate(R.menu.main_item_menu)
        popup.setOnMenuItemClickListener(
            PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        val container = LinearLayout(this)
                        container.orientation = LinearLayout.VERTICAL
                        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.setMargins(50, 0, 50, 0) // editText Margin

                        val wordDialogTextView = TextView(this)
                        val wordDialogEditText = EditText(this)
                        val meanDialogTextView = TextView(this)
                        val meanDialogEditText = EditText(this)

                        wordDialogTextView.text = "단어"

                        wordDialogEditText.maxLines = 1
                        wordDialogEditText.setLines(1)
                        wordDialogEditText.setText(currentValue.word)
                        wordDialogEditText.width = 0
                        wordDialogEditText.setSelection(currentValue.word?.length!!)

                        meanDialogTextView.text = "뜻"

                        meanDialogEditText.maxLines = 1
                        meanDialogEditText.setLines(1)
                        meanDialogEditText.setText(currentValue.mean)

                        container.addView(wordDialogTextView, lp)
                        container.addView(wordDialogEditText, lp)
                        container.addView(meanDialogTextView, lp)
                        container.addView(meanDialogEditText, lp)
                        val mBuilder = AlertDialog.Builder(this)
//                .setTitle("단어 편집")
                            .setMessage("단어 편집")
                            .setView(container)
                            .setNegativeButton("취소", null)
                            .setPositiveButton("확인", null)
                            .create()
                        mBuilder.setOnShowListener {
                            // TODO: 2021-01-31 버튼 디자인 맘에안듦
                            val wordDialogButton: Button =
                                mBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
                            wordDialogButton.setOnClickListener(View.OnClickListener {
                                if (wordDialogEditText.text!!.trim()
                                        .isEmpty() || meanDialogEditText.text!!.trim().isEmpty()
                                ) {
                                    Toast.makeText(this, "단어, 뜻을 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
//                        TODO 특문제외
                                } else {
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                                    currentValue.word = wordDialogEditText.text.toString()
                                    currentValue.mean = meanDialogEditText.text.toString()
                                    Log.d("     TAG",
                                        "===== ViewWordActivity - 편집하기 - else문$currentValue")
                                    wordModel?.update(currentValue)
                                    mBuilder.dismiss()
                                }
                            })
                        }
                        mBuilder.show()
                    }
                    R.id.menu_delete -> {
                        val mBuilder = AlertDialog.Builder(this)
                        mBuilder.setTitle("삭제")
                            .setMessage("단어 : " + currentValue.word.toString() + "\n뜻 : " + currentValue.mean.toString() + "\n이 단어 항목을 삭제하시겠습니까?")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { _, _ ->
                                    wordModel?.delete(currentValue)
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
    override fun onUpdateButtonClicked(view: View, word: Word, isUpdated: Boolean) : Boolean {
//                        TODO 특문제외
        var _isUpdated = isUpdated
        if (view.bsd_input_word.text!!.trim().isNotEmpty() && view.bsd_input_mean.text!!.trim().isNotEmpty()) {
            if (!(word.word!!).equals(view.bsd_input_word.text!!) && !(word.mean!!).equals(view.bsd_input_mean.text!!)) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                word.word = view.bsd_input_word.text.toString()
                word.mean = view.bsd_input_mean.text.toString()
                wordModel?.update(word)
                _isUpdated = true
            }
        } else {
            Toast.makeText(this, "단어, 뜻을 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
            _isUpdated = false
        }
        return _isUpdated
    }
    override fun onDeleteButtonClicked(word: Word, position: Int) {
/*        val mDeletedPosition = position
        val mDeletedWord = viewRecyclerAdapter.getItem()[position]
        viewRecyclerAdapter.getItem().removeAt(position)
        viewRecyclerAdapter.notifyItemRemoved(position)
        binding.currentCount.text = "입력한 단어  " + (viewRecyclerAdapter.itemCount).toString()
        showActionSnackBar(word, mDeletedWord, mDeletedPosition)*/

        wordModel?.delete(word)
        getSortWhen(sortId)
        showActionSnackBar(word)
    }
//    private fun showActionSnackBar(word: Word, mDeletedWord: Word, mDeletedPosition: Int) {
    private fun showActionSnackBar(word: Word) {
        /*currentShowSnackBarTime = System.currentTimeMillis()
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Log.d(TAG, "showActionSnackBar: currentShowSnackBarTime $currentShowSnackBarTime")*/

        val actionSnackbar = Snackbar.make(binding.root, "단어 삭제 완료", Snackbar.LENGTH_LONG)
        actionSnackbar.setAction("취소") {
/*            viewRecyclerAdapter.getItem().add(mDeletedPosition, mDeletedWord)
            viewRecyclerAdapter.notifyItemInserted(mDeletedPosition)
            binding.currentCount.text = "입력한 단어  " + (viewRecyclerAdapter.itemCount).toString()*/
            wordModel?.insert(word)
            getSortWhen(sortId)
        }
/*        actionSnackbar.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT) {
                    wordModel?.delete(word)
                }
            }
        })*/
        actionSnackbar.show()
    }
    private fun getSortWhen(sortId: Int) {
        when (sortId) {
            0 -> {
                getRecentOrder()
            } // 최신순
            1 -> {
                getWordFavoriteOrder()
            } // 별표순
            2 -> {
                getWordAscendingOrder()
            } // 단어순 ▲
            3 -> {
                getWordDescendingOrder()
            } // 단어순 ▼
            4 -> {
                getMeanAscendingOrder()
            } // 뜻 ▲
            5 -> {
                getMeanDescendingOrder()
            } // 뜻 ▼
            6 -> {
                getRandomOrder()
            } // 랜덤
        }
    }
    private fun getRecentOrder() {
        sortId = 0
        wordList = wordModel?.getWordFromWordBookForAddAndEdit(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordFavoriteOrder() {
        sortId = 1
        wordList = wordModel?.getWordFavoriteOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordAscendingOrder() {
        sortId = 2
        wordList = wordModel?.getWordAscendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordDescendingOrder() {
        sortId = 3
        wordList = wordModel?.getWordDescendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getMeanAscendingOrder() {
        sortId = 4
        wordList = wordModel?.getMeanAscendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getMeanDescendingOrder() {
        sortId = 5
        wordList = wordModel?.getMeanDescendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getRandomOrder() {
        sortId = 6
        wordList = wordModel?.getRandomOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
}