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
class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface {
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private var _binding: ActivityViewWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewRecyclerAdapter: ViewWordRecyclerAdapter
    private var model: WordViewModel? = null
    private var wordBookModel: WordBookViewModel? = null
    var wordBookIdForView: Long = 0

    var wordBookNameForView: String? = null


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

        Log.d("     TAG",
            "===== ViewWordActivity getExtra wordBookIdForView 값은 1111 : $wordBookIdForView")

        model = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForView) as T
            }
        }).get(WordViewModel::class.java)

        model?.wordList?.observe(this, {
            Log.d("     TAG", "===== ViewWordActivity observe IN")
            updateWordList(it)
            Log.d("     TAG", "===== ViewWordActivity observe OUT")
        })
        wordBookModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(
            WordBookViewModel::class.java)


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
                        model?.getWordAscendingOrder(wordBookIdForView)
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
        viewRecyclerAdapter = ViewWordRecyclerAdapter(this)
        viewRecyclerAdapter.submitList(word as ArrayList<Word>)
        rv_list_word_view.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            setHasFixedSize(true)
            if (word.isNotEmpty()) {
                scrollToPosition(word.size - 1)
            }
            adapter = viewRecyclerAdapter
        }
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
                    Log.d("     TAG", "===== ViewWordActivity - onActivityResult when called 2222 $wordBookIdForView")
                    val wordList = model?.getWordFromWordBook222(wordBookIdForView)
                    updateWordList(wordList)
                    Log.d("     TAG",
                        "===== ViewWordActivity - onActivityResult when wordBookIdForView : $wordBookIdForView")
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val mBuilder = AlertDialog.Builder(this)
        when (item.itemId) {
            android.R.id.home -> {
                // FIXME: 2021-01-24 count는 startActivityForResult이렇게하니까 안되네 in ViewWordActivity
/*                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)*/
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
        model?.updateStarChecked(word)
    }

    override fun onStarClicked(v: View, adapterPosition: Int) {
        val word: Word = viewRecyclerAdapter.getItems()[adapterPosition]
        word.bookMarkCheck = !word.bookMarkCheck
        updateStar(word)
    }
}