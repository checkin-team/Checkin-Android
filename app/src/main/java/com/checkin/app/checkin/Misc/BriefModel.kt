package com.checkin.app.checkin.Misc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
open class BriefModel : Serializable {
    @JsonProperty("pk")
    var pk: String? = null

    @JsonProperty("display_name")
    var displayName: String? = null

    @JsonProperty("display_pic_url")
    var displayPic: String? = null

    constructor()

    constructor(pk: String, displayName: String, displayPic: String) {
        this.pk = pk
        this.displayName = displayName
        this.displayPic = displayPic
    }

    constructor(pk: Long, displayName: String, displayPic: String) {
        this.pk = pk.toString()
        this.displayName = displayName
        this.displayPic = displayPic
    }
}
