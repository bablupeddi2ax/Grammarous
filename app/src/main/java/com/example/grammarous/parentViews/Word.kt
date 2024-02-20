package com.example.grammarous.parentViews

class Word {
    private var pronunciation:String?=null
    private var definition:String?=null
    private var partsOfSpeech:String?=null
    private var synonyms:ArrayList<String?>? = null
    private var word:String? = null
    private var timeStamp:Long?  = null

    constructor()

    fun getWord():String?{
        return this.word
    }
    fun getPronunciation():String?{
        return this.pronunciation
    }
    fun getDefinition():String?{
        return this.definition
    }
    fun getPartsOfSpeech():String?{
        return this.partsOfSpeech
    }
    fun getSynonyms():ArrayList<String?>?{
        return this.synonyms
    }
    fun getTimeStamp():Long?{
        return this.timeStamp
    }
    fun setWord(word:String?){
        this.word =word
    }
    fun setDefinition(definition:String?){
        this.definition = definition
    }
    fun setPartsOfSpeech(partsOfSpeech:String?){
        this.partsOfSpeech = partsOfSpeech
    }
    fun setPronunciation(audio:String?){
        this.pronunciation = audio
    }
    fun setSynonyms(synonyms:ArrayList<String?>?){
        this.synonyms = synonyms
    }
    fun setTimeStamp(timeStamp:Long?){
        this.timeStamp  = timeStamp
    }

}