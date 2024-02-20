package com.example.grammarous

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var age:Int?=null

    /**
     * Empty constructor required for Firebase.
     */
    constructor() {}

    /**
     * Constructor to initialize user data.
     *
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param uid The unique user ID (UID) of the user.
     */
    constructor(name: String?, email: String?, uid: String?,age:Int?) {
        this.email = email
        this.name = name
        this.uid = uid
        this.age=age
    }
}

