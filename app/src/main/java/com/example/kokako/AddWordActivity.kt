package com.example.kokako

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.databinding.ActivityAddWordBinding
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.activity_add_word.*
import kotlinx.android.synthetic.main.activity_view_word.view.*
import kotlin.NullPointerException

//  Log.d("     TAG", "===== AddWordActivity")
// TODO: 2021-01-27 키보드를 아예 보이지말까
// TODO: 2021-02-02 추가하면 스크롤 위로안감
class AddWordActivity : AppCompatActivity(), AddRecyclerViewInterface {
    private lateinit var            toolbarBinding: ActivityToolbarBinding
    private var                     _binding : ActivityAddWordBinding? = null
    private val                     binding get() = _binding!!
    private lateinit var            addRecyclerAdapter: AddRecyclerAdapter
    private var                     model : WordViewModel? = null
    var                             currentWordCount: Int = 0
    private var                     currentCount: Int = 0
    var                             countString: String? = null
    private var                     tvWordCount: TextView? = null
    private lateinit var            imm : InputMethodManager
    private var                     word = ArrayList<Word>()
    var                             wordBookIdForAddOrEdit : Long = 0
    var                             checkActivity = false
    companion object Constant {
        const val INPUT_WORD_ID : Int = 101101101110.toInt() // input_word.id 은 -1978113994 INPUT_WORD_ID 은 -1978113994
        const val INPUT_MEAN_ID : Int = 10000000001000.toInt() // input_mean.id 은 1316135912 INPUT_MEAN_ID 은 1316135912
        const val TAG = "TAG AddWordActivity"
        const val COMPLETE_CODE = 10
        const val CANCEL_CODE = 11
        const val EDIT_WORD_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        input_word.id =             INPUT_WORD_ID
        input_mean.id =             INPUT_MEAN_ID
        wordBookIdForAddOrEdit = intent.getLongExtra("wordBookIdForAddOrEdit",0)
        Log.d("     TAG", "===== AddWordActivity getLongExtra() wordBookIdForAdd 값은 : $wordBookIdForAddOrEdit ")
        checkActivity = intent.getBooleanExtra("checkActivity",false)

        Log.d("     TAG", "===== AddWordActivity input_word.id 은 ${input_word.id} INPUT_WORD_ID 은 $INPUT_WORD_ID \n input_mean.id 은 ${input_mean.id} INPUT_MEAN_ID 은 ${INPUT_MEAN_ID} ")

        //        add Divider in RecyclerView
//        rv_list_item.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rv_list_item.isFocusable = false

        binding.toolbarTitle.gravity = Gravity.LEFT
        binding.toolbarTitle.text = if(checkActivity) {"단어장 편집"} else {"단어장 추가"}

        // Forcing the Soft Keyboard open
/*      imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)*/

// TODO: 2021-01-27 19:21분 데이터로 고치기

//        데이터핸들링은 뷰모델을 통해서만 한다.
        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForAddOrEdit) as T
            }}).get(WordViewModel::class.java)

        addRecyclerAdapter = AddRecyclerAdapter(this)
        word            = model?.getRecentOrder(wordBookIdForAddOrEdit)!!
        addRecyclerAdapter.submitDataList(word)

        rv_list_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            // TODO: 2021-01-31 위에 reverseLayout이랑 밑에꺼 같이하더라 일단 추가
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            if (checkActivity){
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(word.size,0)
            }
            setHasFixedSize(true)
//            if(word.size != 0) smoothScrollToPosition(word.size - 1)    // 데이터가 없으면 에러
// TODO: 2021-01-31 내폰은 ㄱㅊ은데 엄ㄴ마폰은 삭제가 이상하게됨 -> ㄱㅊ아졋음 다시한번해보기
            val recyclerViewState : Parcelable = rv_list_item.layoutManager?.onSaveInstanceState()!!
            rv_list_item.layoutManager!!.onRestoreInstanceState(recyclerViewState)
            adapter = addRecyclerAdapter
            }
            getCurrentCount("init")

//        countString = "${findViewById<EditText>(currentFocusId)}/$currentWordCount"
//        Toast.makeText(this.context, countString, Toast.LENGTH_SHORT).show()
//        tvWordCount?.text = countString

        val btnListener = View.OnClickListener { view ->
            try {
                val currentFocusId = currentFocus!!.id
                when (view.id) {
                    R.id.btn_add_word_back_btn -> {
                        val mBuilder = AlertDialog.Builder(this)
                        cancelDialog(mBuilder)
                    }
                    R.id.btn_add_finish_check -> {
                        // FIXME: 2021-01-20 아무값 입력없이 추가 예외처리
                        val mBuilder = AlertDialog.Builder(this)
                        mBuilder.setTitle("단어장으로 추가")
                        if (word.size == 0) {
                            mBuilder.setMessage("작성된 단어가 없습니다. 이대로 단어장을 만드시겠습니까?")
                        } else {
                            mBuilder.setMessage("입력한 단어를 단어장으로 만드시겠습니까?")
                        }
                            .setNegativeButton("취소", null)
                            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                                Toast.makeText(this, "단어장 추가완료", Toast.LENGTH_SHORT).show()
                                // TODO: 2021-01-27 잠시 키보드 넣음
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                                val intent = Intent()
                                intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForAddOrEdit)
                                if (!checkActivity) {
                                    model?.insertAllDatas(word)
                                } else {
                                    intent.putParcelableArrayListExtra("word", word)
                                }
                                setResult(COMPLETE_CODE, intent)
                                dialog.dismiss()
                                finish()
                            })
                        mBuilder.create().show()
                    }
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
                        when {
                            findViewById<EditText>(currentFocusId + 1) != null -> {
                                findViewById<EditText>(currentFocusId + 1).requestFocus()
                            }
                            currentFocus!!.id == INPUT_MEAN_ID -> {
                                input_word.requestFocus()
                            }
                            currentFocus!!.id == INPUT_WORD_ID -> {
                                "do nothing"
                            }
                            else -> {
                                input_mean.requestFocus()
                            }
                        }
                    }
                    R.id.btn_move_right -> {
                        if (currentFocus!!.id == INPUT_WORD_ID) {
                            input_mean.requestFocus()
                            Log.d("     TAG", "===== AddWordActivity btn_move_right if문 ")
                        } else if (currentFocus!!.id == INPUT_MEAN_ID && addRecyclerAdapter.itemCount != 0) {
                            try {
                                findViewById<EditText>(((addRecyclerAdapter.itemCount - 1) * 2) + 1).requestFocus()
                            } catch (e: NullPointerException) {
                                return@OnClickListener
                            }
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
                        if (input_mean.text.toString().trim().isEmpty() || input_word.text.toString().trim().isEmpty()) {
                            Toast.makeText(this, "데이터를 올바르게 입력", Toast.LENGTH_SHORT).show()
                        } else {
//                        이제 word에 추가가되네 add로
                            addRecyclerAdapter.addItem(Word
                                (0,
                                input_word.text.toString(),
                                input_mean.text.toString(),
                                "",
                                0,
                                9999,
                                wordBookIdForAddOrEdit))
                            input_word.text.clear()
                            input_mean.text.clear()
                            input_word.requestFocus()
                            getCurrentCount("add")
                        }
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
        btn_add_finish_check.setOnClickListener(btnListener)
        btn_add_word_back_btn.setOnClickListener(btnListener)
        delete_all.setOnClickListener(btnListener)
        btn_remove_text.setOnClickListener(btnListener)
        btn_move_left.setOnClickListener(btnListener)
        btn_move_right.setOnClickListener(btnListener)
        btn_add_word.setOnClickListener(btnListener)
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentCount(AddOrRemove : String) {
        when (AddOrRemove) {
            "init" -> { currentCount = addRecyclerAdapter.itemCount }
            "add" -> { currentCount += 1 }
            "remove" -> { currentCount -= 1 }
        }
        binding.wordCount.text = currentCount.toString() + "개"
    }
    /*
* 이 액티비티에서 뷰홀더에서 onClick()된걸 아니까 이 액티비티에서 클릭처리를 할 수 있다
* */
    override fun onRemoveClicked(view: View, position: Int) {
        Log.d("     TAG","===== AddWordActivity onRemoveClicked position $position")
        val mBuilder = AlertDialog.Builder(view.context)
        mBuilder.setTitle("삭제")
            .setMessage("단어 : " + addRecyclerAdapter.getItem()[position].word.toString() + "\n뜻 : " + addRecyclerAdapter.getItem()[position].mean.toString() + "\n이 단어 항목을 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { _, _ ->
                    addRecyclerAdapter.removeItem(position)
                    getCurrentCount("remove")
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
        mBuilder.show()
    }
/*    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }*/

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val mBuilder = AlertDialog.Builder(this)
        when(item.itemId) {
            android.R.id.home -> { cancelDialog(mBuilder) }
            R.id.menu_check -> {
                // FIXME: 2021-01-20 아무값 입력없이 추가 예외처리
                mBuilder.setTitle("단어장으로 추가")
                if (word.size == 0) {
                    mBuilder.setMessage("작성된 단어가 없습니다. 이대로 단어장을 만드시겠습니까?")
                } else {
                    mBuilder.setMessage("입력한 단어를 단어장으로 만드시겠습니까?")
                }
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(this, "단어장 추가완료", Toast.LENGTH_SHORT).show()
                        // TODO: 2021-01-27 잠시 키보드 넣음
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        val intent = Intent()
                        intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForAddOrEdit)
                        if (!checkActivity) {
                            model?.insertAllDatas(word)
                        } else {
                            intent.putParcelableArrayListExtra("word", word)
                        }
                        setResult(COMPLETE_CODE, intent)
                        dialog.dismiss()
                        finish()
                    })
                mBuilder.create().show()
            }
        }
        return true
    }*/
    override fun onBackPressed() {
        cancelDialog(mBuilder = AlertDialog.Builder(this))
    }

    private fun cancelDialog(mBuilder: AlertDialog.Builder) {
        mBuilder.setTitle("단어장 취소")
            .setMessage("입력한 단어를 취소하고 이동하시겠습니까?")
            .setNegativeButton("취소", null)
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                // TODO: 2021-01-27 잠시 키보드 넣음
//                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                if (!checkActivity) {
                    val intent = Intent()
                    intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForAddOrEdit)
                    setResult(CANCEL_CODE, intent)
                }
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

}

/*
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
* */