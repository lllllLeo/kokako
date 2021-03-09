package com.example.kokako

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kokako.databinding.ActivityTestBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.activity_test.*
import java.lang.NullPointerException

class TestActivity : AppCompatActivity() {
//    private lateinit var toolbarBinding: ActivityToolbarBinding
    private var _binding : ActivityTestBinding? = null
    private val binding get() = _binding!!
    private var                     model : WordViewModel? = null
    private var wordBookIdForTest : Long = 0
    private var                     wordList : ArrayList<Word>? = null
    private var currentCount = 1
    private var index = 0
    private var testScope : String? = null
    private var testCategory : String? = null
    private var testSort : String? = null
    companion object {
        val TAG = "TAG TestActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        toolbarBinding = binding.includeToolbar


        wordBookIdForTest = intent.getLongExtra("wordBookIdForTest", 0)
        testScope = intent.getStringExtra("scope")
        testCategory = intent.getStringExtra("category")
        testSort = intent.getStringExtra("sort")


        val testFragment = TestFragment()
        val bundle = Bundle()
        bundle.putLong("wordBookIdForTest", wordBookIdForTest)
        bundle.putString("testScope", testScope)
        bundle.putString("testCategory", testCategory)
        bundle.putString("testSort", testSort)
        testFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.test_frameLayout, testFragment).commit()

/*        Log.d(TAG, "onCreate: $testScope / $testCategory / $testSort / $wordBookIdForTest")

        testScope = when {
            testScope!!.contains("모") -> { "2" }
            testScope!!.contains("안") -> { "0" }
            else -> { "1" }
        }
        testSort = when {
            testSort!!.contains("최") -> { "id" }
            testSort!!.contains("단") -> { "word" }
            else -> { "random" }
        }

        Log.d(TAG, "onCreate22: $testScope / $testCategory / $testSort / $wordBookIdForTest")

        */
        /**
         * 문제 범위 : 모든 단어            (값x)
         *            북마크한 단어        (WHERE bookMarkCheck = 1)
         *            북마크 안 한 단어    (WHERE bookMarkCheck = 0)
         * 문제 유형 : 단어 - 뜻
         *            뜻 - 단어
         * 정    렬 : 최신순               ORDER BY id ASC (값 x)
         *            문제 a-z순          ORDER BY word ASC    asc는 디폴트라서 안적어도 됨
         *            랜덤                ORDER BY random()~~~
         *
         *  모든 단어는 WHERE이 안붙으니까 따로 메서드 만들기
         *  @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView ORDER BY word | id | random")
         *
         * 북마크o 버전
         *  @Query("SELECT * FROM tb_word WHERE wordBookId = :wordBookIdForView AND bookmarkCheck = :scope ORDER BY word | id | random")
         *//*
// FIXME: 2021-03-08 뒤로가기 버튼 삽입하기
        *//*---- Tool Bar ----*//*
   *//*     setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.gravity = Gravity.LEFT
        toolbarBinding.toolbarTitle.text = "testest"*//*

        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForTest) as T
            }}).get(WordViewModel::class.java)

        if(testScope!! == "2") { // 모든 단어
            wordList = when {
                testSort!! == "id" -> { model?.getTestAllWord(wordBookIdForTest) }
                testSort!! == "word" -> { model?.getTestAllWordWordAscOrder(wordBookIdForTest) }
                else -> { model?.getTestAllWordRandomOrder(wordBookIdForTest) }
            }
        } else {    // 북마크 안/한 단어
            wordList = when {
                testSort!! == "id" -> { model?.getTestBookmarkAllWordRecentOrder(wordBookIdForTest, testScope!!) }
                testSort!! == "word" -> { model?.getTestBookmarkAllWordWordAscOrder(wordBookIdForTest, testScope!!) }
                else -> { model?.getTestBookmarkAllWordRandomOrder(wordBookIdForTest, testScope!!) }
            }
        }


        // TODO: 2021-03-08 문제 유형 단어 - 뜻에 따라 다르게 ㄱㄱ
        val allCount = wordList!!.size.toString()

        setTestCount(currentCount, allCount)
        setTestCategory(index)

        if(wordList!![index].bookMarkCheck == 0){
            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
        } else
            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
//        북마크체크도 해야함









        val btnListener = View.OnClickListener { view ->
            try {
                when (view.id) {
                    R.id.btn_previous -> {
                        currentCount -= 1
                        index -= 1
                        setTestCount(currentCount, allCount)
                        setTestCategory(index)
                        setBookmarked(index)
                        setAnswerInvisible()
                    }
                    R.id.btn_false -> {
                        currentCount += 1
                        index += 1
                        if (index == allCount.toInt()) {
                            // TODO: 2021-02-27 결과화면 프래그먼트, 라이브데이터 업뎃
                        }
                        setTestCount(currentCount, allCount)
                        setTestCategory(index)
                        setBookmarked(index)
                        setAnswerInvisible()
                    }
                    R.id.btn_true -> {
                        currentCount += 1
                        index += 1
                        if (index == allCount.toInt()) {
                            // TODO: 2021-02-27 결과화면 프래그먼트, 라이브데이터 업뎃

                        }
                        setTestCount(currentCount, allCount)
                        setTestCategory(index)
                        setBookmarked(index)
                        setAnswerInvisible()
                    }
                    R.id.btn_favorite -> {
                        if (wordList!![index].bookMarkCheck == 0) {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
                            wordList!![index].bookMarkCheck = 1
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
                            wordList!![index].bookMarkCheck = 0
                        }
//                        model?.updateFavoriteChecked(word) 이거는 테스트 끝낼때 한번에 ㄱㄱ
                    }
                    R.id.answer_layout -> {
                        if(binding.answerTest.visibility == View.INVISIBLE) {
                            binding.answerTest.visibility = View.VISIBLE
                        } else
                            binding.answerTest.visibility = View.INVISIBLE
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
        btn_previous.setOnClickListener(btnListener)
        btn_false.setOnClickListener(btnListener)
        btn_true.setOnClickListener(btnListener)
        btn_favorite.setOnClickListener(btnListener)
        answer_layout.setOnClickListener(btnListener)
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

    private fun setTestCategory(index : Int) {
        if (testCategory!!.contains("단어 - 뜻")) {
            binding.questionTest.text = wordList!![index].word.toString()
            binding.answerTest.text = wordList!![index].mean.toString()
        } else {
            binding.questionTest.text = wordList!![index].mean.toString()
            binding.answerTest.text = wordList!![index].word.toString()
        }
    }*/
    }
}