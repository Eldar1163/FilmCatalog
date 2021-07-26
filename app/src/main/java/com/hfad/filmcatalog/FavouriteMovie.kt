package com.hfad.filmcatalog

import io.realm.RealmObject

open class FavouriteMovie(var id: Int = 0, var isFavourite: Boolean = false): RealmObject() {
}