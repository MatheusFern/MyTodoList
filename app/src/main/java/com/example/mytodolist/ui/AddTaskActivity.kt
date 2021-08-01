package com.example.mytodolist.ui

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mytodolist.R
import com.example.mytodolist.databinding.ActivityAddTaskBinding
import com.example.mytodolist.databinding.ActivityNotificationBinding
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
           createNotificationChannel()
           createNotification()
           finish()
       }

    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "task teste"
            val descriptionText = "texto comum de test"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL, name, importance).apply {
                this.description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotification(){
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, ActivityNotificationBinding::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle("Lembrete")
            .setContentText("Seu lembrete foi salvo com sucesso")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(12, builder.build())
        }
    }




    companion object {
        const val  TASK_ID = "task_id"
        private val NOTIFICATION_CHANNEL = "ID_DO_CANAL"
    }

}