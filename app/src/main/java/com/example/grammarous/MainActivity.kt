package com.example.grammarous

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.annotation.RequiresApi
import com.example.grammarous.alphabets.ShakeAlphabets
import com.example.grammarous.mcq.Mcq
import com.example.grammarous.mcq.Topic
import com.example.grammarous.parentViews.ChildProfile
import com.example.grammarous.utils.ApplicationTimeSpentTracker
import com.example.grammarous.words.Games
import com.example.grammarous.words.ShakeWords
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.system.exitProcess

/*
 TODO
 Login -> TODO [ childName, password, ]
 Signup -> TODO [ name and password, face signup(face detection)(experiment might or might not be implemented) ]
 Alphabets TODO [shakeIt, Learn By Order]
 Words -> TODO [
           ShakeIt(shake to generate words then press on word to get details about it[Word, Parts of Speech, Definition,Use Cases),
            Learn By Order(Display words in alphabetical order),
            Search(use local and remote db based on internet availability)
            ]
 Games ->  TODO [
            Matching->[Synonyms,Antonyms,Meanings,Parts of Speech],
            Fill in the blanks[Synonyms,Antonyms,Meanings,Parts of Speech]
            ]
 Story -> TODO [ Genre(Funny, Sweet, Bed Time, Motivational) ]
 Profile -> TODO [ Name, Age, Progress, words learned, rank, photo]

Navigation TODO
    SIGNUP ->  child details is empty in db then TODO navigate to Collect Details Activity else to main screen(can be any activity home or main)
    LOGIN ->   TODO GOTO HOME (or whatever activity name )
    HOME ->    TODO Display the ui having 3 cards
    cards -> has an image that child can relate to and understand     [TODO]
        TODO  card1 -> LEARN WORDS card
                       |_on click -> ShakeIt -> Navigate to WordShake Activity -> shake-> display word-> onclick-> go to DETAILS screen
                                   |->  Search -> show list of words on searching -> onclick -> go to DETAILS screen
                                   |-> Learn -> list of words starting with a,b,c.....z -> onclick(alphabet(for example a))-> WordsListScreen filter by alphabet
                card2 -> LEARN ALPHABETS -> ShakeIt -> Navigate to LetterShake Activity -> shake -> display letter -> go to Letter Details screen
                                         |-> Learn ->  list of alphabets (display something like i am A i am B ) -> on click display the word and add a button for pronunciation, words with that letter facts about it
                card3 -> PLAY GAMES  -> Matching
                                     -> Fill in Blanks
                                     -> Reveal whats under the card
                                     -> Who am i ?
               card4 -> STORY -> GENRE-> [Funny, Inspiring, Fortune, Bed Time, Princess, Prince]
    Profile -> TODO Name, Age, Gender, Standard, Favorites(optional), Candies earned(Calculated by no of words generated, no of words learnt , no of games played correctly)

    MAKE FRIENDS -> TODO Display list of same and close age -> Onclick -> add to friends
    Chat -> TODO limited messages , take care of messages and what is being sent, emojis, gifs
    Clans -> optional
    Compete -> TODO select friend from Friends list -> select game->Start-> SOCKET MAYBE -> PLAY-> END Game-> Declare winner

 */

class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseDatabase
    private lateinit var ref:DatabaseReference
    private lateinit var txtWords: TextView
    private lateinit var txtAlphabets:TextView
    private lateinit var txtGames:TextView
    private lateinit var btnLogout:Button
    private lateinit var imgProfilePic:CircleImageView
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.grammarous.SHOW_SCREEN_TIME_ALERT") {
                showAlertDialog()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  val childName = intent.getStringExtra("childName")
        val filter = IntentFilter("com.example.grammarous.SHOW_SCREEN_TIME_ALERT")
        filter.priority = RECEIVER_NOT_EXPORTED
        registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        application.registerActivityLifecycleCallbacks(ApplicationTimeSpentTracker())
        val sharedPrefs = getSharedPreferences("userData", MODE_PRIVATE)
        val childName = sharedPrefs.getString("name","user-er1")
        val childAge = sharedPrefs.getInt("age",4)
        val childId = sharedPrefs.getString("uid","")
        val childEmail = sharedPrefs.getString("email","")
        val role = sharedPrefs.getString("role","")
        //val role = intent.getStringExtra("role")
        Toast.makeText(this@MainActivity,childName+role,Toast.LENGTH_SHORT).show()
        auth = FirebaseAuth.getInstance()
        btnLogout = findViewById(R.id.btnLogout)
        txtAlphabets = findViewById(R.id.txtAlphabets)
        txtWords = findViewById(R.id.txtWords)
        txtGames = findViewById(R.id.txtGames)
        imgProfilePic = findViewById(R.id.imgProfilePic)

        // check notification permission
        // request notification permission

        imgProfilePic.setOnClickListener{
            val intent = Intent(this@MainActivity,ChildProfile::class.java )
            startActivity(intent)
        }
        txtWords.setOnClickListener{
            val intent = Intent(this@MainActivity,ShakeWords::class.java)
            startActivity(intent)
        }

        txtAlphabets.setOnClickListener{
            val intent = Intent(this@MainActivity,ShakeAlphabets::class.java)
            startActivity(intent)
        }

        txtGames.setOnClickListener {
            val intent = Intent(this@MainActivity, Games::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {

            logout()
        }
    }
    private fun isNotificationPermissionGranted(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun checkNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            // Notification permission is not granted, show a button to request it
            requestNotificationPermission()
        }
    }
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun logout() {
        auth.signOut()
        val sp = getSharedPreferences("userData", Context.MODE_PRIVATE)
        sp.edit().clear().apply()
        val loginIntent = Intent(this,Signup::class.java)
        startActivity(loginIntent)


    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
        // Eat FiveStar Do nothing
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
//    private fun closeApp() {
//        finishAndRemoveTask()
//    }
private fun closeApp() {
    finishAffinity()
    android.os.Process.killProcess(android.os.Process.myPid())
    exitProcess(0)
}
    private fun showAlertDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Screen Time Alert!")
        dialog.setMessage("You have spent 10 minutes of your time already. Please take a break")
        dialog.setPositiveButton("OK") { dialogInterface, _ ->
            dialogInterface.dismiss()
            closeApp()
        }
        dialog.setOnDismissListener{
            closeApp()
        }
        dialog.show()
    }
}