package com.example.rflix.data.response

class Video {
    var id: String = ""
    var iso_639_1: String = ""
    var iso_3166_1: String = ""
    var key: String = ""
    var name: String = ""
    var site: String = ""
    var size: Int = 0
    var type: String = ""

    override fun toString(): String {
        return "$id, $iso_639_1, $iso_3166_1, $key, $name, $site, $size, $type"
    }
}