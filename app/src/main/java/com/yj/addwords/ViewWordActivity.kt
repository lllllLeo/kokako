package com.yj.addwords

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yj.addwords.databinding.ActivityViewWordBinding
import com.yj.addwords.model.CheckBoxData
import com.yj.addwords.model.VisibleCheckBoxData
import com.yj.addwords.model.Word
import com.yj.addwords.viewModel.WordBookViewModel
import com.yj.addwords.viewModel.WordViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_view_word.*
import kotlinx.android.synthetic.main.modal_bottom_sheet.view.*
import kotlinx.android.synthetic.main.rv_word_list.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface, BottomSheetDialog.BottomSheetInterface, TestSettingDialog.OnDataPass {
    private var                     deleteCountForTts: Int = 0
    private var                     adView : AdView? = null
    private var                     ttsStatus: Int? = 0
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
    private var                     sortSelectedIndex = 0
    private var                     hideSelectedIndex = 0
    private var                     testValue : ArrayList<String>? = null
    private var                     language = 0
    private var                     ttsResult : Int = 0
    private var                     tts : TextToSpeech? = null
    private var                     ttsArrayList = ArrayList<LinearLayout>()
    private var                     ttsCount = 0
    private var                     ssb : SpannableStringBuilder? = null
    private var                     anim : AlphaAnimation? = null
    /*private var                     currentShowSnackBarTime : Long = 0
    private var                     mLastClickTime : Long = 0L*/
    companion object {
//        const val                     MIN_CLICK_INTERVAL = 600
    const val                           TAG = "TAG ViewWordActivity"
        var                         checkboxList = ArrayList<CheckBoxData>()
        var                     isInvisibleItem : String? = null
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


        MobileAds.initialize(this, getString(R.string.admob_app_id))
        adView = binding.adView
        val adRequest : AdRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)









        /*---- Tool Bar ----*/
//        binding.toolbarTitle.gravity = Gravity.LEFT

//        val span : Spannable = wordBookNameForView as Spannable
        ssb = SpannableStringBuilder(wordBookNameForView)
        Log.d(TAG, "onCreate: $wordBookNameForView")
        ssb!!.setSpan(UnderlineSpan(), 0, wordBookNameForView!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


//        binding.toolbarTitle.gravity = Gravity.CENTER
//        binding.toolbarTitle.text = ssb
        binding.toolbarTitle.text = wordBookNameForView
        /*val span : Spannable = binding.toolbarTitle.text as Spannable
        span.setSpan(UnderlineSpan(),0,wordBookNameForView!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.toolbarTitle.text = span*/

//        binding.toolbarTitle.text = wordBookNameForView


        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom).apply {
                ViewCompat.onApplyWindowInsets(view, this)
            }
        }

        viewRecyclerAdapter = ViewWordRecyclerAdapter(this)
        checkboxList.clear()
        isDeleteMode(0, -2)
        setTtsProgressAnimation()


        wordBookModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(
            WordBookViewModel::class.java)
        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForView) as T
            }
        }).get(WordViewModel::class.java)

        language = wordBookModel?.getLanguageCode(wordBookIdForView)!!
        textToSpeechInit(language)  // tts 초기 셋팅
//        tts!!.setOnUtteranceProgressListener(TtsUtteranceListener())
        Log.d(TAG, "onCreate: 22222 $tts")
        wordList = wordModel?.getLatestOrder(wordBookIdForView)


        wordModel?.wordListLivedata?.observe(this, { updateWordList(it, sortId, wordList!!) })
        rv_list_word_view.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(viewRecyclerAdapter.itemCount,0)
//            scrollToPosition(viewRecyclerAdapter.itemCount-1)  안되네 ㅅㅂ
//            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            setHasFixedSize(true)
            adapter = viewRecyclerAdapter
        }



        val btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.view_back_btn -> {
                    goBackToPreviousActivity()
                }
                R.id.view_cancel_btn -> {
                    isDeleteMode(0, -1)
                    binding.hideSpinner.setSelection(0)
                }
                R.id.view_add_or_edit_btn -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        isSpeakingTts()
                    }
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
                R.id.view_all_listen_layout -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (tts != null) {
                            Log.d(TAG, "onCreate: tts가 널이 아님")
                            textToSpeechAllWord()
                        }
                    } else {
                        Toast.makeText(this, "안드로이드 버전이 낮아서 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        view_back_btn.setOnClickListener(btnListener)
        view_cancel_btn.setOnClickListener(btnListener)
        view_add_or_edit_btn.setOnClickListener(btnListener)
        view_delete_btn.setOnClickListener(btnListener)
        view_export_btn.setOnClickListener(btnListener)
        view_all_listen_layout.setOnClickListener(btnListener)

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
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_24_black, 0)
/*                    DrawableCompat.setTint(
                        DrawableCompat.wrap(context.getDrawable(R.drawable.ic_baseline_check_24)!!),
                        ContextCompat.getColor(context, R.color.colorBlue)
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
                        for (i in 0 until visibleCheckboxList.size) {
                            if (visibleCheckboxList[i].checked) {
                                visibleCheckboxList[i].checked = false
//                                visibleCheckBox.isSelected = visibleCheckboxList[i].checked
                            }
                        }
                        viewRecyclerAdapter.showAndHide(1)
                    } // 단어 가리기
                    2 -> {
                        for (i in 0 until visibleCheckboxList.size) {
                            if (visibleCheckboxList[i].checked) {
                                visibleCheckboxList[i].checked = false
//                                visibleCheckBox.isSelected = visibleCheckboxList[i].checked
                            }
                        }
                        viewRecyclerAdapter.showAndHide(2)
                    } // 뜻 가리기
                    /*3 -> {
                        for (i in 0 until visibleCheckboxList.size) {
                            if (visibleCheckboxList[i].checked) {
                                visibleCheckboxList[i].checked = false
//                                visibleCheckBox.isSelected = visibleCheckboxList[i].checked
                            }
                        }
                        viewRecyclerAdapter.showAndHide(3)
                    } // 랜덤*/
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        fab_test_word.setOnClickListener { _ ->

            if(wordList!!.size != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isSpeakingTts()
                }
                openTestSettingDialog()
            } else {
                Toast.makeText(this, "작성된 단어가 없습니다. 단어를 추가해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setTtsProgressAnimation() {
        anim = AlphaAnimation(0.0f, 1.0f)
        anim!!.duration = 100
        anim!!.startOffset = 20
        anim!!.repeatMode = Animation.REVERSE
        anim!!.repeatCount = Animation.INFINITE
    }
    private fun textToSpeechInit(language: Int) {
        Log.d(TAG, "textToSpeechInit: $language")
        // TODO: 2021-03-18 음성데이터가 없어도 재생이된다 갤s6에서
        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            when (language) {
                0 -> { // 영어
                    ttsResult = tts?.setLanguage(Locale.ENGLISH)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                1 -> { // 영어(영국)
                    ttsResult = tts?.setLanguage(Locale.UK)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                2 -> { // 일본어
                    ttsResult = tts?.setLanguage(Locale.JAPANESE)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                3 -> { // 중국어
                    ttsResult = tts?.setLanguage(Locale.CHINESE)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                4 -> { // 스페인어
//                    val spanish : Locale = Locale("es", "ES")
//                    configuration.locale = spanish
//                    ttsResult = tts?.setLanguage(configuration.locale)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                    ttsResult = tts?.setLanguage(Locale("spa"))!!
                }
                5 -> { // 포르투갈어
                    ttsResult = tts?.setLanguage(Locale("pt-PT"))!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                6 -> { // 독일어
                    ttsResult = tts?.setLanguage(Locale.GERMAN)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                7 -> { // 프랑스어
                    ttsResult = tts?.setLanguage(Locale.FRENCH)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                8 -> { // 러시아어
                    ttsResult = tts?.setLanguage(Locale("ru"))!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                9 -> { // 베트남어
                    ttsResult = tts?.setLanguage(Locale("vi"))!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
                10 -> { // 한국어
                    ttsResult = tts?.setLanguage(Locale.KOREAN)!!
                    Log.d(TAG, "textToSpeechInit: $language")
                }
            }
            ttsStatus = it
            if (ttsStatus == TextToSpeech.SUCCESS) {
                if (ttsResult == TextToSpeech.LANG_MISSING_DATA or TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "해당 언어의 음성 데이터가 없습니다. 음성 데이터를 설치해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    // TODO: 2021-03-18 낱개클릭, 전체클릭 난타하면 낱개 안꺼짐 한번씩 이럴애 거의업을테니까
    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun textToSpeechAllWord() {
        if (tts!!.isSpeaking) {  // 전체 듣기로 실행중일때 끄고 실행
            tts!!.stop()
            runOnUiThread {
                binding.viewAllListenLayout.backgroundTintList =
                    ContextCompat.getColorStateList(this@ViewWordActivity,
                        R.color.colorButtonGray)
                binding.viewTtsProgrssbar.clearAnimation()
                binding.viewTtsProgrssbar.visibility = View.INVISIBLE
            }
            for (i in ttsArrayList) {
                i.view_listen_layout.setBackgroundResource(R.drawable.button_border)
                i.view_listen_imageview.supportBackgroundTintList =
                    ContextCompat.getColorStateList(this@ViewWordActivity, R.color.colorButtonGray)
            }
            ttsArrayList.clear()
        } else {
            tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // TODO: 2021-03-17 단어가 많으면 시작할떄 좀 느리게 반응하네
                    Log.d(TAG, "onStart: start with $utteranceId")
                    runOnUiThread {
                        binding.viewTtsProgrssbar.visibility = View.VISIBLE
                        binding.viewTtsProgrssbar.startAnimation(anim)
                        binding.viewAllListenLayout.backgroundTintList =
                            ContextCompat.getColorStateList(this@ViewWordActivity,
                                R.color.colorBlue)
                    }
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(TAG, "onDone: done with $utteranceId")
                    ttsCount += 1
                    Log.d(TAG, "onDone: ${ttsCount} ${wordList!!.size} ${deleteCountForTts}")
                    if (ttsCount == (wordList!!.size - deleteCountForTts)) {
                        Log.d(TAG, "onDone: $ttsCount 번")
                        runOnUiThread {
                            binding.viewTtsProgrssbar.visibility = View.INVISIBLE
                            binding.viewTtsProgrssbar.clearAnimation()
                            binding.viewAllListenLayout.backgroundTintList =
                                ContextCompat.getColorStateList(this@ViewWordActivity,
                                    R.color.colorButtonGray)
                        }
                        ttsCount = 0
                        deleteCountForTts = 0
                    }
                }

                //  API 21 기준으로는 onError(utteranceId: String?, errorCode: Int) 가 필요하고 그 미만으로는 onError(utteranceId: String?) 가 필요하다.
                override fun onError(utteranceId: String?, errorCode: Int) {
                    super.onError(utteranceId, errorCode)

                    Log.d(TAG, "onError: error with $utteranceId - code $errorCode")
                }

                override fun onError(utteranceId: String?) {
                    Log.d(TAG, "onError: error with $utteranceId")
                }
            })
            if (wordList != null) {
                val utteranceId = UUID.randomUUID().toString()
                val map = hashMapOf<String, String>()
                map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId

                for (word in wordList!!) {
                    if (Build.VERSION.SDK_INT >= 21) { //LOLLIPOP
                        tts?.speak(word.word, TextToSpeech.QUEUE_ADD, null, utteranceId)
                        tts?.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null) // 지정한 시간만큼 무음을 추가
                    } else {
                        tts?.speak(word.word, TextToSpeech.QUEUE_ADD, map)
                        tts?.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null) // 지정한 시간만큼 무음을 추가
                    }
                }
            }

        }
    }
    @SuppressLint("ResourceAsColor", "RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun ontextToSpeechSpeakButtonClicked(v: View, adapterPosition: Int) {
        isSpeakingTts()     // 전체 듣기로 실행중일때 끄고 실행
        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            @SuppressLint("RestrictedApi")
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "onStart: start with $utteranceId")
                if (!ttsArrayList.contains(v.view_listen_layout)) {
                    Log.d(TAG, "onStart: 존재하지 않는다면 추가")
                    ttsArrayList.add(v.view_listen_layout)
                }
                runOnUiThread { // Main Thread 외의 새로 생성한 Thread를 이용하여 임의로 UI를 변경시키려고 했기 때문
                    v.view_listen_layout.setBackgroundResource(R.drawable.button_round)
                    v.view_listen_imageview.supportBackgroundTintList =
                        ContextCompat.getColorStateList(this@ViewWordActivity, R.color.colorWhite)
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "onDone: done with $utteranceId")
                runOnUiThread {
                    for (i in ttsArrayList) {
                        i.view_listen_layout.setBackgroundResource(R.drawable.button_border)
                        i.view_listen_imageview.supportBackgroundTintList =
                            ContextCompat.getColorStateList(this@ViewWordActivity, R.color.colorButtonGray)
                    }
                }
            }

            //                            API 21 기준으로는 onError(utteranceId: String?, errorCode: Int) 가 필요하고 그 미만으로는 onError(utteranceId: String?) 가 필요하다.
            override fun onError(utteranceId: String?, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                Log.d(TAG, "onError: error with $utteranceId - code $errorCode")
            }

            override fun onError(utteranceId: String?) {
                Log.d(TAG, "onError: error with $utteranceId")
            }

        })
        val utteranceId = UUID.randomUUID().toString()
        val map = hashMapOf<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
        if (Build.VERSION.SDK_INT >= 21) {
            tts?.speak(wordList!![adapterPosition].word, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            tts?.speak(wordList!![adapterPosition].word, TextToSpeech.QUEUE_FLUSH, map)
        }
//        tts?.speak(wordList!![adapterPosition].word, TextToSpeech.QUEUE_ADD, null, null)
    }

    // FIXME: 2021-03-23 플로우가 좀 이상 고치기
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isSpeakingTts() {
        if (tts!!.isSpeaking) {
            tts!!.stop()
            runOnUiThread {
                binding.viewTtsProgrssbar.visibility = View.INVISIBLE
                binding.viewTtsProgrssbar.clearAnimation()
                binding.viewAllListenLayout.backgroundTintList =
                    ContextCompat.getColorStateList(this@ViewWordActivity,
                        R.color.colorButtonGray)
    //                for (i in ttsArrayList) {   // 이거 넣으니까 중복 재생 할 때 마지막값만 켜지고 전값 다 꺼지네
    //                    i.view_listen_layout.setBackgroundResource(R.drawable.button_border)
    //                    i.view_listen_imageview.supportBackgroundTintList =
    //                        ContextCompat.getColorStateList(this@ViewWordActivity, R.color.colorButtonGray)
    //                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
            tts = null
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
        val intent = Intent(this, TestActivity::class.java).apply {
            putExtra("wordBookIdForTest", wordBookIdForView)
            putExtra("testScope", testValue!![0])
            putExtra("testCategory", testValue!![1])
            putExtra("testSort", testValue!![2])
        }
        startActivityForResult(intent, TEST_WORD_CODE)
    //            start result해야함 받고 livedata
    }
    @SuppressLint("SetTextI18n")
    private fun updateWordList(word: List<Word>, sortId: Int, wordList: ArrayList<Word>) {
//     이렇게 word, wordList 두개로 받는 이유는 데이터를 업뎃하면 viewmodel만들 때 작성해놓은 메서드에서 getall로 데이터를 가져오는데
//        이 메서드가 등록된 순으로 가져오니까 만약 단어 오름차순에서 데이터업뎃(북마크, 단어편집)할 경우 단어 오름차순으로 정렬한게
//        무너지고 등록된 순으로 가져오니까 맨밑에잇는 메서드로 다시 ordered wordlist로 받아와서 뿌려줌. 그래서 정렬된 상태(sortId != 0)에서 updateWordList를 하는경우는
//        word는 단지 인자를 채우기 위해서 보내는 의미없는 인자이고 wordList가 찐 인자임

        /*Log.d(TAG, "WordList: word 9090 $word")
        Log.d(TAG, "updateWordList: 9090 sortId $sortId")
        Log.d(TAG, "updateWordList: 9090 wordList $wordList")*/
        if (sortId == 0) {
//            Log.d(TAG, "updateWordList: if문")
            viewRecyclerAdapter.submitList(word as ArrayList<Word>)
            binding.currentCount.text = word.size.toString()
            binding.currentCount2.text = "개"
            if(word.isNotEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyIcon.visibility = View.GONE
                binding.fabTestWord.visibility = View.VISIBLE
            } else {
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyText.text = "작성된 단어가 없습니다.\n단어를 추가해주세요"
                binding.emptyIcon.visibility = View.VISIBLE
                binding.fabTestWord.visibility = View.GONE
            }
        } else {
//            Log.d(TAG, "updateWordList: else문") // 리사이클러뷰만 살아잇는거임(이전데이터) 실제데이터는 사라짐
            viewRecyclerAdapter.submitList(wordList)
            binding.currentCount.text = wordList.size.toString()
            binding.currentCount2.text = "개"
            if(wordList.isNotEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyIcon.visibility = View.GONE
                binding.fabTestWord.visibility = View.VISIBLE
            } else {
                if(sortId == 2) {
                    Log.d(TAG, "updateWordList: if문 $sortId")
                    binding.emptyText.text = "북마크한 단어가 없습니다"
                } else {
                    binding.emptyText.text = "작성된 단어가 없습니다.\n단어를 추가해주세요"
                    Log.d(TAG, "updateWordList: else문 $sortId")
                }
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyIcon.visibility = View.VISIBLE
                binding.fabTestWord.visibility = View.GONE
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    override fun onCheckboxClicked(v: View, wordMeanLayout: LinearLayout, adapterPosition: Int) {
            if (v.view_check.isChecked) {
                checkboxList[adapterPosition].checked = true
                checkboxCount += 1
            } else {
                checkboxList[adapterPosition].checked = false
                checkboxCount -= 1
            }
            isCheckboxCountEmpty()
            wordMeanLayout.isSelected = checkboxList[adapterPosition].checked
            binding.toolbarTitle.text = "${checkboxCount.toString()} 개 선택됨"
}
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewClicked(v: View, wordMeanLayout: LinearLayout, adapterPosition: Int) {
            if (v.view_check.visibility == View.VISIBLE) {
                v.view_check.isChecked = !v.view_check.isChecked
                if (v.view_check.isChecked) {
                    checkboxList[adapterPosition].checked = true
                    checkboxCount += 1
                } else {
                    checkboxList[adapterPosition].checked = false
                    checkboxCount -= 1
                }
                isCheckboxCountEmpty()
                wordMeanLayout.isSelected = checkboxList[adapterPosition].checked
                binding.toolbarTitle.text = "${checkboxCount.toString()} 개 선택됨"
            } else {
                val wordForBottomSheet = viewRecyclerAdapter.getItem()[adapterPosition]
                val bottomSheetDialog = BottomSheetDialog(wordForBottomSheet, adapterPosition)
                bottomSheetDialog.show(supportFragmentManager, "example-bottom-sheet")
            }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isCheckboxCountEmpty() {
        if (checkboxCount == 0) {
            binding.viewDeleteBtn.isEnabled = false
            binding.viewDeleteBtn.imageTintList =
                ContextCompat.getColorStateList(this@ViewWordActivity, R.color.colorButtonGray)
        } else {
            binding.viewDeleteBtn.isEnabled = true
            binding.viewDeleteBtn.imageTintList =
                ContextCompat.getColorStateList(this@ViewWordActivity, R.color.colorWhite)
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
                        Snackbar.make(binding.coordinatorLayout, "단어 삭제 완료", Snackbar.LENGTH_LONG).show()
//                        Toast.makeText(this, "단어 삭제 완료", Toast.LENGTH_SHORT).show()
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
        }
    }
    override fun onVisibilityCheckboxLayoutClicked(v: View,_visibilityOptions: Int,wordTextView: TextView,
        meanTextView: TextView,adapterPosition: Int,) {
        v.visible_check.isChecked = !v.visible_check.isChecked
        setVisivilityOptions(v, adapterPosition, _visibilityOptions, wordTextView, meanTextView)
    }
    override fun onVisibilityCheckboxClicked(v: View, _visibilityOptions: Int, wordTextView: TextView, meanTextView: TextView, adapterPosition: Int,) {
        setVisivilityOptions(v, adapterPosition, _visibilityOptions, wordTextView, meanTextView)
    }

    private fun setVisivilityOptions(
        v: View,
        adapterPosition: Int,
        _visibilityOptions: Int,
        wordTextView: TextView,
        meanTextView: TextView,
    ) {
        if (v.visible_check.isChecked) {
            visibleCheckboxList[adapterPosition].checked = true
        } else {
            visibleCheckboxList[adapterPosition].checked = false
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
        } else if (_visibilityOptions == 3) {
            if (visibleCheckboxList[adapterPosition].checked) {
                if (wordTextView.visibility == View.INVISIBLE) {
                    wordTextView.visibility = View.VISIBLE
                    isInvisibleItem = "word"
                } else if (meanTextView.visibility == View.INVISIBLE) {
                    meanTextView.visibility = View.VISIBLE
                    isInvisibleItem = "mean"
                }
            } else {
                if (isInvisibleItem.equals("word")) {
                    wordTextView.visibility = View.INVISIBLE
                } else {
                    meanTextView.visibility = View.INVISIBLE
                }
            }
        }
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
            binding.dummyForCenter.visibility = View.GONE

//            binding.toolbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack))
            binding.toolbarTitle.gravity = Gravity.LEFT
            binding.toolbarTitle.text = "${checkboxCount.toString()} 개 선택됨"
//            binding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.colorWhite))

//            binding.fabTestWord.visibility = View.INVISIBLE
            binding.hideSpinner.visibility = View.INVISIBLE
            binding.sortSpinner.visibility = View.INVISIBLE
            binding.currentCount.visibility = View.GONE
            binding.currentCount2.visibility = View.GONE
            binding.viewAllListenLayout.visibility = View.GONE
            binding.viewTtsProgrssbar.visibility = View.GONE
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
            binding.dummyForCenter.visibility = View.INVISIBLE

//            binding.toolbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.toolbarTitle.gravity = Gravity.CENTER
            binding.toolbarTitle.text = wordBookNameForView
//            binding.toolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.colorBlack))
//            binding.toolbarTitle.text = ssb

//            binding.fabTestWord.visibility = View.VISIBLE
            binding.hideSpinner.visibility = View.VISIBLE
            binding.sortSpinner.visibility = View.VISIBLE
            binding.currentCount.visibility = View.VISIBLE
            binding.currentCount2.visibility = View.VISIBLE
            binding.viewAllListenLayout.visibility = View.VISIBLE
            binding.viewAllListenLayout.visibility = View.VISIBLE
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
                            Snackbar.make(binding.listView, "단어장 수정 완료", Snackbar.LENGTH_LONG).show()
                        }
                        CANCEL_CODE -> {
                        }
                    }
                }
                TEST_WORD_CODE -> {
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

    override fun onFavoriteButtonLayoutClicked(v: View, adapterPosition: Int) {
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
        if (view.bsd_input_word.text!!.trim().isEmpty() && view.bsd_input_mean.text!!.trim().isEmpty()) {
            Toast.makeText(this, "단어나 뜻을 입력해주세요", Toast.LENGTH_SHORT).show()
            _isUpdated = false
        } else {
            try {
                if ((word.word!!) != view.bsd_input_word.text.toString() || (word.mean!!) != view.bsd_input_mean.text.toString()) {
                    // 하나라도 이전 값들과 같지 않으면
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    word.word = view.bsd_input_word.text.toString()
                    word.mean = view.bsd_input_mean.text.toString()
                    wordModel?.update(word)
                    getSortWhen(sortId)
                }
            } catch (e:NullPointerException) {
                e.printStackTrace()
            }
//           // 이전값들과 같으면
            _isUpdated = true
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
        deleteCountForTts -= 1
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
            deleteCountForTts += 1
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
        actionSnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorBlue))
        actionSnackbar.show()
    }
    private fun getSortWhen(sortId: Int) {
        when (sortId) {
            0 -> {
                getLatestOrder()
            } // 최신순
            1 -> {
                getOldestOrder()
            } // 등록순
            2 -> {
                getWordFavoriteOrder()
            } // 별표순
            3 -> {
                getWordAscendingOrder()
            } // 단어순 ▲
            4 -> {
                getWordDescendingOrder()
            } // 단어순 ▼
            5 -> {
                getMeanAscendingOrder()
            } // 뜻 ▲
            6 -> {
                getMeanDescendingOrder()
            } // 뜻 ▼
            7 -> {
                getRandomOrder()
            } // 랜덤
        }
    }
    private fun getLatestOrder() {
        sortId = 0
        hide_spinner.setSelection(0)
        wordList = wordModel?.getLatestOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getOldestOrder() {
        sortId = 1
        hide_spinner.setSelection(0)
        wordList = wordModel?.getOldestOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordFavoriteOrder() {
        sortId = 2
        hide_spinner.setSelection(0)
        wordList = wordModel?.getWordFavoriteOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordAscendingOrder() {
        sortId = 3
        hide_spinner.setSelection(0)
        wordList = wordModel?.getWordAscendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getWordDescendingOrder() {
        sortId = 4
        hide_spinner.setSelection(0)
        wordList = wordModel?.getWordDescendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getMeanAscendingOrder() {
        sortId = 5
        hide_spinner.setSelection(0)
        wordList = wordModel?.getMeanAscendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getMeanDescendingOrder() {
        sortId = 6
        hide_spinner.setSelection(0)
        wordList = wordModel?.getMeanDescendingOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }
    private fun getRandomOrder() {
        sortId = 7
        hide_spinner.setSelection(0)
        wordList = wordModel?.getRandomOrder(wordBookIdForView)
        updateWordList(wordList!!, sortId, wordList!!)
    }


}