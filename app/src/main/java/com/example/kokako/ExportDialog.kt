package com.example.kokako

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kokako.databinding.ExportDialogBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ExportDialog : DialogFragment() {
    private var                     _binding : ExportDialogBinding? = null
    private val                     binding get() = _binding!!
    private var                     toolbar: Toolbar? = null
    private var                     folderName = "1212121212121212"// 어플이름 폴더이름
    private var                     wordModel: WordViewModel? = null
    private var                     wordBookIdForView: Long? = null
    private var                     wordBookNameForView: String? = null
    private var                     wordList: ArrayList<Word>? = null
    private var                     exportCSVName : String? = null
    companion object {
        const val TAG = "TAG ExportDialog"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        val bundle : Bundle? = arguments
        wordBookIdForView = bundle!!.getLong("wordBookIdForView")
        wordBookNameForView = bundle.getString("wordBookNameForView")
        Log.d(TAG, "onCreate: $wordBookIdForView / $wordBookNameForView")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ExportDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.toolbarTitle.text = "내보내기"
            binding.toolbarTitle.gravity = Gravity.LEFT
            binding.toolbarDialog.setNavigationOnClickListener { v -> dismiss() }
            binding.toolbarDialog.setOnMenuItemClickListener { item ->
                dismiss()
                true
            }
        }
        wordModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(activity!!.application, wordBookIdForView!!) as T
            }
        }).get(WordViewModel::class.java)
        wordList = wordModel?.getRecentOrder(wordBookIdForView!!)




        val btnListener = View.OnClickListener {
            when(it.id) {
                R.id.export_file_to_storage -> {
                    if (Build.VERSION.SDK_INT < 30) {
                        setupPermissions()
                    } else {
                        Toast.makeText(context,
                            "현재 안드로이드 버전에서는 지원하지 않습니다. 파일로 공유하기를 이용해주세요.",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                R.id.export_file_to_share -> {
                    shareCsvFile()
//                    ssaasdf()
//                    writeTempCsvFile("$wordBookNameForView.csv")
                }
                R.id.export_text -> {
                    exportText()
                }
            }
        }


        binding.exportFileToStorage.setOnClickListener(btnListener)
        binding.exportFileToShare.setOnClickListener(btnListener)
        binding.exportText.setOnClickListener(btnListener)

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(context as ViewWordActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            writeCsvFile("$wordBookNameForView.csv", 1)
        } else {
            ActivityCompat.requestPermissions(context as ViewWordActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101)
            Toast.makeText(context as ViewWordActivity, "파일 접근 권한이 없습니다. 권한을 허용해주세요 ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportText(){
        val toText = ArrayList<Array<String>>()
        for (i in 0 until wordList!!.size) {
            val item : Array<String> = arrayOf<String>(wordList!![i].word.toString(),
                wordList!![i].mean.toString(),
                wordList!![i].option.toString())
            toText.add(item)
        }
        val clipboard = activity!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("복사한 단어", toText.toString())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "클립보드에 단어를 복사하였습니다.", Toast.LENGTH_SHORT).show()

    }

    private fun ssaasdf() {
        val extRoot: File? = context!!.getExternalFilesDir(null)
        val someFile = "/test/some.csv"

        val xlsFile = File(extRoot, someFile)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/*" // 엑셀파일 공유 시
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        val contentUri: Uri = FileProvider.getUriForFile(
            context!!,
            context!!.applicationContext.packageName + ".fileprovider",
            xlsFile)
        Log.d(TAG, "ssaasdf: $contentUri")
//        content://com.example.kokako
//        .fileprovider
//        /files/Android/data/com.example.kokako/files/test/some.xls
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(shareIntent, "엑셀 공유"))
//        startActivityForResult(Intent.createChooser(shareIntent, "엑셀 공유"), 100)
    }

    /*@RequiresApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("SimpleDateFormat")
    private fun writeTempCsvFile(filePath: String) : String {
        val time = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = Date(time)
        val strTime = simpleDateFormat.format(date)
//        val folderPath = getExternalPath(folderName)
        val extRoot: File? = context!!.getExternalFilesDir(null)
        Log.d(TAG, "writeTempCsvFile: extRoot $extRoot")
//        /storage/emulated/0/Android/data/com.example.kokako/files
        val someFile = "/temp/$filePath"
        Log.d(TAG, "writeTempCsvFile: someFile $someFile")
//        /temp/구글 시트 한자 2136.csv
        val csvFile = File(extRoot, someFile)
        Log.d(TAG, "writeTempCsvFile: csvFile $csvFile")
//        /storage/emulated/0/Android/data/com.example.kokako/files/temp/구글 시트 한자 2136.csv
//        /files/Android/data/com.example.kokako/files/temp/~~~~~~.csv
        val folderPath = context!!.applicationContext.packageName + ".fileprovider"
        Log.d(TAG, "writeTempCsvFile: folderPath $folderPath")
//        com.example.kokako.fileprovider

        val contentUri: Uri = FileProvider.getUriForFile(context!!,
            context!!.applicationContext.packageName + ".fileprovider",
            csvFile)

        val csv = ArrayList<Array<String>>()
        for (i in 0 until wordList!!.size) {
            val item : Array<String> = arrayOf<String>(wordList!![i].word.toString(),
                wordList!![i].mean.toString(),
                wordList!![i].option.toString())
            csv.add(item)
        }
//        /내보내기_${strTime}_
        val writer = CSVWriter(FileWriter("$folderPath$csvFile"))

//   com.example.kokako.fileprovider/storage/emulated/0/Android/data/com.example.kokako/files/temp/구글 시트 한자 2136.csv


        val exportCSVName = "$folderPath$csvFile"
        Log.d(TAG, "writeCsvFile: $folderPath$csvFile")
//        /storage/emulated/0/1212121212121212
//        /내보내기_
//        2021-03-12 21:11
//        _구글 시트 한자 2136.csv
        writer.writeAll(csv)
        writer.close()
        Log.d(TAG, "writeTempCsvFile: exportCSVName $exportCSVName")
        Log.d(TAG, "writeCsvFile: 완료")
        return exportCSVName
    }*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            try {
                val file = File(exportCSVName!!)
                if(file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
        }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun shareCsvFile() {
        exportCSVName = writeCsvFile("$wordBookNameForView.csv", 0)
//        /storage/emulated/0/Android/data/com.example.kokako/files/Download/내보내기_2021-03-12 22:23_단어장.csv
        Log.d(TAG, "onExportPopupClicked: $exportCSVName")
//            val outputFile = File(exportCSVName)
//            val uri: Uri = Uri.fromFile(outputFile)

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "application/*" // text였는데 application으로 바꾸니까 공유하기에 파일 이미지가 보임 ㅅㅂ 시간 개낭비
        sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//            val strpa = applicationContext.packageName
//            applicationContext.packageName
        val csvUri = FileProvider.getUriForFile(context as ViewWordActivity,
            BuildConfig.APPLICATION_ID + ".fileprovider", File(exportCSVName));

//                                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(exportCSVName))
//        sharingIntent.putExtra(Intent.EXTRA_EMAIL, "")
        sharingIntent.putExtra(Intent.EXTRA_STREAM, csvUri)
//            sharingIntent.setPackage("com.kakao.talk")
//            sharingIntent.setPackage("com.google.android.gm")
//            sharingIntent.setPackage("org.telegram.messenger")
        val sharing = Intent.createChooser(sharingIntent, "공유하기")

//        startActivity(sharing)
        startActivityForResult(Intent.createChooser(sharing, "엑셀 공유"), 100)
        Log.d(TAG, "shareCsvFile: 공유하기 out")
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("SimpleDateFormat")
    private fun writeCsvFile(filePath: String, check: Int) : String {
        val time = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = Date(time)
        val strTime = simpleDateFormat.format(date)
        val folderPath = getExternalPath(folderName, check)
        val csv = ArrayList<Array<String>>()
        for (i in 0 until wordList!!.size) {
            val item : Array<String> = arrayOf<String>(wordList!![i].word.toString(),
                wordList!![i].mean.toString(),
                wordList!![i].option.toString())
            csv.add(item)
        }
        val writer = CSVWriter(FileWriter("$folderPath/내보내기_${strTime}_$filePath"))
        val exportCSVName = "$folderPath/내보내기_${strTime}_$filePath"
        Log.d(TAG, "writeCsvFile: $folderPath/내보내기_${strTime}_$filePath")
        writer.writeAll(csv)
        writer.close()
//        if (Build.VERSION.SDK_INT < 30) {
        if (check == 1) {   // 저장소 저장일 때 (29 이상은 이전에 걸러짐)
            Toast.makeText(context as ViewWordActivity, "$folderPath 폴더 경로로 내보내기 완료", Toast.LENGTH_SHORT).show()
        }
        return exportCSVName
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getExternalPath(folderName: String, check: Int): String? {
        var sdPath = ""
        val ext = Environment.getExternalStorageState()
        sdPath = if (ext == Environment.MEDIA_MOUNTED) {
            if (check == 1) {   // 저장소 저장일 떄
                Environment.getExternalStorageDirectory().absolutePath + "/" + folderName
            } else {    // 공유하기 위해 임시로 내부저장소에 저장할 때
                Log.d(TAG, "getExternalPath: sdk30이상")
                Log.d(TAG,
                    "getExternalPath: ${context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path.toString()}")
                context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path.toString() // data/~~
//                /storage/emulated/0/Android/data/com.example.kokako/files/Download/내보내기_2021-03-12 22:23_단어장.csv
            }
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"$folderName").toString()
        } else {
            Log.d(TAG, "getExternalPath: else문")
            "${context!!.filesDir}/$folderName"
        }
        Log.d(TAG, "getExternalPath: 1 ${context!!.getExternalFilesDir(null)?.path.toString()}")
                                    //        1 /storage/emulated/0/Android/data/com.example.kokako/files
        Log.d(TAG, "getExternalPath: 2 ${Environment.getExternalStorageDirectory().absolutePath + "/" + folderName}")
                                    //        2 /storage/emulated/0/1212121212121212
        Log.d(TAG, "getExternalPath: 3 ${context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path.toString()}")
                                    //        3 /storage/emulated/0/Android/data/com.example.kokako/files/Download
        Log.d(TAG, "getExternalPath: 4 ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}")
                                    //        4 /storage/emulated/0/Download
        Log.d(TAG, "getExternalPath: 5 ${context!!.getExternalFilesDir("temp")?.path.toString()}")
                                    //        5 /storage/emulated/0/Android/data/com.example.kokako/files/temp
        Log.d(TAG, "getExternalPath: 6 ${context!!.getExternalFilesDir("temp")?.toString()}")
                                    //        6 /storage/emulated/0/Android/data/com.example.kokako/files/temp
//        Log.d(TAG, "getExternalPath: sdPath $sdPath")
        return sdPath
    }
}