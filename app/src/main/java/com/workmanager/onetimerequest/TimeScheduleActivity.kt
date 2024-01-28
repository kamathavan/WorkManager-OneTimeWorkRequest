package com.workmanager.onetimerequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.workmanager.onetimerequest.databinding.ActivityTimeScheduleBinding

class TimeScheduleActivity : AppCompatActivity() {

    lateinit var binding: ActivityTimeScheduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}