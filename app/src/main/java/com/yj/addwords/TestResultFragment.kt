package com.yj.addwords

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.yj.addwords.databinding.FragmentTestResultBinding
import com.yj.addwords.model.Word
import kotlinx.android.synthetic.main.fragment_test_result.*
import java.util.ArrayList

// TODO: 2021-03-10 테스트종료 누르면 외움/못외움 값 9999로 초기화
class TestResultFragment : Fragment() {
    private var _binding : FragmentTestResultBinding? = null
    private val binding get() = _binding!!
    private var notMemorized : Int = 0
    private var memorized : Int = 0
    private var index = 0
    lateinit var dataPasser: OnFinishTestDataPass
    companion object {
        const val TAG = "TAG TestResultFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dataPasser = context as OnFinishTestDataPass
        val bundle : Bundle? = arguments
        val wordBookIdForTest = bundle!!.getLong("wordBookIdForTest")
        val testScope = bundle.getString("testScope")
        val testCategory = bundle.getString("testCategory")
        val testSort = bundle.getString("testSort")
        val wordList = bundle.getParcelableArrayList<Word>("wordList")

        Log.d(TAG, "onViewCreated: $testScope / $testCategory /$testSort")
        for (i in wordList!!) {
            if (wordList[index].testCheck == 0) {
                Log.d(TAG, "onViewCreated: 못외움 $i")
                notMemorized += 1
            } else if(wordList[index].testCheck == 1) {
                Log.d(TAG, "onViewCreated: 외움 $i")
                memorized += 1
            }
            index += 1
        }
// TODO: 2021-03-11 java.util.ConcurrentModificationException  값을 삭제시 List의 size가 줄어들어 기존 index와 맞지 않아 오류가 발생하는 것입니다.
// TODO: 2021-03-11 자바의 Iterator 인터페이스는 remove 메소드를 제공하고 있는데요. 자바 공식 문서를 보면 이 메소드를 사용하는 것이 컬렉션을 순회하면서 원소를 삭제할 수 있는 유일하게 안전한 방법이라고 가이드하고 있습니다



        Log.d(TAG, "onViewCreated: 1 $wordBookIdForTest")
        Log.d(TAG, "onViewCreated: 2 $wordList")

        /** 뷰 초기화 */
        binding.allWordCountTest.text = "총 : ${wordList!!.size.toString()}"
        binding.memorizedCountTest.text = "외운 단어 : ${memorized.toString()}"
        binding.notMemorizedCountTest.text = "못외운 단어 : ${notMemorized.toString()}"
        when ((memorized * 100) / wordList.size) {
            in 0..19 -> { binding.resultEmoji.setImageResource(R.drawable.ic_score_0_to_19) }
            in 20..39 -> { binding.resultEmoji.setImageResource(R.drawable.ic_score_20_to_39) }
            in 40..59 -> { binding.resultEmoji.setImageResource(R.drawable.ic_score_40_to_59) }
            in 60..79 -> { binding.resultEmoji.setImageResource(R.drawable.ic_score_60_to_79) }
            in 80..100 -> { binding.resultEmoji.setImageResource(R.drawable.ic_score_80_to_100) }
        }
        if (notMemorized == 0) {
            binding.oneMoreTest.isEnabled = false
            binding.oneMoreTest.setBackgroundColor(ContextCompat.getColor(activity!!.application,R.color.colorGray))
        } else {
            binding.oneMoreTest.setBackgroundColor(ContextCompat.getColor(activity!!.application,R.color.colorBlue))
        }




        val btnListener = View.OnClickListener { view ->

            try {
                when(view.id) {
                    R.id.one_more_test -> {
                        Log.d(TAG, "onViewCreated: 다시 한번 테스트")
                        val testFragment = TestFragment()
                        val bundle = Bundle()
                        val ft = (activity as TestActivity).supportFragmentManager.beginTransaction()
                        val iter : MutableIterator<Word> = wordList.iterator()
                        Log.d(TAG, "onViewCreated:  wordList  $wordList")
                        while (iter.hasNext()) {
                            val testCheckValue = iter.next().testCheck
                            if (testCheckValue == 1) {
                                iter.remove()
                            }
                        // TODO: 2021-03-11 지금은 계속 지우고 하지만 테스트 종료, 강제종료할 때는 북마크만 업뎃해야하나? 못외운단어들만 워드리스트에 잇을테니 아 업데이트만하면될거같다
                        }
                        bundle.putLong("wordBookIdForTest", wordBookIdForTest)
                        bundle.putInt("test", 1)
                        bundle.putString("testScope", testScope)
                        bundle.putString("testCategory", testCategory)
                        bundle.putString("testSort", testSort)
                        bundle.putParcelableArrayList("wordList", wordList)
                        testFragment.arguments = bundle
                        ft.replace(R.id.test_frameLayout, testFragment).commit()
                    }
                    R.id.finish_test -> {
                        finishTestPassData(wordBookIdForTest, wordList)
                    }
                    R.id.test_back_btn -> {
                        finishTestPassData(wordBookIdForTest, wordList)
                    }
                }
            } catch (e : NullPointerException) {
                e.printStackTrace()
            }
        }
        one_more_test.setOnClickListener(btnListener)
        finish_test.setOnClickListener(btnListener)
        test_back_btn.setOnClickListener(btnListener)
    }

    // FIXME: 2021-03-11 데이터가 필요없네
    interface OnFinishTestDataPass {
        fun onFinishTestDataPass(wordBookIdForTest: Long, wordList: ArrayList<Word>)
    }
    fun finishTestPassData(wordBookIdForTest: Long, wordList: ArrayList<Word>) {
        dataPasser.onFinishTestDataPass(wordBookIdForTest, wordList)
    }

}
