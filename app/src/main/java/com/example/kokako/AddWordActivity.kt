package com.example.kokako

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.databinding.ActivityAddWordBinding
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.activity_add_word.*
import java.lang.NullPointerException

//  Log.d("     TAG", "===== AddWordActivity")
// TODO: 2021-01-23 Upsert
// FIXME: 2021-01-23 단어장 편집하기로 불러온 상태에서 맨위값 삭제하면 스크롤이 맨위로 안올라감
// FIXME: 2021-01-23 wordCount 고치기
class AddWordActivity : AppCompatActivity(), AddRecyclerViewInterface {
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private var _binding : ActivityAddWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var addRecyclerAdapter: AddRecyclerAdapter
    private var model : WordViewModel? = null // 바로 WordDatabase안부르고 뷰모델 통해서 부름
    var currentWordCount: Int = 0
    var currentCount: Int = 0
    var countString: String? = null
    private var tvWordCount: TextView? = null
    private lateinit var imm : InputMethodManager   // 여기 선언한다고 lateinit 함
    private var word = ArrayList<Word>()
    var word2 = ArrayList<Word>()
    var wordBookIdForAddOrEdit : Long = 0
    companion object Constant {
        const val INPUT_WORD_ID : Int = 101101101110.toInt()
        const val INPUT_MEAN_ID : Int = 10000000001000.toInt()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        wordBookIdForAddOrEdit = intent.getLongExtra("wordBookIdForAddOrEdit",0)
        val checkActivity = intent.getBooleanExtra("checkActivity",false)
//        val wordBookIdForView = intent.getLongExtra("wordBookIdForView",0)
          Log.d("     TAG", "===== AddWordActivity getLongExtra() wordBookIdForAdd 값은 : $wordBookIdForAddOrEdit ")
//          Log.d("     TAG", "===== AddWordActivity getLongExtra() wordBookIdForView 값은 : $wordBookIdForView ")
        toolbarBinding = binding.includeToolbar
        tvWordCount = binding.wordCount
        input_word.id = INPUT_WORD_ID
        input_mean.id = INPUT_MEAN_ID

        Log.d("     TAG", "===== AddWordActivity input_word.id 은 ${input_word.id} INPUT_WORD_ID 은 $INPUT_WORD_ID \n input_mean.id 은 ${input_mean.id} INPUT_MEAN_ID 은 ${INPUT_MEAN_ID} ")

        //        add Divider in RecyclerView
//        rv_list_item.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rv_list_item.isFocusable = false



        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.text = if(checkActivity) {"단어장 편집"} else {"단어장 추가"}

        // Forcing the Soft Keyboard open
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)



//        데이터핸들링은 뷰모델을 통해서만 한다.
        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForAddOrEdit) as T
            }}).get(WordViewModel::class.java)
        addRecyclerAdapter = AddRecyclerAdapter(this)
        if (checkActivity){
            word  = model?.getWordFromWordBook222(wordBookIdForAddOrEdit)!!
            currentCount = word.size
//            recyclerViewApply(addRecyclerAdapter)
            rv_list_item.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                //                            (layoutManager as LinearLayoutManager).stackFromEnd = true
                setHasFixedSize(true)
                if(word.size != 0) smoothScrollToPosition(word.size - 1)    // 데이터가 없으면 에러
                adapter = addRecyclerAdapter
                addRecyclerAdapter.submitDataList(word)
            }
        }

        // FIXME: 2021-01-23 count

//        countString = "${findViewById<EditText>(currentFocusId)}/$currentWordCount"
//        Toast.makeText(this.context, countString, Toast.LENGTH_SHORT).show()
//        tvWordCount?.text = countString

        val btnListener = View.OnClickListener { view ->
            val currentFocusId = currentFocus!!.id
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
                    if (findViewById<EditText>(currentFocusId + 1) != null) {
                        findViewById<EditText>(currentFocusId + 1).requestFocus()
                    } else if (currentFocus!!.id == INPUT_MEAN_ID){
                        input_word.requestFocus()
                    } else if (currentFocus!!.id == INPUT_WORD_ID){
                        "do nothing"
                    }
                    else {
                        input_mean.requestFocus()
                    }
                }
                // TODO 2021-01-22 LongTouch하면 쭉쭉쭉쭉가게? 오바
                R.id.btn_move_right -> {
                    Log.d("     TAG",
                        "===== AddWordActivity btn_move_right input_word.id 은 ${input_word.id} INPUT_WORD_ID 은 ${INPUT_WORD_ID} \n input_mean.id 은 ${input_mean.id} INPUT_MEAN_ID 은 ${INPUT_MEAN_ID} ")
                    Log.d("     TAG",
                        "===== AddWordActivity btn_move_right currentFocus!!.id ${currentFocus!!.id} 와 INPUT_MEAN_ID ${INPUT_MEAN_ID} ")
                    if (currentFocus!!.id == INPUT_WORD_ID) {
                        input_mean.requestFocus()
                        Log.d("     TAG", "===== AddWordActivity btn_move_right if문 ")
                    } else if (currentFocus!!.id == INPUT_MEAN_ID && addRecyclerAdapter.itemCount != 0) {
                        try {
                            findViewById<EditText>(((addRecyclerAdapter.itemCount - 1) * 2) + 1).requestFocus()
                        } catch(e : NullPointerException) {
                            return@OnClickListener
                        }
//                        Log.d("     TAG", "===== AddWordActivity ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ if문 ${((addRecyclerAdapter.itemCount - 1) * 2) + 1} 는 ${(addRecyclerAdapter.itemCount * 2 - 1)}")
//                        if (((addRecyclerAdapter.itemCount - 1) * 2) + 1 != (addRecyclerAdapter.itemCount * 2 - 1))
                    } else if (findViewById<EditText>(currentFocusId - 1) != null) {
                        Log.d("     TAG", "===== AddWordActivity btn_move_right else if문 2")
                        findViewById<EditText>(currentFocusId - 1).requestFocus()
                    }
                }
// data없으면  무시하기 -> textViewLayout?
                R.id.btn_add_word -> {
                    input_word.text.toString().trim()
                    input_mean.text.toString().trim()
//                    if (input_word.text.equals("") || input_mean.text.equals("")) {
                    if (input_mean.text.toString().trim().isEmpty() || input_word.text.toString()
                            .trim()
                            .isEmpty()
                    ) {
                        Toast.makeText(this, "데이터를 올바르게 입력", Toast.LENGTH_SHORT).show()
                    } else {
                        // FIXME: 2021-01-23 입력한 값들에서 빈값 예외처리 / 추후
                        word.add(Word(0,
                            input_word.text.toString(),
                            input_mean.text.toString(),
                            false,
                            wordBookIdForAddOrEdit))
                        Log.d("     TAG",
                            "===== AddWordActivity 입력한 단어, 뜻 : ${input_word.text}, ${input_mean.text}")
                        Log.d("     TAG", "===== AddWordActivity word 값은 : $word")
                        addRecyclerAdapter = AddRecyclerAdapter(this)
                        rv_list_item.apply {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                            //                            (layoutManager as LinearLayoutManager).stackFromEnd = true
                            setHasFixedSize(true)
                            smoothScrollToPosition(word.size - 1)
                            adapter = addRecyclerAdapter
                            addRecyclerAdapter.submitDataList(word)
                        }
                        input_word.text.clear()
                        input_mean.text.clear()
                        input_word.requestFocus()
                        // TODO: 2021-01-23 wordCount
//                        wordCount++
//                        countString = "$currentCount/$wordCount"
//                        tvWordCount?.text = countString
//                        Toast.makeText(this, "wordCount는 $wordCount", Toast.LENGTH_SHORT).show()
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

    private fun addWord(wordBookId: Long){
//        val word = Word(0,
//            binding.inputWord.text.toString(),
//            binding.inputMean.text.toString(),
//            wordBookId)
//        model?.insert(word)
//        model?.insert(Word(0, binding.inputWord.text.toString(),binding.inputMean.text.toString(), wordBookId))
    }
    private fun deleteWord(position: Int) {
        model?.delete(addRecyclerAdapter.getItem()[position])
    }
    private fun updateWord(){

    }
    private fun deleteAllWord(wordBookIdForView : Long){
        model?.deleteWordById(wordBookIdForView)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateWordList(words: List<Word>?) {
        addRecyclerAdapter = AddRecyclerAdapter(this)
        addRecyclerAdapter.submitDataList(words as ArrayList<Word>)
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(200f)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(rv_list_item)
        // 리사이클러뷰 설정
        rv_list_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
//                            (layoutManager as LinearLayoutManager).stackFromEnd = true
            setHasFixedSize(true)
            // 어답터 장착
            adapter = addRecyclerAdapter
//            addItemDecoration(ItemDecoration())

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    /*
* 이 액티비티에서 뷰홀더에서 onClick()된걸 아니까 이 액티비티에서 클릭처리를 할 수 있다
* */

    //    fun removeWord(wordDto: ArrayList<WordDTO>, position: Int){
//        wordDto.removeAt(position)
//        notifyItemRemoved(position){ // 뜻 ▲
//
//                }
//        notifyItemRangeChanged(position, wordDto.size)
//    }
    override fun onRemoveClicked(view: View, position: Int) {
        Log.d("     TAG","===== AddWordActivity onRemoveClicked position $position")
        val mBuilder = AlertDialog.Builder(view.context)
        mBuilder.setTitle("삭제")
            .setMessage("단어 : " + addRecyclerAdapter.getItem()[position].word.toString() + "\n뜻 : " + addRecyclerAdapter.getItem()[position].mean.toString() + "\n이 단어 항목을 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { _, _ ->
                    word.removeAt(position)
                    addRecyclerAdapter.notifyItemRemoved(position)
                    addRecyclerAdapter.notifyItemRangeChanged(position, addRecyclerAdapter.itemCount)
//                    addRecyclerAdapter.notifyDataSetChanged() // 이거 추가하니까 아예 갱신도 안되노
                    currentWordCount--
                    countString = "$currentCount/$currentWordCount"
                    tvWordCount?.text = countString
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
        val mBuilder = AlertDialog.Builder(this)
        when(item.itemId) {
            android.R.id.home -> { cancelDialog(mBuilder) }
            R.id.menu_check -> {
                // FIXME: 2021-01-20 아무값 입력없이 추가 예외처리
                mBuilder.setTitle("단어장으로 추가")
                    .setMessage("입력한 단어를 단어장으로 만드시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        // FIXME: 2021-01-23 Toast? snackBar?
                        Toast.makeText(this, "단어장 추가완료", Toast.LENGTH_SHORT).show()
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        Log.d("     TAG",
                            "===== AddWordActivity onOptionsItemSelected 단어장으로 추가 확인1")
                        model?.deleteWordById(wordBookIdForAddOrEdit)
                        model?.insertAllDatas(word)
                        Log.d("     TAG", "===== AddWordActivity onOptionsItemSelected word $word")
                        Log.d("     TAG",
                            "===== AddWordActivity onOptionsItemSelected 단어장으로 추가 확인2")
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        dialog.dismiss()
                        finish()
                    })
                mBuilder.create().show()
            }
        }
        return true
    }
    override fun onBackPressed() {
        cancelDialog(mBuilder = AlertDialog.Builder(this))
    }

    private fun cancelDialog(mBuilder: AlertDialog.Builder) {
        mBuilder.setTitle("단어장 취소")
            .setMessage("입력한 단어를 취소하고 이동하시겠습니까?")
            .setNegativeButton("취소", null)
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                dialog.dismiss()
                finish()
            })
        mBuilder.create().show()
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
    }

    /*    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

}
