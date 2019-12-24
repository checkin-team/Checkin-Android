package com.checkin.app.checkin.misc.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
open class BriefModel : Serializable {
    open var pk: Long = 0

    @JsonProperty("display_name")
    var displayName: String? = null

    @JsonProperty("display_pic_url")
    var displayPic: String? = null

    constructor()

    constructor(pk: Long, displayName: String?, displayPic: String?) {
        this.pk = pk
        this.displayName = displayName
        this.displayPic = displayPic
    }
}
