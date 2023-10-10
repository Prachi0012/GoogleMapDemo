package com.example.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo.databinding.ActivityAdsDemoBinding


class AdsDemo : AppCompatActivity() {
//    lateinit var mAdView : AdView

    lateinit var binding: ActivityAdsDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdsDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initview()
    }

    private fun initview() {
//        MobileAds.initialize(this) {}
//
//        mAdView = findViewById(R.id.adView)
//        val adRequest = AdRequest.Builder().build()
//        mAdView.loadAd(adRequest)
    }
}
