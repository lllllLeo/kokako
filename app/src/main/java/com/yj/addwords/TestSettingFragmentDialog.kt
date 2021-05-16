package com.yj.addwords

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.yj.addwords.databinding.TestSettingDialogBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.chip.Chip


class TestSettingFragmentDialog : DialogFragment() {
    private var adView : AdView? = null
    private var _binding : TestSettingDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var dataPasser: OnDataPass
    companion object {
        const val TAG = "TAG TestSettingDialog"
        fun display(fragmentManager: FragmentManager?): TestSettingFragmentDialog? {
            val testSettingDialog = TestSettingFragmentDialog()
            testSettingDialog.show(fragmentManager!!, TAG)
            return testSettingDialog
        }
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
        _binding = TestSettingDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            binding.toolbarTitle.text = "테스트 설정"
            binding.toolbarTitle.gravity = Gravity.LEFT
//            binding.toolbarTitle.gravity = Gravity.CENTER

            /*binding.toolbarDialog.setNavigationOnClickListener { v -> dismiss() }
            binding.toolbarDialog.setOnMenuItemClickListener { item ->
                dismiss()
                true
            }*/
        }

        MobileAds.initialize(requireContext(), getString(R.string.admob_app_id))
        adView = binding.adView
        val adRequest : AdRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)




        binding.testSettingCloseBtn.setOnClickListener { dismiss() }
        binding.testWord.setOnClickListener{
            val chipList = ArrayList<String>()
//            var questionScope : Int = 0
//            var questionCategory : Int = 0
//            var questionSort : Int = 0
            val filter1: List<Int> = binding.chipgroup.checkedChipIds
            for (id in filter1) {
                val chip: Chip = binding.chipgroup.findViewById(id)
                if (chip.isChecked){
                    chipList.add(chip.text as String)
                }
            }
            val filter2: List<Int> = binding.chipgroup2.checkedChipIds
            for (id in filter2) {
                val chip: Chip = binding.chipgroup2.findViewById(id)
                if (chip.isChecked){
                    chipList.add(chip.text as String)
                }
            }
            val filter3: List<Int> = binding.chipgroup3.checkedChipIds
            for (id in filter3) {
                val chip: Chip = binding.chipgroup3.findViewById(id)
                if (chip.isChecked){
                    chipList.add(chip.text as String)
                }
            }
            passData(chipList)
            dismiss()
        }
    }

    interface OnDataPass {
        fun onDataPass(data: ArrayList<String>)
    }

    fun passData(data: ArrayList<String>){
        dataPasser.onDataPass(data)
    }

}