package com.example.kokako

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.kokako.databinding.ActivityTestBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel

class TestActivity : AppCompatActivity(), TestResultFragment.OnFinishTestDataPass, TestFragment.OnCancelTest {
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
        const val COMPLETE_CODE = 10
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        toolbarBinding = binding.includeToolbar


        wordBookIdForTest = intent.getLongExtra("wordBookIdForTest", 0)
        testScope = intent.getStringExtra("testScope")
        testCategory = intent.getStringExtra("testCategory")
        testSort = intent.getStringExtra("testSort")


        val testFragment = TestFragment()
        val bundle = Bundle()
        bundle.putLong("wordBookIdForTest", wordBookIdForTest)
        bundle.putInt("test", 0)
        bundle.putString("testScope", testScope)
        bundle.putString("testCategory", testCategory)
        bundle.putString("testSort", testSort)
        testFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.test_frameLayout, testFragment).commit()

    }

    // FIXME: 2021-03-11 데이터가 필요없네
//    테스트 종료 버튼 눌렸을때
    override fun onFinishTestDataPass(wordBookIdForTest: Long, wordList: java.util.ArrayList<Word>) {
        Log.d(TAG, "onFinishTestDataPass: 여기")
        val intent = Intent()
//        intent.putExtra("wordBookIdForTest", wordBookIdForTest)
//        intent.putParcelableArrayListExtra("wordList", wordList)
        setResult(COMPLETE_CODE, intent)
        finish()
    }

//    툴바 뒤로가기버튼 클릭시
    override fun onCancelTest() {
        val mBuilder = AlertDialog.Builder(this)
            .setMessage("진행중인 테스트를 종료하시겠습니까?")
            .setNegativeButton("취소", null)
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                setResult(COMPLETE_CODE, intent)
                finish()
            })
        val mDialog = mBuilder.create()
        mDialog.show()

    /*val intent = Intent()
        setResult(COMPLETE_CODE, intent)
        finish()*/
    }
//    안드로이드 뒤로가기버튼 눌렸을때
    override fun onBackPressed() {
        onCancelTest()
    }
}