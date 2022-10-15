package com.m_and_a_company.canelatube.ui.modals

import android.app.Dialog
import android.content.Context
import com.airbnb.lottie.LottieDrawable
import com.m_and_a_company.canelatube.databinding.ModalAnimationLayoutBinding

class ModalAnimation(context: Context): Dialog(context) {

    private val mBinding = ModalAnimationLayoutBinding.inflate(layoutInflater)

    init {
        setContentView(mBinding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun setTitleAndDesc(title: String, desc: String) {
        mBinding.modelAnimationTitle.text = title
        mBinding.modalAnimationDesc.text = desc
    }

    fun setAnimation(animation: Int) {
        mBinding.modalAnimationView.apply {
            setAnimation(animation)
            repeatMode = LottieDrawable.RESTART
            repeatCount = 5
            playAnimation()
        }
    }

    fun setCallback(callbackAccept: () -> Unit, callbackCancel: () -> Unit) {
        mBinding.modalAnimationAccept.setOnClickListener {
            callbackAccept()
            dismiss()
        }

        mBinding.modalAnimationCancel.setOnClickListener {
            callbackCancel()
            dismiss()
        }
    }




}