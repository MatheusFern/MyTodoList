package com.example.mytodolist.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mytodolist.databinding.ActivityAddTaskBinding
import com.example.mytodolist.datasource.TaskDataSource
import com.example.mytodolist.extentions.format
import com.example.mytodolist.extentions.text
import com.example.mytodolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findByid(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilTime.text = it.time
            }
        }

        insertListners()
    }

    private fun insertListners() {
       //evento de data
       binding.tilDate.editText?.setOnClickListener{
       val datePicker = MaterialDatePicker.Builder.datePicker().build()
       datePicker.addOnPositiveButtonClickListener {
           val timeZone = TimeZone.getDefault()
           val offset = timeZone.getOffset(Date().time) * -1
           binding.tilDate.text = Date(it + offset).format()
       }
       datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
       }
       //evento de relogio
        binding.tilTime.editText?.setOnClickListener {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val minute =  if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
            val hour =  if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
            binding.tilTime.text = "$hour:$minute"
        }
        timePicker.show(supportFragmentManager, null)
    }
       binding.btnCancel.setOnClickListener {
           finish()
       }
       binding.btnNewTask.setOnClickListener {
            val task = Task (
                title = binding.tilTitle.text,
                date =  binding.tilDate.text,
                time =  binding.tilTime.text,
                id = intent.getIntExtra(TASK_ID, 0)
                    )
           TaskDataSource.insertTaks(task)

           setResult(Activity.RESULT_OK)
           finish()
       }

    }

    companion object {
        const val  TASK_ID = "task_id"
    }

}