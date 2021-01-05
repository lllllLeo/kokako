package com.example.kokako

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.viewModel.WordViewModel
import com.example.kokako.databinding.ActivityAddWordBinding
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.model.Word
import kotlinx.android.synthetic.main.activity_add_word.*

class AddWordActivity : AppCompatActivity(), AddRecyclerViewInterface {
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private var _binding : ActivityAddWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var addRecyclerAdapter: AddRecyclerAdapter
    private var model : WordViewModel? = null // 바로 WordDatabase안부르고 뷰모델 통해서 부름
    //    lateinit var db : WordDatabase  뷰모델할려고 일단지움
//    var wordList = listOf<WordDTO>()
    var wordList = ArrayList<Word>()  // 밑에 var word 새로 만들거라서 잠시 지움 맨밑에도 오류 뜸 이거떄문에
    var wordCount: Int = 0
    var currentCount: Int = 0
    var countString: String? = null
    private var tvWordCount: TextView? = null
    private lateinit var imm : InputMethodManager   // 여기 선언한다고 lateinit 함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbarBinding = binding.includeToolbar
        tvWordCount = binding.wordCount

        //        add Divider in RecyclerView
        rv_list_item.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))

        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.setText("단어장 추가")


        // Forcing the Soft Keyboard open
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)



//        db = WordDatabase.getInstance(this)!!

//        데이터핸들링은 뷰모델을 통해서만 한다.
        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(WordViewModel::class.java)
        model?.wordList?.observe(this, {    // 단어 추가 버튼 누르면 이게 옵저버로 보고잇으니까 여기 안에는 리사이클러뷰에 넣는거해야겟지
            updateWordList(it)
        })

        countString = "$currentCount/$wordCount"
//        Toast.makeText(this.context, countString, Toast.LENGTH_SHORT).show()
        tvWordCount?.text = countString

        var btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.delete_all -> {
                    input_word.text.clear()
                    input_mean.text.clear()
                    input_word.requestFocus()
                }
                R.id.btn_remove_text -> {
                    if (input_word.isFocused) {
                        input_word.text.clear()
                    } else if (input_mean.isFocused)
                        input_mean.text.clear()
                }
                R.id.btn_move_left -> {
                    input_word.requestFocus()
                }
                R.id.btn_move_right -> {
                    input_mean.requestFocus()
                }
// data없으면  무시하기 -> textViewLayout?
                R.id.btn_add_word -> {
                    input_word.text.toString().trim()
                    input_mean.text.toString().trim()
//                    if (input_word.text.equals("") || input_mean.text.equals("")) {
                    if (input_mean.text.toString().isEmpty() || input_word.text.toString()
                            .isEmpty()
                    ) {
                        Toast.makeText(this, "데이터를 올바르게 입력", Toast.LENGTH_SHORT).show()
//                        버튼 활성화 안되게
//                        btn_add_word.isClickable = false
//                        btn_add_word.isEnabled = false
                    } else {
                        addWord(view)
                        input_word.text.clear()
                        input_mean.text.clear()
                        input_word.requestFocus()
//                        Toast.makeText(this,"현재 단어 갯수"+wordDto.size,Toast.LENGTH_SHORT).show()
                        wordCount++
                        countString = "$currentCount/$wordCount"
                        tvWordCount?.text = countString
                        Toast.makeText(this, "wordCount는 " + wordCount, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        delete_all.setOnClickListener(btnListener)
        btn_remove_text.setOnClickListener(btnListener)
        btn_move_left.setOnClickListener(btnListener)
        btn_move_right.setOnClickListener(btnListener)
        btn_add_word.setOnClickListener(btnListener)
    }
    fun addWord(view: View){
        var word = Word()
        word.word = binding.inputWord.text.toString()
        word.mean = binding.inputMean.text.toString()
        model?.insert(word)
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun updateWordList(words: List<Word>?) {
        addRecyclerAdapter = AddRecyclerAdapter(this)
        addRecyclerAdapter.submitList(words as ArrayList<Word>)
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(200f)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(rv_list_item)
        // 리사이클러뷰 설정
        rv_list_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
//                            (layoutManager as LinearLayoutManager).stackFromEnd = true
            // 어답터 장착
            adapter = addRecyclerAdapter
//            addItemDecoration(ItemDecoration())

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }

        }
    }

    fun deleteWord(position: Int) {
//        model?.delete(addRecyclerAdapter.getItem()[position])
//        들어있는 데이터(Word) 를빼와야함 위에처럼 선언한거 하면 INdexOutOFBoundException나옴
        model?.delete(wordList[position])
    }

    /*
* 이 액티비티에서 뷰홀더에서 onClick()된걸 아니까 이 액티비티에서 클릭처리를 할 수 있다
* */

//    delete메서드 만들기


    override fun onRemoveClicked(view: View, position: Int) {
        Log.d(" TAG ", "AddWordActivity -- onItemClicked() called")
        val mBuilder = AlertDialog.Builder(view.context)
        mBuilder.setTitle("삭제")
//            .setMessage("단어 : " + wordList[position].word.toString() + "\n뜻 : " + wordList[position].mean.toString() + "\n이 항목을 삭제하시겠습니까?")
            .setMessage("삭제 ㄱㄱ?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->

                    deleteWord(position)

//                    addRecyclerAdapter.removeWord(word, position)
//                    Toast.makeText(this,"삭제된 후 단어 개수"+wordDto.size,Toast.LENGTH_SHORT).show()
                    wordCount--
                    countString = "$currentCount/$wordCount"
                    tvWordCount?.text = countString

//                    Toast.makeText(this,"wordCount는 "+wordCount,Toast.LENGTH_SHORT).show()
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
        mBuilder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                finish()
            }
            R.id.menu_check -> {
                var mBuilder = AlertDialog.Builder(this)
                mBuilder.setTitle("단어장으로 추가")
                    .setMessage("입력한 텍스트를 단어장으로 만드시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener {
                        dialog, which ->
//                        DB작업
                        Toast.makeText(this,"단어장으로 추가완료", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        finish()
                    })
                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }
        return true
    }











/*    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/
}

    // 뒤로가기 다이얼로그


