package com.yj.addwords

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yj.addwords.databinding.FragmentTestShortAnswerBinding
import com.yj.addwords.model.Word
import com.yj.addwords.model.WordBook
import com.yj.addwords.viewModel.WordBookViewModel
import com.yj.addwords.viewModel.WordViewModel
import java.util.*

// 주관식
class TestShortAnswerFragment : Fragment() {
    private var                     _binding : FragmentTestShortAnswerBinding? = null
    private val                     binding get() = _binding!!
    private var                     wordBookModel: WordBookViewModel? = null
    private var                     model : WordViewModel? = null
    private var                     wordBookIdForTest : Long = 0
    private var                     language = 0
    private var                     wordList : ArrayList<Word>? = null
    private var                     tts : TextToSpeech? = null
    private var                     ttsStatus: Int? = 0
    private var                     ttsResult : Int = 0
    private var                     test : Int = 0      // testStatus? testCount?
    private var                     testScope : String? = null
    private var                     testCategory : String? = null
    private var                     testSort : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTestShortAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle : Bundle? = arguments
        wordBookIdForTest = bundle!!.getLong("wordBookIdForTest", 0)
        test = bundle.getInt("test")
        testScope = bundle.getString("testScope")
        testCategory = bundle.getString("testCategory")
        testSort = bundle.getString("testSort")

        wordBookModel = ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(WordBookViewModel::class.java)
        model = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(activity!!.application, wordBookIdForTest) as T
            }
        }).get(WordViewModel::class.java)

        language = wordBookModel?.getLanguageCode(wordBookIdForTest)!!
        textToSpeechInit(language)

        if (test == 0) { // 처음 테스트일 때
            Log.d(TestFragment.TAG, "onCreate: $testScope / $testCategory / $testSort / $wordBookIdForTest")
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
            Log.d(TestFragment.TAG, "onCreate22: $testScope / $testCategory / $testSort / $wordBookIdForTest")
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
                        testSort!! == "latest" -> { model?.getTestBookmarkAllWordLatestOrder(wordBookIdForTest,
                            testScope!!) }
                        testSort!! == "oldest" -> { model?.getTestBookmarkAllWordOldestOrder(wordBookIdForTest,
                            testScope!!) }
                        testSort!! == "word" -> { model?.getTestBookmarkAllWordWordAscOrder(wordBookIdForTest,
                            testScope!!) }
                        else -> { model?.getTestBookmarkAllWordRandomOrder(wordBookIdForTest, testScope!!) }
                    }
                }
            }
        } else if (test == 1) { // 한 번 더 테스트일때
            wordList = bundle.getParcelableArrayList<Word>("wordList")
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
}