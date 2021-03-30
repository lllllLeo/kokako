package com.yj.addwords

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
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
import com.yj.addwords.databinding.ImportDialogBinding
import com.yj.addwords.model.Word
import com.yj.addwords.model.WordBook
import com.yj.addwords.viewModel.WordBookViewModel
import com.yj.addwords.viewModel.WordViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.opencsv.CSVReaderBuilder
import com.opencsv.enums.CSVReaderNullFieldIndicator
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*


class ImportDialog : DialogFragment() {
    private var adView : AdView? = null
    private var                     _binding : ImportDialogBinding? = null
    private val                     binding get() = _binding!!
    private var                     folderName = "Addwords"// 어플이름 폴더이름
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
//            binding.toolbarTitle.gravity = Gravity.CENTER
//            binding.toolbarDialog.setNavigationOnClickListener { v -> dismiss() }
//            binding.toolbarDialog.setOnMenuItemClickListener { item ->
//                dismiss()
//                true
//            }
        }

        MobileAds.initialize(requireActivity().application, getString(R.string.admob_app_id))
        adView = binding.adView
        val adRequest : AdRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)


        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(activity!!.application, -1) as T
            }
        }).get(WordViewModel::class.java)
        wordBookModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(
            requireActivity().application)).get(WordBookViewModel::class.java)

        /*val span2 : Spannable = binding.tv1.text as Spannable
        span2.setSpan(StyleSpan(Typeface.BOLD), 9,15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val span : Spannable = binding.tv2.text as Spannable
        span.setSpan(StyleSpan(Typeface.BOLD), 11,13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), 16,18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), 20,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(StyleSpan(Typeface.BOLD), 25,26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)*/


        binding.importCloseBtn.setOnClickListener { dismiss() }
        binding.importFile.setOnClickListener{
            setupPermissions()
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(context as MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), GET_FILE_CODE)
        } else {
            ActivityCompat.requestPermissions(context as MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101)
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

//                        Snackbar.make(binding.importFramelayout, "단어장 추가 완료", Snackbar.LENGTH_LONG).show()
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
            val folderAndName = "$folderName/$fileNameFilenameExtension"
            val reader =
                CSVReaderBuilder(FileReader(getExternalPath(folderAndName))).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build()
            if (reader != null) {
                var cell : Array<String>? = null
                excelWordBookId = addWordBook(fileName)

                cell = reader.readNext()
                while (cell != null) {
                    val word = Word(0, "default word", "default mean", "",0, 9999, excelWordBookId!!)
                    Log.d(TAG, "readCsvFile: ${cell[0]}  ${cell[1]}")
                    word.word = cell[0]
                    word.mean = cell[1]
                    importWordList.add(word)
                    cell = reader.readNext()
                }

                /*while (reader.readNext().also { cell = it } != null) {
                    val word = Word(0, "default word", "default mean", "",0, 9999, excelWordBookId!!)
                    Log.d(TAG, "readCsvFile: ${cell!![0]}  ${cell!![1]}")
                    word.word = cell!![0]
                    word.mean = cell!![1]
                    importWordList.add(word)
                }*/
                /*for (cell in reader.iterator()) {
                    if (cell[0].isNotEmpty() && cell[1].isNotEmpty()) {
                        val word = Word(0, "default word", "default mean", "",0, 9999, excelWordBookId!!)
                        Log.d(TAG, "readCsvFile: ${cell[0]}  ${cell[1]}")
                        word.word = cell[0]
                        word.mean = cell[1]
                        importWordList.add(word)
                    }
                }*/
                wordModel?.insertAllDatas(importWordList)
                passData(excelWordBookId!!)
                reader.close()
                Toast.makeText(context, "단어장 가져오기를 완료하였습니다.\n가져온 단어장의 언어 설정을 변경해주세요.", Toast.LENGTH_SHORT).show()
//                updateWordBookCount(excelWordBookId!!)
//                Toast.makeText(this, "$fileName 가져오기 완료", Toast.LENGTH_SHORT).show()
            }
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "단어장 가져오기에 실패하였습니다.", Toast.LENGTH_SHORT).show()
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
                requireActivity().application?.contentResolver?.query(uri,
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
            "${requireContext().filesDir}/${folderName}"
        }
        return sdPath
    }
}