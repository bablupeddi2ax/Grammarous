package com.example.grammarous.alphabets

class TreeNode
// Add other properties as needed

{
    private var letter: Char?=null
    private var leftAligned = true
    constructor(l:Char?){
        this.letter=l
    }
    fun getLetter():Char?{
        return this.letter
    }
    fun isLeftAligned():Boolean{
        return this.leftAligned
    }
    fun setLeftAligned(b:Boolean){
        this.leftAligned=b
    }
    }
