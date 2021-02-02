package com.example.kokako

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.databinding.ActivityViewWordBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordBookViewModel
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.activity_view_word.*
import kotlinx.android.synthetic.main.activity_view_word.view.*
import androidx.appcompat.widget.Toolbar
import com.example.kokako.model.CheckBoxData
import kotlinx.android.synthetic.main.rv_view_list_item.view.*

//  Log.d("     TAG", "===== AddWordActivity")
class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface {
//    private lateinit var            toolbarBinding: ActivityToolbarBinding
    private var                     toolbar: Toolbar? = null
    private var                     _binding: ActivityViewWordBinding? = null
    private val                     binding get() = _binding!!
    private lateinit var            viewRecyclerAdapter: ViewWordRecyclerAdapter
    private var                     wordModel: WordViewModel? = null
    private var                     wordBookModel: WordBookViewModel? = null
    var                             wordBookIdForView: Long = 0
    var                             wordBookNameForView: String? = null
    private lateinit var            imm : InputMethodManager
    private var                     checkboxList = ArrayList<CheckBoxData>()
/*    companion object {
        var checkboxList = arrayListOf<CheckBoxData>()
    }*/


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
        toolbar!!.toolbar_title.text = wordBookNameForView
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        viewRecyclerAdapter = ViewWordRecyclerAdapter(this)

        longClick(0)


        wordBookModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(
            WordBookViewModel::class.java)
        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForView) as T
            }
        }).get(WordViewModel::class.java)
        wordModel?.wordList?.observe(this, { updateWordList(it) })
        rv_list_word_view.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = viewRecyclerAdapter
        }


        val btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.view_back_btn -> {
                    finish()
                }
                R.id.view_cancel_btn -> {
//                    체크버튼 clear해야함
//                    var clear  = checkboxList
//                    viewRecyclerAdapter.setCheckboxList(clear)
                    longClick(0)
                }
                R.id.view_add_or_edit_btn -> {
                    val intent = Intent(this, AddWordActivity::class.java)
                    intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForView)
                    intent.putExtra("checkActivity", true)
                    startActivityForResult(intent, 100)
                }
                R.id.view_delete_btn -> {
                    deleteWordDialog(checkboxList)
                }
            }
        }

        view_back_btn.setOnClickListener(btnListener)
        view_cancel_btn.setOnClickListener(btnListener)
        view_add_or_edit_btn.setOnClickListener(btnListener)
        view_delete_btn.setOnClickListener(btnListener)






        val sortArrayAdapter = object : ArrayAdapter<String>(this, R.layout.sort_spinner) {
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).text = getItem(count)
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }
        sortArrayAdapter.addAll(sortItems.toMutableList())
        sortArrayAdapter.add("정렬")
        // TODO 정렬 :생성일순 처럼 처음 default는 정렬 만나오게하고 밑에 클릭하면 정렬 : ~~하게 나오면 좋겠다

        sort_spinner.adapter = sortArrayAdapter
        sort_spinner.setSelection(sortArrayAdapter.count)
        sort_spinner.dropDownVerticalOffset = dipToPixels(35f).toInt()
        sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (position) {
                    0 -> { // 최신순

                    }
                    1 -> { // 별표순

                    }
                    2 -> { // 단어 ▲
                        wordModel?.getWordAscendingOrder(wordBookIdForView)
                    }
                    3 -> { // 단어 ▼

                    }
                    4 -> {

                    }
                    5 -> { // 뜻 ▼

                    }
                    6 -> { // 랜덤

                    }
                }
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
        hide_spinner.dropDownVerticalOffset = dipToPixels(35f).toInt()
        hide_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (position) {
                    0 -> { // 전체보기

                    }
                    1 -> { // 단어 가리기

                    }
                    2 -> { // 뜻 가리기

                    }
                    3 -> { // 랜덤

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    // TODO: 2021-01-23  putExtra(wordBookIdForView) -> 순서 정하는 Activity
//        fab_add_note.setOnClickListener { view -> }
    }

    override fun onCheckboxClicked(v: View, adapterPosition: Int, checkboxList: ArrayList<CheckBoxData>) {
        Log.d("TAGGG", "ViewWordActivity  들어온 checkboxList adapterPosition : / ${checkboxList[adapterPosition].checked}")
        checkboxList[adapterPosition].checked = v.view_my_check.isChecked   // v. 을 해줘야함 onClick에서 보내주는걸 받고 고쳐야지
        viewRecyclerAdapter.setCheckboxList(checkboxList)

        this.checkboxList = checkboxList
        Log.d("TAGGG", "ViewWordActivity 나간 checkboxList adapterPosition :  / ${checkboxList[adapterPosition].checked}")
    }


    private fun deleteWordDialog(checkboxList: ArrayList<CheckBoxData>) {
        Log.d("TAG", "deleteWordDialog 사이즈 : ${checkboxList.size}")
        val mBuilder = AlertDialog.Builder(this)
//        mBuilder.setTitle("삭제")
            .setMessage("체크된 단어들을 모두 삭제합니다.\n정말 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { _, _ ->
                    var i = 0
                    var j = 0
                    while (checkboxList.size != 0 && i < checkboxList.size) {
                        if (checkboxList[i].checked) {
                            /* todo deleteAll(ArrayList<Word>) 만들기
                            val temp = ArrayList<Word>()
                            temp.add(viewRecyclerAdapter.getItem()[i])
                            */
                            wordModel?.delete(viewRecyclerAdapter.getItem()[i])
                        }
                        i++
                        j = 1
                    }
                    if (j == 1)
                        Toast.makeText(this, "단어 삭제 완료.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "삭제할 단어를 체크해주세요.", Toast.LENGTH_SHORT).show()

                    longClick(0)
                }
            )
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
        mBuilder.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateWordList(word: List<Word>?) {
        viewRecyclerAdapter.submitList(word as ArrayList<Word>)
        binding.currentCount.text = "단어 개수 : " + word.size.toString()
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.add_or_edit_menu, menu)
//        return true
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("     TAG", "===== ViewWordActivity - onActivityResult called")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    val wordBookIdForAddOrEdit = data!!.getLongExtra("wordBookIdForAddOrEdit", 0)
                    val word = data.getParcelableArrayListExtra<Word>("word")
                    wordModel?.deleteWordById(wordBookIdForAddOrEdit)
                    wordModel?.insertAllDatas(word)
                    wordBookModel?.updateWordBookCount(wordBookIdForAddOrEdit)
                }
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

//        val mBuilder = AlertDialog.Builder(this)
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.view_add_or_edit_btn -> {
//                val intent = Intent(this, AddWordActivity::class.java)
//                intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForView)
//                intent.putExtra("checkActivity", true)
//                startActivityForResult(intent, 100)
            }
            /*R.id.menu_delete -> {
                mBuilder.setTitle("단어장 삭제")
                    .setMessage("단어장을 삭제하시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        // FIXME: 2021-01-22 삭제하고 MainActivity로 돌아오면 LiveData반영 안되어있음 / 이거랑 위에꺼 해결
                        wordBookModel?.deleteWordBookById(wordBookIdForView)
                        dialog.dismiss()
                        finish()
                    })
                mBuilder.create().show()
            }*/
        }
        return true
    }

    private fun updateStar(word: Word) {
        wordModel?.updateStarChecked(word)
    }

    override fun onStarClicked(v: View, adapterPosition: Int) {
        val word: Word = viewRecyclerAdapter.getItem()[adapterPosition]
        if (word.bookMarkCheck == 0) {
            word.bookMarkCheck = 1
        } else {
            word.bookMarkCheck = 0
        }
//        word.bookMarkCheck = !word.bookMarkCheck
        updateStar(word)
    }

    // FIXME: 2021-01-27 단어 삭제하고 메인으로 가면 카운트가 안없어짐
    override fun onPopupMenuWordClicked(v: View, myWordBtnViewOption: Button, adapterPosition: Int) {
        Log.d("     TAG", "===== MainActivity - onPopupMenuClicked called")
        val popup: PopupMenu = PopupMenu(this, myWordBtnViewOption)
        val currentValue = viewRecyclerAdapter.getItem()[adapterPosition]
        popup.inflate(R.menu.view_word_menu)
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
                                    Toast.makeText(this, "단어, 뜻을 정확히 입력해주세요", Toast.LENGTH_SHORT)
                                        .show()
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
                        Log.d("     TAG", "===== MainActivity - onRemoveClicked() IN $currentValue")
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

    override fun onWordLongClicked(v: View, adapterPosition: Int) {
        longClick(1)
    }

    private fun longClick(num: Int) {
        viewRecyclerAdapter.updateCheckbox(num)
        viewRecyclerAdapter.notifyDataSetChanged()

        if (num == 1) {
            binding.viewAddOrEditBtn.visibility = View.GONE
            binding.viewBackBtn.visibility = View.GONE
            binding.viewDeleteBtn.visibility = View.VISIBLE
            binding.viewCancelBtn.visibility = View.VISIBLE

            binding.fabTestWord.visibility = View.INVISIBLE
            binding.hideSpinner.visibility = View.INVISIBLE // 여기에 선택된 갯수 표시?
            binding.sortSpinner.visibility = View.INVISIBLE
            binding.currentCount.visibility = View.INVISIBLE

//            binding.viewAllCheckRbt.visibility = View.VISIBLE
        } else {
            binding.viewAddOrEditBtn.visibility = View.VISIBLE
            binding.viewBackBtn.visibility = View.VISIBLE
            binding.viewDeleteBtn.visibility = View.GONE
            binding.viewCancelBtn.visibility = View.GONE

            binding.fabTestWord.visibility = View.VISIBLE
            binding.hideSpinner.visibility = View.VISIBLE // 여기에 선택된 갯수 표시?
            binding.sortSpinner.visibility = View.VISIBLE
            binding.currentCount.visibility = View.VISIBLE

//            binding.viewAllCheckRbt.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        longClick(0)
    }
}