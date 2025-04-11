package com.henry.core.widget.dialog

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.henry.core.databinding.DialowShowJokeFragmentBinding

class DialogShowJoke(
    var content: String,
) : DialogFragment() {

    private lateinit var binding: DialowShowJokeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialowShowJokeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContent()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun setupContent() = with(binding) {
        contentText.text = content
        btnOk.setOnClickListener {
            dismiss()
        }
    }
}