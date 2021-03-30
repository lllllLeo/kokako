package com.yj.addwords

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yj.addwords.databinding.FragmentTestBinding
import com.yj.addwords.model.Word
import com.yj.addwords.viewModel.WordBookViewModel
import com.yj.addwords.viewModel.WordViewModel
import kotlinx.android.synthetic.main.fragment_test.*
import java.util.*

class TestFragment : Fragment() {
    private var                     _binding : FragmentTestBinding? = null
    private val                     binding get() = _binding!!
    private var                     wordBookModel: WordBookViewModel? = null
    private var                     model : WordViewModel? = null
    private var                     wordBookIdForTest : Long = 0
    private var                     test : Int = 0
    private var                     language = 0
    private var                     ttsStatus: Int? = 0
    private var                     ttsResult : Int = 0
    private var                     tts : TextToSpeech? = null
    private var                     wordList : ArrayList<Word>? = null
    private var                     currentCount = 1
    private var                     index = 0
    private var                     testScope : String? = null
    private var                     testCategory : String? = null
    private var                     testSort : String? = null
    lateinit var                    onCancelTest: OnCancelTest
    companion object {
        const val TAG = "TAG TestFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCancelTest = context as OnCancelTest
        val bundle : Bundle? = arguments
        wordBookIdForTest = bundle!!.getLong("wordBookIdForTest", 0)
        test = bundle.getInt("test")
        testScope = bundle.getString("testScope")
        testCategory = bundle.getString("testCategory")
        testSort = bundle.getString("testSort")

        Log.d(TAG, "onViewCreated: $testScope / $testCategory /$testSort")


        wordBookModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            WordBookViewModel::class.java)
        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(activity!!.application, wordBookIdForTest) as T
            }}).get(WordViewModel::class.java)

        language = wordBookModel?.getLanguageCode(wordBookIdForTest)!!
        textToSpeechInit(language)


        if (test == 0) { // 처음 테스트일 때
            Log.d(TAG, "onCreate: $testScope / $testCategory / $testSort / $wordBookIdForTest")
            when {  // 0 : 북마크 안 함  1 : 북마크 함 2 : 모든 단어  fixme 이렇게하면 나중에 언어대응할 때 막힘. 고치기
                testScope!!.contains("모") -> {
                    testScope = "2"
                }
                testScope!!.contains("안") -> {
                    testScope = "0"
                }
                else -> {
                    testScope = "1"
                }
            }
            testSort = when {
                testSort!!.contains("최") -> { "latest" }
                testSort!!.contains("등") -> { "oldest" }
                testSort!!.contains("단") -> { "word" }
                else -> { "random" }
            }
            Log.d(TAG, "onCreate22: $testScope / $testCategory / $testSort / $wordBookIdForTest")
            when {
                testScope!! == "2" -> { // 모든 단어
                    wordList = when {
                        testSort!! == "latest" -> {
                            model?.getTestAllWordLatest(wordBookIdForTest)
                        }
                        testSort!! == "oldest" -> {
                            model?.getTestAllWordOldest(wordBookIdForTest)
                        }
                        testSort!! == "word" -> {
                            model?.getTestAllWordWordAscOrder(wordBookIdForTest)
                        }
                        else -> {
                            model?.getTestAllWordRandomOrder(wordBookIdForTest)
                        }
                    }
                }
                else -> {    // 북마크 안/한 단어
                    wordList = when {
                        testSort!! == "latest" -> { model?.getTestBookmarkAllWordLatestOrder(wordBookIdForTest, testScope!!) }
                        testSort!! == "oldest" -> { model?.getTestBookmarkAllWordOldestOrder(wordBookIdForTest, testScope!!) }
                        testSort!! == "word" -> { model?.getTestBookmarkAllWordWordAscOrder(wordBookIdForTest, testScope!!) }
                        else -> { model?.getTestBookmarkAllWordRandomOrder(wordBookIdForTest, testScope!!) }
                    }
                }
            }
        } else if (test == 1) { // 한 번 더 테스트일때
            wordList = bundle.getParcelableArrayList<Word>("wordList")
        }


        /** 뷰 초기화*/
        val allCount = wordList!!.size.toString()

        if (wordList!!.size != 0) {
            setTestCount(currentCount, allCount)
            setTestText(index)
            btn_previous.isEnabled = false
            if(wordList!![index].bookMarkCheck == 0){
                binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
            } else
                binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
        } else {
            binding.notMatchedTest.visibility = View.VISIBLE
            binding.notMatchedTest.text = "설정된 단어가 없습니다.\n북마크를 추가/해제 해주세요."
            binding.countTest.visibility = View.INVISIBLE
            binding.btnPrevious.isEnabled = false
            binding.btnFalse.isEnabled = false
            binding.btnFalse.setTextColor(R.color.colorDarkGray)
            binding.btnTrue.isEnabled = false
            binding.btnTrue.setTextColor(R.color.colorDarkGray)
            binding.btnFavorite.isEnabled = false
            binding.btnFavorite.imageTintList =
                ContextCompat.getColorStateList(requireActivity().application, R.color.colorButtonGray)
            binding.testListenLayout.isEnabled = false
            binding.testListenImageview.isEnabled = false
        }

        val btnListener = View.OnClickListener { view ->
            val testResultFragment = TestResultFragment()
            val bundle = Bundle()
            val ft = (activity as TestActivity).supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(R.anim.fade_in_fragment, 0)
            try {
                when (view.id) {
                    R.id.test_back_btn -> {
                        cancelTest()
                    }
                    R.id.btn_previous -> {
                        currentCount -= 1
                        btn_previous.isEnabled = currentCount != 1
                        index -= 1
                        setTestCount(currentCount, allCount)
                        setTestText(index)
                        setBookmarked(index)
                        setAnswerInvisible()
                    }
                    R.id.btn_false -> {
                        Log.d(TAG, "onViewCreated: index $index")
                        Log.d(TAG, "onViewCreated: allCount.toInt() ${allCount.toInt()}")
                        Log.d(TAG, "onViewCreated: currentCount $currentCount")
                        wordList!![index].testCheck = 0
                        if (index == (allCount.toInt() - 1)) {
                            setTestResultFragment(bundle, testResultFragment, ft)
                        } else {
                            setNextTestValue(allCount)
                        }
                    }
                    R.id.btn_true -> {
                        Log.d(TAG, "onViewCreated: index $index")
                        Log.d(TAG, "onViewCreated: allCount.toInt() ${allCount.toInt()}")
                        Log.d(TAG, "onViewCreated: currentCount $currentCount")
                        wordList!![index].testCheck = 1
                        if (index == (allCount.toInt() - 1)) {
                            setTestResultFragment(bundle, testResultFragment, ft)
                        } else {
                            setNextTestValue(allCount)
                        }

                    }
                    R.id.btn_favorite -> {
                        if (wordList!![index].bookMarkCheck == 0) {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
                            wordList!![index].bookMarkCheck = 1
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
                            wordList!![index].bookMarkCheck = 0
                        }
                        model?.updateFavoriteChecked(wordList!![index])
//                        model?.updateFavoriteChecked(word) 이거는 테스트 끝낼때 한번에 ㄱㄱ 아이지 할떄해야지 계속 테스트를 하니까
                    }
                    R.id.answer_layout -> {
                        if(binding.answerTest.visibility == View.INVISIBLE) {
                            binding.answerTest.visibility = View.VISIBLE
                        } else
                            binding.answerTest.visibility = View.INVISIBLE
                    }
                    R.id.test_listen_layout -> {
                        ttSpeechSpeakButton(index)
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
        test_listen_layout.setOnClickListener(btnListener)
        test_back_btn.setOnClickListener(btnListener)
        btn_previous.setOnClickListener(btnListener)
        btn_false.setOnClickListener(btnListener)
        btn_true.setOnClickListener(btnListener)
        btn_favorite.setOnClickListener(btnListener)
        answer_layout.setOnClickListener(btnListener)
    }

    private fun setNextTestValue(allCount: String) {
        btn_previous.isEnabled = true
        currentCount += 1
        index += 1
        setTestCount(currentCount, allCount)
        setTestText(index)
        setBookmarked(index)
        setAnswerInvisible()
    }

    private fun setTestResultFragment(bundle: Bundle,testResultFragment: TestResultFragment,ft: FragmentTransaction,) {
        bundle.putLong("wordBookIdForTest", wordBookIdForTest)
        bundle.putInt("test", test)
        bundle.putString("testScope", testScope)
        bundle.putString("testCategory", testCategory)
        bundle.putString("testSort", testSort)
        bundle.putParcelableArrayList("wordList", wordList)
        testResultFragment.arguments = bundle
        ft.replace(R.id.test_frameLayout, testResultFragment).commit()
    }

    @SuppressLint("ResourceAsColor", "RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttSpeechSpeakButton(index: Int) {
        if (tts!!.isSpeaking) {  // 전체 듣기로 실행중일때 끄고 실행
            tts!!.stop()
            binding.testListenImageview.supportBackgroundTintList =
                ContextCompat.getColorStateList(requireActivity().application, R.color.colorButtonGray)
        }
        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            @SuppressLint("RestrictedApi")
            override fun onStart(utteranceId: String?) {
                binding.testListenImageview.supportBackgroundTintList =
                    ContextCompat.getColorStateList(requireActivity().application, R.color.colorBlack)
            }

            @SuppressLint("RestrictedApi")
            override fun onDone(utteranceId: String?) {
                binding.testListenImageview.supportBackgroundTintList =
                    ContextCompat.getColorStateList(requireActivity().application, R.color.colorButtonGray)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                Log.d(ViewWordActivity.TAG, "onError: error with $utteranceId - code $errorCode")
            }

            override fun onError(utteranceId: String?) {
                Log.d(ViewWordActivity.TAG, "onError: error with $utteranceId")
            }

        })
        val utteranceId = UUID.randomUUID().toString()
        val map = hashMapOf<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
        if (Build.VERSION.SDK_INT >= 21) {
            if (testCategory!!.contains("단어 - 뜻")) {
                tts?.speak(wordList!![index].word, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            } else {
                tts?.speak(wordList!![index].mean, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        } else {
            if (testCategory!!.contains("단어 - 뜻")) {
                tts?.speak(wordList!![index].word, TextToSpeech.QUEUE_FLUSH, map)
            } else {
                tts?.speak(wordList!![index].mean, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        }
    }

    private fun textToSpeechInit(language: Int) {
        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
        if (testCategory!!.contains("단어 - 뜻")) {
            tts = TextToSpeech(requireActivity().application, TextToSpeech.OnInitListener {
                when (language) {
                    0 -> { // 영어
                        ttsResult = tts?.setLanguage(Locale.ENGLISH)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    1 -> { // 영어(영국)
                        ttsResult = tts?.setLanguage(Locale.UK)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    2 -> { // 일본어
                        ttsResult = tts?.setLanguage(Locale.JAPANESE)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    3 -> { // 중국어
                        ttsResult = tts?.setLanguage(Locale.CHINESE)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    4 -> { // 스페인어
                        //                    val spanish : Locale = Locale("es", "ES")
                        //                    configuration.locale = spanish
                        //                    ttsResult = tts?.setLanguage(configuration.locale)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                        ttsResult = tts?.setLanguage(Locale("spa"))!!
                    }
                    5 -> { // 포르투갈어
                        ttsResult = tts?.setLanguage(Locale("pt-PT"))!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    6 -> { // 독일어
                        ttsResult = tts?.setLanguage(Locale.GERMAN)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    7 -> { // 프랑스어
                        ttsResult = tts?.setLanguage(Locale.FRENCH)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    8 -> { // 러시아어
                        ttsResult = tts?.setLanguage(Locale("ru"))!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    9 -> { // 베트남어
                        ttsResult = tts?.setLanguage(Locale("vi"))!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                    10 -> { // 한국어
                        ttsResult = tts?.setLanguage(Locale.KOREAN)!!
                        Log.d(ViewWordActivity.TAG, "textToSpeechInit: $language")
                    }
                }
            ttsStatus = it
            })
        }else {
            tts = TextToSpeech(requireActivity().application, TextToSpeech.OnInitListener {
                    ttsResult = tts?.setLanguage(Locale.KOREAN)!!
            ttsStatus = it
            })
        }
        if (ttsStatus == TextToSpeech.SUCCESS) {
            if (ttsResult == TextToSpeech.LANG_MISSING_DATA or TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "해당 언어의 음성 데이터가 없습니다. 음성 데이터를 설치해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setTestCount(currentCount: Int, allCount: String) {
        binding.countTest.text = "${currentCount}/$allCount"
    }

    private fun setAnswerInvisible() {
        binding.answerTest.visibility = View.INVISIBLE
    }

    private fun setBookmarked(index: Int) {
        if (wordList!![index].bookMarkCheck == 0) {
            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
        }
    }

    private fun setTestText(index : Int) {
        if (testCategory!!.contains("단어 - 뜻")) {
            binding.questionTest.text = wordList!![index].word.toString()
            binding.answerTest.text = wordList!![index].mean.toString()
        } else {
            binding.questionTest.text = wordList!![index].mean.toString()
            binding.answerTest.text = wordList!![index].word.toString()
        }
    }

    interface OnCancelTest {
        fun onCancelTest()
    }
    fun cancelTest() {
        onCancelTest.onCancelTest()
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

}

