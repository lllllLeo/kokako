package com.yj.addwords

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Selection
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yj.addwords.databinding.ActivityAddWordBinding
import com.yj.addwords.model.Word
import com.yj.addwords.viewModel.WordViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_add_word.*
import kotlinx.android.synthetic.main.activity_view_word.view.*

//  Log.d("     TAG", "===== AddWordActivity")
// TODO: 2021-01-27 키보드를 아예 보이지말까
// TODO: 2021-02-02 추가하면 스크롤 위로안감
class AddWordActivity : AppCompatActivity(), AddRecyclerViewInterface {
    private var adView : AdView? = null
    private var                     _binding : ActivityAddWordBinding? = null
    private val                     binding get() = _binding!!
    private lateinit var            addRecyclerAdapter: AddRecyclerAdapter
    private var                     model : WordViewModel? = null
    private var                     currentCount: Int = 0
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
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        input_word.id =             INPUT_WORD_ID
        input_mean.id =             INPUT_MEAN_ID
        wordBookIdForAddOrEdit = intent.getLongExtra("wordBookIdForAddOrEdit",0)
        checkActivity = intent.getBooleanExtra("checkActivity",false)


        MobileAds.initialize(this, getString(R.string.admob_app_id))
        adView = binding.adView
        val adRequest : AdRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)





        rv_list_item.isFocusable = false

        binding.toolbarTitle.gravity = Gravity.CENTER
        binding.inputWord.privateImeOptions = "defaultInputmode=english"



// TODO: 2021-01-27 19:21분 데이터로 고치기

//        데이터핸들링은 뷰모델을 통해서만 한다.
        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForAddOrEdit) as T
            }}).get(WordViewModel::class.java)

        addRecyclerAdapter = AddRecyclerAdapter(this)
//        word            = model?.getLatestOrder(wordBookIdForAddOrEdit)!!
        word            = model?.getOldestOrder(wordBookIdForAddOrEdit)!! // 원래는 getLatestOrder으로 뽑아야하는데 이거하고 역순false하고 position에0주고 추가로 하면 뷰에서 정렬이 최신순, 등록순이 반대로 나옴
        Log.d(TAG, "onCreate: $word")
        addRecyclerAdapter.submitDataList(word)

        if(checkActivity) {
            binding.toolbarTitle.text = "[ 단어장 수정 ]"
            binding.emptyText.visibility = View.GONE
            if (word.size == 0) {
                binding.btnAddFinishCheck.isEnabled = false // 편집으로 들어올때 0개로 들어올 수 있으니 0개면 버튼 비활성화
                binding.btnAddFinishCheck.imageTintList =
                    ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorButtonGray)
                binding.emptyText.visibility = View.VISIBLE
            }
        } else {
            binding.toolbarTitle.text = "[ 단어장 추가 ]"
            binding.btnAddFinishCheck.isEnabled = false // 처음 단어장 추가로 들어오면 단어가 없을테니까 버튼을 비활성화 초기설정
            binding.btnAddFinishCheck.imageTintList =
                ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorButtonGray)
            binding.emptyText.visibility = View.VISIBLE
        }











        rv_list_item.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)    // 역순  역순이니 맨 밑이 position 0
            (layoutManager as LinearLayoutManager).stackFromEnd = true  // 역순
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
                        // FIXME: 2021-03-21 단어, 뜻 전부 지우고 체크 누르려고하면 addviewholder에서 position별로 체크해서 리스트만들고 입력하고 이게 하나라도 둘다 null이면 체크버튼은 회색으로
                        if (checkActivity) {
                            mBuilder.setTitle("단어장 수정")
                            mBuilder.setMessage("입력한 단어로 단어장을 수정하시겠습니까?")
                        } else {
                            mBuilder.setTitle("단어장 추가")
                            mBuilder.setMessage("입력한 단어를 단어장으로 만드시겠습니까?")
                        }
                            .setNegativeButton("취소", null)
                            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                                // TODO: 2021-01-27 잠시 키보드 넣음
                                val intent = Intent()
                                intent.putExtra("wordBookIdForAddOrEdit", wordBookIdForAddOrEdit)
                                if (!checkActivity) { // 수정
                                    model?.insertAllDatas(word)
                                } else { // 추가
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
                        } else if (input_mean.isFocused) {
                            input_mean.text.clear()
                        } else if (findViewById<EditText>(currentFocusId) != null ) {
                            findViewById<EditText>(currentFocusId).text.clear()
                        }
                    }
                    R.id.btn_move_left -> {
                        when {
                            findViewById<EditText>(currentFocusId + 1) != null -> {
                                findViewById<EditText>(currentFocusId + 1).requestFocus()
                                val editableText = findViewById<EditText>(currentFocusId + 1).text
                                Selection.setSelection(editableText, editableText.length)
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
                                val editableText = findViewById<EditText>(((addRecyclerAdapter.itemCount - 1) * 2) + 1).text
                                Selection.setSelection(editableText, editableText.length)
                            } catch (e: NullPointerException) {
                                return@OnClickListener
                            }
                        } else if (findViewById<EditText>(currentFocusId - 1) != null) {
                            Log.d("     TAG", "===== AddWordActivity btn_move_right else if문 2")
                            findViewById<EditText>(currentFocusId - 1).requestFocus()
                            val editableText = findViewById<EditText>(currentFocusId - 1).text
                            Selection.setSelection(editableText, editableText.length)
                        }
                    }
// data없으면  무시하기 -> textViewLayout?
                    R.id.btn_add_word -> {
                        // TODO: 2021-03-21 추가하면 애니메이션으로 fade out?
                        input_word.text.toString().trim()
                        input_mean.text.toString().trim()
                        if (input_mean.text.toString().trim().isEmpty() && input_word.text.toString().trim()
                                .isEmpty()
                        ) {
                            Toast.makeText(this, "단어나 뜻을 입력해주세요.", Toast.LENGTH_SHORT).show()
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
                            rv_list_item.smoothScrollToPosition(word.size)  // 상단에 추가 후 스크롤을 맨 위로 올림
                            input_word.text.clear()
                            input_mean.text.clear()
                            input_word.requestFocus()
                            getCurrentCount("add")
                            if (checkActivity) { // 수정
                                if (word.size != 0) {
                                    binding.btnAddFinishCheck.isEnabled = true
                                    binding.btnAddFinishCheck.imageTintList =
                                        ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorBlack)
                                    binding.emptyText.visibility = View.GONE
                                    Log.d(TAG, "gone")
                                }
                            } else {
                                if (addRecyclerAdapter.itemCount != 0) {
                                    Log.d(TAG, "onCreate: ${addRecyclerAdapter.itemCount}")
                                    binding.btnAddFinishCheck.isEnabled = true
                                    binding.btnAddFinishCheck.imageTintList =
                                        ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorBlack)
                                    binding.emptyText.visibility = View.GONE
                                    Log.d(TAG, "gone")
                                }
                            }
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
        binding.currentCount.text = currentCount.toString()
        binding.currentCount2.text = "개"
    }
    /*
* 이 액티비티에서 뷰홀더에서 onClick()된걸 아니까 이 액티비티에서 클릭처리를 할 수 있다
* */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRemoveClicked(view: View, position: Int) {
        val mBuilder = AlertDialog.Builder(view.context)
        mBuilder.setTitle("삭제")
            .setMessage("단어 : " + addRecyclerAdapter.getItem()[position].word.toString() + "\n뜻 : " + addRecyclerAdapter.getItem()[position].mean.toString() + "\n이 단어 항목을 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { _, _ ->
                    addRecyclerAdapter.removeItem(position)
                    getCurrentCount("remove")
                    if (checkActivity) {    // 수정
                        if (word.size != 0) {
                            binding.btnAddFinishCheck.isEnabled = true
                            binding.btnAddFinishCheck.imageTintList =
                                ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorBlack)
                            binding.emptyText.visibility = View.GONE
                            Log.d(TAG, "gone")
                        } else {
                            binding.btnAddFinishCheck.isEnabled = false
                            binding.btnAddFinishCheck.imageTintList =
                                ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorButtonGray)
                            binding.emptyText.visibility = View.VISIBLE
                            Log.d(TAG, "visi2 수정")
                        }
                    } else {
                        Log.d(TAG, "onCreate: 단어장추가로들어옴")
                        if (addRecyclerAdapter.itemCount != 0) {
                            Log.d(TAG, "onCreate: ${addRecyclerAdapter.itemCount}")
                            binding.btnAddFinishCheck.isEnabled = true
                            binding.btnAddFinishCheck.imageTintList =
                                ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorBlack)
                            binding.emptyText.visibility = View.GONE
                            Log.d(TAG, "gone")
                        } else {
                            binding.btnAddFinishCheck.isEnabled = false
                            binding.btnAddFinishCheck.imageTintList =
                                ContextCompat.getColorStateList(this@AddWordActivity, R.color.colorButtonGray)
                            binding.emptyText.visibility = View.VISIBLE
                            Log.d(TAG, "visi2 추가")
                        }
                    }
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
        mBuilder.show()


    }
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
