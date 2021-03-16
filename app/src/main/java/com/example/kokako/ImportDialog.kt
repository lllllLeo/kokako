package com.example.kokako

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kokako.databinding.ImportDialogBinding
import com.example.kokako.model.Word
import com.example.kokako.model.WordBook
import com.example.kokako.viewModel.WordBookViewModel
import com.example.kokako.viewModel.WordViewModel
import com.opencsv.CSVReaderBuilder
import com.opencsv.enums.CSVReaderNullFieldIndicator
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*


class ImportDialog : DialogFragment() {
    private var                     _binding : ImportDialogBinding? = null
    private val                     binding get() = _binding!!
    private var                     folderName = "1212121212121212"// 어플이름 폴더이름
    private var                     excelWordBookId: Long? = null
    private var wordBookModel: WordBookViewModel? = null
    private var wordModel: WordViewModel? = null
    private var language = 0
    lateinit var dataPasser: OnDataPass
    companion object {
        const val TAG = "TAG ExportDialog"
        const val GET_FILE_CODE = 102
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ImportDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.toolbarTitle.text = "가져오기"
            binding.toolbarTitle.gravity = Gravity.LEFT
            binding.toolbarDialog.setNavigationOnClickListener { v -> dismiss() }
            binding.toolbarDialog.setOnMenuItemClickListener { item ->
                dismiss()
                true
            }
        }

        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(activity!!.application, -1) as T
            }
        }).get(WordViewModel::class.java)
        wordBookModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
            activity!!.application)).get(WordBookViewModel::class.java)

//        아래 예시화면처럼 A열에는 단어, B열에는 뜻을 입력해주세요.
        val span : Spannable = binding.tv3.text as Spannable
        span.setSpan(StyleSpan(Typeface.BOLD), 10,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), 15,17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), 19,21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), 24,25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.importFile.setOnClickListener{
            setupPermissions()
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(context as MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), GET_FILE_CODE)
        } else {
            ActivityCompat.requestPermissions(context as MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $requestCode /  $resultCode")
        when (requestCode) {
            GET_FILE_CODE -> { // 102
                try {
                    if (data != null) {
                        readCsvFile(data)
                        dismiss()
                        Toast.makeText(context, "단어장 가져오기를 완료하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun readCsvFile(data: Intent?) {
        try {
            val importWordList = ArrayList<Word>()
            val uri: Uri = data!!.data!!
            Log.d(TAG, "readCsvFile: uri ${uri.path}")
            val fileName = getFileName(uri)!!.split(".")[0]
            val fileNameFilenameExtension = getFileName(uri)!!.split(".")[0] + ".csv"
            Log.d(TAG, "readCsvFile: fileName $fileName")
            // TODO: 2021-03-11 단어장 이름 어플 정해서 폴더가 없으면 이걸로 만들어 놓기. 그리고 여기서만 내보내기 ㄱㄴ,
            // TODO: 2021-03-12 도움말에 가져오기 할 때 ~~폴더를 만들고 구글스프레드시트에서 만든 .csv 파일을 넣어주세요
            val folderAndName = "$folderName/$fileNameFilenameExtension"
            val reader =
                CSVReaderBuilder(FileReader(getExternalPath(folderAndName))).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build()
            if (reader != null) {
                excelWordBookId = addWordBook(fileName)
                for (cell in reader.iterator()) {
                    if (cell[0].isNotEmpty() && cell[1].isNotEmpty()) {
                        val word = Word(0, "default word", "default mean", "",0, 9999, excelWordBookId!!)
                        word.word = cell[0]
                        word.mean = cell[1]
                        importWordList.add(word)
                    }
                }
                wordModel?.insertAllDatas(importWordList)
                passData(excelWordBookId!!)
//                updateWordBookCount(excelWordBookId!!)
//                Toast.makeText(this, "$fileName 가져오기 완료", Toast.LENGTH_SHORT).show()
            }
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "단어장 가져오기에 실패하였습니다..", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    interface OnDataPass {
        fun onDataPass(data: Long)
    }

    // TODO: 2021-03-16 private되누
    private fun passData(data: Long){
        dataPasser.onDataPass(data)
    }

    private fun updateWordBookCount(wordBookIdForComplete: Long) {
        wordBookModel?.updateWordBookCount(wordBookIdForComplete)
    }

    private fun addWordBook(wordBookName: String): Long {
        val wordBook = WordBook(0, wordBookName, 0, 0, (wordBookModel?.getMaxOrder()!! + 1), language)
        val recentInsertedWordBookId: Long = wordBookModel?.insert(wordBook)!!
        Log.d("     TAG",
            "===== MainActivity - addWordBook - recentInsertedWordBookId 값은 : $recentInsertedWordBookId")
        return recentInsertedWordBookId
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                activity!!.application?.contentResolver?.query(uri,
                    null,
                    null,
                    null,
                    null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun getExternalPath(folderName: String): String? {
        var sdPath = ""
        val ext = Environment.getExternalStorageState()
        sdPath = if (ext == Environment.MEDIA_MOUNTED) {
            Environment.getExternalStorageDirectory().absolutePath + "/" + folderName
        } else {
            "${context!!.filesDir}/${folderName}"
        }
        return sdPath
    }
}