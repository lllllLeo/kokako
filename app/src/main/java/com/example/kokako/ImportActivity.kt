package com.example.kokako

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kokako.databinding.ActivityImportBinding

class ImportActivity : AppCompatActivity() {
    private var _binding : ActivityImportBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityImportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_import)

        var imText : String = "hi,안녕\nbye,잘가\nbeer,맥주"
        var importText = binding.importText.toString()

        binding.importPreview.setOnClickListener{
            var textSeparator : String = binding.textSeparator.text.toString()
            var previewText = imText.split(textSeparator,"\n")
//            Toast.makeText(context,""+previewText[0]+"|"+previewText[1], Toast.LENGTH_SHORT).show()

//       미리보기를 좀 다르게 어떻게 할까나 / 실시간, 똑같은 뷰,
/*            val mBuilder = AlertDialog.Builder(it.context)
            mBuilder.setMessage(previewText[0])*/
        }
    }
}