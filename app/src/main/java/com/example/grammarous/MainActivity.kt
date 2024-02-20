package com.example.grammarous

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import com.example.grammarous.alphabets.ShakeAlphabets
import com.example.grammarous.words.Games
import com.example.grammarous.words.ShakeWords
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  val childName = intent.getStringExtra("childName")
        val sharedPrefs = getSharedPreferences("userData", MODE_PRIVATE)
        val childName = sharedPrefs.getString("name","user-er1")
        val childAge = sharedPrefs.getInt("age",4)
        val childId = sharedPrefs.getString("uid","")
        val childEmail = sharedPrefs.getString("email","")
        val role = sharedPrefs.getString("role","")
        //val role = intent.getStringExtra("role")
        Toast.makeText(this@MainActivity,childName+role,Toast.LENGTH_SHORT).show()
        auth = FirebaseAuth.getInstance()

        txtAlphabets = findViewById(R.id.txtAlphabets)
        txtWords = findViewById(R.id.txtWords)
        txtGames = findViewById(R.id.txtGames)
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
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
        // Eat FiveStar Do nothing
    }
}