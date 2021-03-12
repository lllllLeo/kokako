package com.example.kokako

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
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

class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface, BottomSheetDialog.BottomSheetInterface, TestSettingDialog.OnDataPass {
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
    private var                     sortSelectedIndex = 0
    private var                     hideSelectedIndex = 0
    private var                     testValue : ArrayList<String>? = null

    private var                     itemToHide : MenuItem? = null
    /*private var                     currentShowSnackBarTime : Long = 0
    private var                     mLastClickTime : Long = 0L*/
    companion object {
//        const val                     MIN_CLICK_INTERVAL = 600
        const val                         TAG = "TAG ViewWordActivity"
        var                         checkboxList = ArrayList<CheckBoxData>()
        var                         visibleCheckboxList = ArrayList<VisibleCheckBoxData>()
        const val                   EDIT_WORD_CODE = 100
        const val                   GET_WORD_VIEW_CODE = 105
        const val                   COMPLETE_CODE = 10
        const val                   CANCEL_CODE = 11
        const val                   TEST_WORD_CODE = 102
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
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
        toolbar = findViewById<Toolbar>(R.id.toolbar_view)
        /*---- Tool Bar ----*/
        setSupportActionBar(toolbar!!.toolbar_view)

        toolbar!!.toolbar_title.gravity = Gravity.LEFT
        toolbar!!.toolbar_title.text = wordBookNameForView


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

        wordList = wordModel?.getRecentOrder(wordBookIdForView)


        wordModel?.wordListLivedata?.observe(this, { updateWordList(it, sortId, wordList!!) })
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
                R.id.view_export_btn -> {
                    onExportPopupClicked(view)
                }
            }
        }

        view_back_btn.setOnClickListener(btnListener)
        view_cancel_btn.setOnClickListener(btnListener)
        view_add_or_edit_btn.setOnClickListener(btnListener)
        view_delete_btn.setOnClickListener(btnListener)
        view_export_btn.setOnClickListener(btnListener)

        val sortArrayAdapter = object : ArrayAdapter<String>(this, R.layout.sort_spinner, R.id.sort_item_spinner) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv : TextView = super.getDropDownView(position, convertView, parent) as TextView
                if(position == sortSelectedIndex) {
                    tv.setTextColor(Color.BLACK)
                    tv.setTypeface(null, Typeface.BOLD)
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_24_black, 0);
                    /*DrawableCompat.setTint(
                        DrawableCompat.wrap(context.getDrawable(R.drawable.ic_baseline_check_24)!!),
                        ContextCompat.getColor(context, R.color.colorBlack)
                    )*/
                }
                return tv
            }
        }
        sortArrayAdapter.addAll(sortItems.toMutableList())
        sort_spinner.adapter = sortArrayAdapter
//        sort_spinner.setSelection(sortArrayAdapter.count)
        sort_spinner.setSelection(0)
        sort_spinner.dropDownVerticalOffset = dipToPixels(42f).toInt()
        sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortSelectedIndex = position
                getSortWhen(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val hideArrayAdapter = object : ArrayAdapter<String>(this, R.layout.hide_spinner, R.id.hide_item_spinner) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv : TextView = super.getDropDownView(position, convertView, parent) as TextView
                if(position == hideSelectedIndex) {
                    tv.setTextColor(Color.BLACK)
                    tv.setTypeface(null, Typeface.BOLD)
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_24_black, 0);
                    /*DrawableCompat.setTint(
                        DrawableCompat.wrap(context.getDrawable(R.drawable.ic_baseline_check_24)!!),
                        ContextCompat.getColor(context, R.color.colorBlack)
                    )*/
                }
                return tv
            }
        }
        hideArrayAdapter.addAll(hideItems.toMutableList())
        hide_spinner.adapter = hideArrayAdapter
        hide_spinner.setSelection(0)
        hide_spinner.dropDownVerticalOffset = dipToPixels(42f).toInt()
        hide_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                hideSelectedIndex = position
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

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fab_test_word.setOnClickListener { _ ->
            if(wordList!!.size != 0) {
                openTestSettingDialog()
            } else {
                Toast.makeText(this, "작성된 단어가 없습니다. 단어를 추가해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportDialog() {
        val bundle = Bundle()
        val exportDialog = ExportDialog()
        bundle.putLong("wordBookIdForView", wordBookIdForView)
        bundle.putString("wordBookNameForView", wordBookNameForView)
        val fragmentManager : FragmentManager = supportFragmentManager
        exportDialog.arguments = bundle
        exportDialog.show(fragmentManager, "ExportDialog")
    }

    private fun openTestSettingDialog() {
        TestSettingDialog.display(supportFragmentManager)
    }

    override fun onDataPass(data: ArrayList<String>) {
        testValue = data
        Log.d(TAG, "onDataPass: testValue는 ${testValue.toString()}")
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("wordBookIdForTest", wordBookIdForView)
        intent.putExtra("testScope", testValue!![0])
        intent.putExtra("testCategory", testValue!![1])
        intent.putExtra("testSort", testValue!![2])
        startActivityForResult(intent, TEST_WORD_CODE)
    //            start result해야함 받고 livedata
    }

/*    //  폴더생성
    private fun getSaveFolder(): File? {
        val dir = File(Environment.getExternalStorageDirectory()
            .absolutePath + "/" + folderName)
        if (!dir.exists()) {
            Log.d(TAG, "getSaveFolder: 폴더 만듬")
            dir.mkdirs()
        }
        Log.d(TAG, "getSaveFolder: dir ${dir.toString()}")
        return dir
    }*/




    @SuppressLint("SetTextI18n")
    private fun updateWordList(word: List<Word>, sortId: Int, wordList: ArrayList<Word>) {
        Log.d(TAG, "updateWordList: word $word")
        Log.d(TAG, "updateWordList: sortId $sortId")
        Log.d(TAG, "updateWordList: wordList $wordList")
        // TODO: 2021-02-15 별표 개수에서 막히네  별표순으로 바꾸면 별표개수만 나오는데 지우면 단어개수0되고
        if (sortId == 0) {
            Log.d(TAG, "updateWordList: if문")
            viewRecyclerAdapter.submitList(word as ArrayList<Word>)
            binding.currentCount.text = word.size.toString()
            binding.currentCount2.text = "개"
            if(word.isNotEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyIcon.visibility = View.GONE
                binding.fabTestWord.visibility = View.VISIBLE
            } else {
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyIcon.visibility = View.VISIBLE
                binding.fabTestWord.visibility = View.GONE
            }
        } else {
            Log.d(TAG, "updateWordList: else문") // 리사이클러뷰만 살아잇는거임(이전데이터) 실제데이터는 사라짐
            viewRecyclerAdapter.submitList(wordList)
            binding.currentCount.text = word.size.toString()
            binding.currentCount2.text = "개"
            if(wordList.isNotEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyIcon.visibility = View.GONE
                binding.fabTestWord.visibility = View.VISIBLE
            } else {
                if(sortId == 1) { binding.emptyText.text = "북마크한 단어가 없습니다" }
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyIcon.visibility = View.VISIBLE
                binding.fabTestWord.visibility = View.GONE
            }
        }
    }
    // FIXME: 2021-02-08 체크박스 클릭시 색상변경
    @SuppressLint("SetTextI18n")
    override fun onCheckboxClicked(v: View, wordMeanLayout: LinearLayout, adapterPosition: Int) {
        if(v.view_check.isChecked) {
            checkboxList[adapterPosition].checked = true
            checkboxCount += 1
        } else {
            checkboxList[adapterPosition].checked = false
            checkboxCount -= 1
        }
        wordMeanLayout.isSelected = checkboxList[adapterPosition].checked
        binding.toolbarTitle.text = "${checkboxCount.toString()} 개 선택됨"
    }
    override fun onViewClicked(v: View, wordMeanLayout: LinearLayout, adapterPosition: Int) {
        if(v.view_check.visibility==View.VISIBLE) {
            v.view_check.isChecked = !v.view_check.isChecked
            if (v.view_check.isChecked) {
                checkboxList[adapterPosition].checked = true
                checkboxCount += 1
            } else {
                checkboxList[adapterPosition].checked = false
                checkboxCount -= 1
            }
            wordMeanLayout.isSelected = checkboxList[adapterPosition].checked
            binding.toolbarTitle.text = "${checkboxCount.toString()} 개 선택됨"
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

    override fun onVisibilityCheckboxClicked(
        v: View, _visibilityOptions: Int, wordTextView: TextView, meanTextView: TextView, adapterPosition: Int,
    ) {
        Log.d(TAG, "onVisibilityCheckboxClicked: ${v.visible_check.isChecked}")
        if (v.visible_check.isChecked) {
            visibleCheckboxList[adapterPosition].checked = true
            Log.d(TAG, "onVisibilityCheckboxClicked: if")
        } else {
            visibleCheckboxList[adapterPosition].checked = false
            Log.d(TAG, "onVisibilityCheckboxClicked: else")
        }
        if (_visibilityOptions == 1) {
            if (visibleCheckboxList[adapterPosition].checked) {
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.VISIBLE
            } else {
                wordTextView.visibility = View.INVISIBLE
                meanTextView.visibility = View.VISIBLE
            }
        } else if (_visibilityOptions == 2) {
            if (visibleCheckboxList[adapterPosition].checked) {
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.VISIBLE
            } else {
                wordTextView.visibility = View.VISIBLE
                meanTextView.visibility = View.INVISIBLE
            }
        }
        // FIXME: 2021-02-24 랜덤
    }
    @SuppressLint("SetTextI18n")
    private fun isDeleteMode(num: Int, adapterPosition: Int) {
        viewRecyclerAdapter.updateCheckbox(num, adapterPosition) // 처음 LongClick하면 들어옴
        if (num == 1) {
            isDelete = true
            viewRecyclerAdapter.showAndHide(0)
            binding.viewAddOrEditBtn.visibility = View.GONE
            binding.viewBackBtn.visibility = View.GONE
            binding.viewExportBtn.visibility = View.GONE
            binding.viewDeleteBtn.visibility = View.VISIBLE
            binding.viewCancelBtn.visibility = View.VISIBLE
            binding.ckboxCountTv.visibility = View.VISIBLE
            binding.toolbarTitle.text = "${checkboxCount.toString()} 개 선택됨"

//            binding.fabTestWord.visibility = View.INVISIBLE
            binding.hideSpinner.visibility = View.INVISIBLE
            binding.sortSpinner.visibility = View.INVISIBLE
            binding.currentCount.visibility = View.GONE
            binding.currentCount2.visibility = View.GONE
            binding.viewAllListen.visibility = View.GONE
            binding.fabTestWord.visibility = View.GONE
        } else {
            isDelete = false
            checkboxCount = 1
            binding.viewAddOrEditBtn.visibility = View.VISIBLE
            binding.viewBackBtn.visibility = View.VISIBLE
            binding.viewExportBtn.visibility = View.VISIBLE
            binding.viewDeleteBtn.visibility = View.GONE
            binding.viewCancelBtn.visibility = View.GONE
            binding.ckboxCountTv.visibility = View.GONE
            binding.toolbarTitle.text = wordBookNameForView

//            binding.fabTestWord.visibility = View.VISIBLE
            binding.hideSpinner.visibility = View.VISIBLE
            binding.sortSpinner.visibility = View.VISIBLE
            binding.currentCount.visibility = View.VISIBLE
            binding.currentCount2.visibility = View.VISIBLE
            binding.viewAllListen.visibility = View.VISIBLE
            binding.fabTestWord.visibility = View.VISIBLE
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
                TEST_WORD_CODE -> {
                    Log.d(TAG, "onActivityResult: 들어옴~~~안녕~~")
//                    wordBookIdForView 이거를 받아서 밑에 같이 넣어줘야하는거같은데
                    Log.d(TAG, "onActivityResult: wordBookIdForView $wordBookIdForView")
//                    val wordBookIdForView = data!!.getLongExtra("wordBookIdForView", 0)
//                    val wordList = data.getParcelableArrayListExtra<Word>("wordList")

                    getSortWhen(sortId)
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
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun onExportPopupClicked(view: View) {
        val popup: PopupMenu = PopupMenu(this, view)
        menuInflater.inflate(R.menu.view_sub_menu, popup.menu)
        popup.setOnMenuItemClickListener(
            PopupMenu.OnMenuItemClickListener {
                Log.d(TAG, "onExportPopupClicked: ${it.itemId}")
                when (it.itemId) {
                    R.id.menu_export -> {
                        if (wordList!!.size != 0) {
                            exportDialog()
                        } else {
                            Toast.makeText(this, "작성된 단어가 없습니다. 단어를 추가해주세요", Toast.LENGTH_SHORT).show()
                        }
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

    // FIXME: 2021-03-02 스낵바 올라갈 때 플로팅버튼이 올라가는데 한 번씩 버벅임

//        val actionSnackbar = Snackbar.make(binding.root, "단어 삭제 완료", Snackbar.LENGTH_LONG)
        val actionSnackbar = Snackbar.make(binding.coordinatorLayout, "단어 삭제 완료", Snackbar.LENGTH_LONG)
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
        hide_spinner.setSelection(0)
        wordList = wordModel?.getRecentOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordFavoriteOrder() {
        sortId = 1
        hide_spinner.setSelection(0)
        wordList = wordModel?.getWordFavoriteOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordAscendingOrder() {
        sortId = 2
        hide_spinner.setSelection(0)
        wordList = wordModel?.getWordAscendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordDescendingOrder() {
        sortId = 3
        hide_spinner.setSelection(0)
        wordList = wordModel?.getWordDescendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getMeanAscendingOrder() {
        sortId = 4
        hide_spinner.setSelection(0)
        wordList = wordModel?.getMeanAscendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getMeanDescendingOrder() {
        sortId = 5
        hide_spinner.setSelection(0)
        wordList = wordModel?.getMeanDescendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getRandomOrder() {
        sortId = 6
        hide_spinner.setSelection(0)
        wordList = wordModel?.getRandomOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }


}