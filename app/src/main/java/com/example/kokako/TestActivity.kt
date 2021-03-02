package com.example.kokako

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kokako.databinding.ActivityTestBinding
import com.example.kokako.databinding.ActivityToolbarBinding
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

        /*---- Tool Bar ----*/
   /*     setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.gravity = Gravity.LEFT
        toolbarBinding.toolbarTitle.text = "testest"*/


        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application, wordBookIdForTest) as T
            }}).get(WordViewModel::class.java)

        wordList = model?.getRecentOrder(wordBookIdForTest)

        val allCount = wordList!!.size.toString()
        binding.countTest.text = "$currentCount/$allCount"
        binding.questionTest.text = wordList!![index].word.toString()
        binding.answerTest.text = wordList!![index].mean.toString()
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
                        binding.countTest.text = "$currentCount/$allCount"
                        binding.questionTest.text = wordList!![index].word.toString()
                        binding.answerTest.text = wordList!![index].mean.toString()
                        if (wordList!![index].bookMarkCheck == 0) {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
                        }
                        binding.answerTest.visibility = View.INVISIBLE
                    }
                    R.id.btn_false -> {
                        currentCount += 1
                        index += 1
                        if (index == allCount.toInt()) {
                            // TODO: 2021-02-27 결과화면
                        }
                        binding.countTest.text = "$currentCount/$allCount"
                        binding.questionTest.text = wordList!![index].word.toString()
                        binding.answerTest.text = wordList!![index].mean.toString()
                        if (wordList!![index].bookMarkCheck == 0) {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
                        }
                        binding.answerTest.visibility = View.INVISIBLE
                    }
                    R.id.btn_true -> {
                        currentCount += 1
                        index += 1
                        if (index == allCount.toInt()) {
                            // TODO: 2021-02-27 결과화면

                        }
                        binding.countTest.text = "$currentCount/$allCount"
                        binding.questionTest.text = wordList!![index].word.toString()
                        binding.answerTest.text = wordList!![index].mean.toString()
                        if (wordList!![index].bookMarkCheck == 0) {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
                        }
                        binding.answerTest.visibility = View.INVISIBLE
                    }
                    R.id.btn_favorite -> {
                        if (wordList!![index].bookMarkCheck == 0) {
                            Log.d(TAG, "onCreate: btn_favorite if문")
                            binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
                            wordList!![index].bookMarkCheck = 1
                        } else {
                            Log.d(TAG, "onCreate: btn_favorite else문")
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
}