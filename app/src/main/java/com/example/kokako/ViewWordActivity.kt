package com.example.kokako

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.databinding.ActivityViewWordBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordBookViewModel
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.activity_view_word.*

//  Log.d("     TAG", "===== AddWordActivity")
// TODO: 2021-01-22 정렬, 가리기 구현
// TODO: 2021-01-22 편집 하나씩 Dialog로 구현
// TODO: 2021-01-27 뷰페이지 에서 편집 -> AddActivity에서 편집 -> 뷰 -> 메인 가면 업뎃안돼잇음 ㅅㅂ 다 존나 처리해야하나 ㅈ같네
// TODO: 2021-01-29 데이터 많은 상태에서 여기로 들어오면 스크롤이 맨 위가 아님
class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface {
    private lateinit var            toolbarBinding: ActivityToolbarBinding
    private var                     _binding: ActivityViewWordBinding? = null
    private val                     binding get() = _binding!!
    private lateinit var            viewRecyclerAdapter: ViewWordRecyclerAdapter
    private var                     wordModel: WordViewModel? = null
    private var                     wordBookModel: WordBookViewModel? = null
    var                             wordBookIdForView: Long = 0
    var                             wordBookNameForView: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityViewWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        wordBookIdForView = intent.getLongExtra("wordBookIdForView", 0)
        wordBookNameForView = intent.getStringExtra("wordBookNameForView")
        val sortItems = resources.getStringArray(R.array.sort_array)
        val hideItems = resources.getStringArray(R.array.hide_array) // 발음 가리기 (3 items)
        toolbarBinding = binding.includeToolbar
        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.text = wordBookNameForView

        viewRecyclerAdapter = ViewWordRecyclerAdapter(this)



        Log.d("     TAG",
            "===== ViewWordActivity getExtra wordBookIdForView 값은 1111 : $wordBookIdForView")
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
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // reverseLayout이엇다 ㅅㅂ..
            setHasFixedSize(true)
            adapter = viewRecyclerAdapter
        }

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
        /* TODO 정렬 :생성일순 처럼 처음 default는 정렬 만나오게하고 밑에 클릭하면 정렬 : ~~하게 나오면 좋겠다
        * */
        sort_spinner.adapter = sortArrayAdapter
        sort_spinner.setSelection(sortArrayAdapter.count)
        sort_spinner.dropDownVerticalOffset = dipToPixels(35f).toInt()
        sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
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
                id: Long
            ) {
                when (position) {
                    0 -> { // 단어 가리기
                    }
                    1 -> { // 뜻 가리기

                    }
                    2 -> { // 랜덤

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
// TODO: 2021-01-23  putExtra(wordBookIdForView) -> 순서 정하는 Activity
//        fab_add_note.setOnClickListener { view -> }
    }

    private fun updateWordList(word: List<Word>?) {
        Log.d("     TAG", "===== ViewWordActivity updateWordList IN")
        Log.d("     TAG", "===== ViewWordActivity updateWordList $word")
        viewRecyclerAdapter.submitList(word as ArrayList<Word>)
        Log.d("     TAG", "===== ViewWordActivity updateWordList OUT")
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_word_menu, menu)
        return true
    }

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

        val mBuilder = AlertDialog.Builder(this)
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_edit -> {
                val intent = Intent(this, AddWordActivity::class.java)
                intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForView)
                intent.putExtra("checkActivity", true)
                startActivityForResult(intent, 100)
            }
            R.id.menu_delete -> {
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
            }
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
        popup.inflate(R.menu.view_word_menu)
        popup.setOnMenuItemClickListener(
            PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
//                        val container = LinearLayout(this)
//                        container.orientation = LinearLayout.VERTICAL
//                        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT)
//                        lp.setMargins(50, 0, 50, 0) // editText Margin
//
//                        val dialogEditText = EditText(this)
//                        dialogEditText.maxLines = 1
//                        dialogEditText.setLines(1)
//                        container.addView(dialogEditText, lp)
//                        val mBuilder = AlertDialog.Builder(this)
////                .setTitle("단어장 이름 변경")
//                            .setMessage("변경할 단어장 이름을 입력해주세요.")
//                            .setView(container)
//                            .setNegativeButton("취소", null)
//                            .setPositiveButton("확인", null)
//                            .create()
//                        mBuilder.setOnShowListener {
//                            val b: Button = mBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
//                            b.setOnClickListener(View.OnClickListener {
//                                if (dialogEditText.text!!.trim().isEmpty()) {
//                                    Toast.makeText(this, "단어장 이름을 정확히 입력해주세요", Toast.LENGTH_SHORT)
//                                        .show()
////                        TODO 특문제외
//                                } else {
//                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//                                    myWordRecyclerAdapter.getItem()[adapterPosition].title =
//                                        dialogEditText.text.toString()
//                                    Log.d("     TAG", "===== MainActivity - 편집하기 - else문" + myWordRecyclerAdapter.getItem()[adapterPosition].toString())
//                                    updateWordBookTitle(myWordRecyclerAdapter.getItem()[adapterPosition])
//                                    mBuilder.dismiss()
//                                }
//                            })
//                        }
//                        mBuilder.show()
                    }
                    R.id.menu_delete -> {
                        Log.d("     TAG",
                            "===== MainActivity - onRemoveClicked() IN " + viewRecyclerAdapter.getItem()[adapterPosition].toString())
                        val mBuilder = AlertDialog.Builder(this)
                        mBuilder.setTitle("삭제")
                            .setMessage("단어 : " + viewRecyclerAdapter.getItem()[adapterPosition].word.toString() + "\n뜻 : " + viewRecyclerAdapter.getItem()[adapterPosition].mean.toString() + "\n이 단어 항목을 삭제하시겠습니까?")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { _, _ ->
                                    wordModel?.delete(viewRecyclerAdapter.getItem()[adapterPosition])
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
}