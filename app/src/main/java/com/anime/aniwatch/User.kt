package com.anime.aniwatch

data class User(
    var username: String? = null,
    var email: String? = null,
    var profileImageUrl: String? = null // Added profile image URL for handling images
) {
    constructor() : this(null, null, null) // Explicit no-argument constructor
}
