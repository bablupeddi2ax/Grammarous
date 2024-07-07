package com.example.grammarous

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId  = FirebaseAuth.getInstance().currentUser?.uid

        if(userId!=null){
            val db = FirebaseDatabase.getInstance()
            val userTokenRef = db.getReference("users").child(userId).child("fcmToken")

            userTokenRef.setValue(token).addOnSuccessListener {
                Log.i("Fcm new token set success","success")
            }.addOnFailureListener{
                Log.i("Fcm new token set failed","failed")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
        val body  = message.notification?.body

        notify(title,body)
    }
    private fun notify(title:String?, body:String?){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, "wordChannel")
            .setSmallIcon(R.drawable.logo) // Ensure this is a valid and correct icon resource
            .setContentTitle(title)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.logo))
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.logo)))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "wordChannel",
                "wordChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

    notificationManager.notify(0,notificationBuilder.build())
    }





}
