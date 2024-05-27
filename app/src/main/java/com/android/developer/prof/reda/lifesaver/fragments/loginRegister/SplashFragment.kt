package com.android.developer.prof.reda.lifesaver.fragments.loginRegister

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.android.developer.prof.reda.lifesaver.R
import com.android.developer.prof.reda.lifesaver.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_splash,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//         load the animation
        val animationUpDown = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.up_down_anim
        )

        val animationStartEnd = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.start_end_anim
        )

//         start the animation
        binding.emergencyImgView.startAnimation(animationUpDown)
        binding.appName.startAnimation(animationStartEnd)

        Handler().postDelayed({

            findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
        }, 5000)
    }
}