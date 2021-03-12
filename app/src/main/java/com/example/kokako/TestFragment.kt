package com.example.kokako

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kokako.databinding.FragmentTestBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.fragment_test.*
import java.lang.NullPointerException

class TestFragment : Fragment() {
    private var                     _binding : FragmentTestBinding? = null
    private val                     binding get() = _binding!!
    private var                     model : WordViewModel? = null
    private var                     wordBookIdForTest : Long = 0
    private var                     test : Int = 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCancelTest = context as OnCancelTest
        val bundle : Bundle? = arguments
        wordBookIdForTest = bundle!!.getLong("wordBookIdForTest", 0)
        test = bundle!!.getInt("test")
        testScope = bundle!!.getString("testScope")
        testCategory = bundle!!.getString("testCategory")
        testSort = bundle!! .getString("testSort")

        Log.d(TAG, "onViewCreated: $testScope / $testCategory /$testSort")

        model = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(activity!!.application, wordBookIdForTest) as T
            }}).get(WordViewModel::class.java)

        if (test == 0) { // 처음 테스트일 때
            Log.d(TAG, "onCreate: $testScope / $testCategory / $testSort / $wordBookIdForTest")
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
            wordList = if(testScope!! == "2") { // 모든 단어
                when {
                    testSort!! == "id" -> { model?.getTestAllWord(wordBookIdForTest) }
                    testSort!! == "word" -> { model?.getTestAllWordWordAscOrder(wordBookIdForTest) }
                    else -> { model?.getTestAllWordRandomOrder(wordBookIdForTest) }
                }
            } else {    // 북마크 안/한 단어
                when {
                    testSort!! == "id" -> { model?.getTestBookmarkAllWordRecentOrder(wordBookIdForTest, testScope!!) }
                    testSort!! == "word" -> { model?.getTestBookmarkAllWordWordAscOrder(wordBookIdForTest, testScope!!) }
                    else -> { model?.getTestBookmarkAllWordRandomOrder(wordBookIdForTest, testScope!!) }
                }
            }
        } else if (test == 1) { // 한 번 더 테스트일때
            wordList = bundle.getParcelableArrayList<Word>("wordList")
        }


// FIXME: 2021-03-08 뒤로가기 버튼 삽입하기
        /*---- Tool Bar ----*/
        /*     setSupportActionBar(toolbarBinding.toolbar)
             supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
             supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
             supportActionBar?.setDisplayHomeAsUpEnabled(true)
             supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
             toolbarBinding.toolbarTitle.gravity = Gravity.LEFT
             toolbarBinding.toolbarTitle.text = "testest"*/






        /** 뷰 초기화*/
        val allCount = wordList!!.size.toString()

        if (wordList!!.size != 0) {
            setTestCount(currentCount, allCount)
            setTestCategory(index)
            btn_previous.isEnabled = false
            if(wordList!![index].bookMarkCheck == 0){
                binding.btnFavorite.setImageResource(R.drawable.favorite_normal_background)
            } else
                binding.btnFavorite.setImageResource(R.drawable.favorite_pressed_background)
        } else {
            binding.notMatchedTest.visibility = View.VISIBLE
            binding.notMatchedTest.text = "설정된 단어가 없습니다.\n북마크를 추가/해제 해주세요."
            count_test.visibility = View.INVISIBLE
            btn_previous.isEnabled = false
            btn_false.isEnabled = false
            btn_true.isEnabled = false
            btn_favorite.isEnabled = false
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
                            /*val mBuilder = AlertDialog.Builder(activity!!.application)
                                .setMessage("진행중인 테스트를 종료하시겠습니까?")
                                .setNegativeButton("취소", null)
                                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                    cancelTest()
                                })
                            val mDialog = mBuilder.create()
                            mDialog.show()*/
                    }
                    R.id.btn_previous -> {
                        Log.d(TAG, "onViewCreated: 이외 들어옴")
                        currentCount -= 1
                        btn_previous.isEnabled = currentCount != 1
                        index -= 1
                        setTestCount(currentCount, allCount)
                        setTestCategory(index)
                        setBookmarked(index)
                        setAnswerInvisible()
                    }
                    R.id.btn_false -> {
                        Log.d(TAG, "onViewCreated: index $index")
                        Log.d(TAG, "onViewCreated: allCount.toInt() ${allCount.toInt()}")
                        Log.d(TAG, "onViewCreated: currentCount $currentCount")
                        wordList!![index].testCheck = 0
                        if (index == (allCount.toInt() - 1)) {
                            bundle.putLong("wordBookIdForTest", wordBookIdForTest)
                            bundle.putInt("test", test) // 받아온 test 값을 넣어줌 처음, 한번 더 한건지 판별
                            bundle.putString("testScope", testScope)
                            bundle.putString("testCategory", testCategory)
                            bundle.putString("testSort", testSort)
                            bundle.putParcelableArrayList("wordList", wordList)
                            testResultFragment.arguments = bundle
                            ft.replace(R.id.test_frameLayout, testResultFragment).commit()
                            // TODO: 2021-02-27 결과화면 프래그먼트, 라이브데이터 업뎃
                        } else {
                            btn_previous.isEnabled = true
                            currentCount += 1
                            index += 1
                            setTestCount(currentCount, allCount)
                            setTestCategory(index)
                            setBookmarked(index)
                            setAnswerInvisible()
                        }
                    }
                    R.id.btn_true -> {
                        Log.d(TAG, "onViewCreated: index $index")
                        Log.d(TAG, "onViewCreated: allCount.toInt() ${allCount.toInt()}")
                        Log.d(TAG, "onViewCreated: currentCount $currentCount")
                        wordList!![index].testCheck = 1
                        if (index == (allCount.toInt() - 1)) {
                            bundle.putLong("wordBookIdForTest", wordBookIdForTest)
                            bundle.putInt("test", test)
                            bundle.putString("testScope", testScope)
                            bundle.putString("testCategory", testCategory)
                            bundle.putString("testSort", testSort)
                            bundle.putParcelableArrayList("wordList", wordList)
                            testResultFragment.arguments = bundle
                            ft.replace(R.id.test_frameLayout, testResultFragment).commit()
                            // TODO: 2021-02-27 결과화면 프래그먼트, 라이브데이터 업뎃
                        } else {
                            btn_previous.isEnabled = true
                            currentCount += 1
                            index += 1
                            setTestCount(currentCount, allCount)
                            setTestCategory(index)
                            setBookmarked(index)
                            setAnswerInvisible()
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
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
        test_back_btn.setOnClickListener(btnListener)
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
    }

    interface OnCancelTest {
        fun onCancelTest()
    }
    fun cancelTest() {
        onCancelTest.onCancelTest()
    }


}

