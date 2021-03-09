package com.example.kokako

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kokako.databinding.FragmentTestBinding
import com.example.kokako.databinding.FragmentTestResultBinding

class TestResultFragment : Fragment() {
    private var _binding : FragmentTestResultBinding? = null
    private val binding get() = _binding!!
    companion object {
        const val TAG = "TAG TestResultFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}