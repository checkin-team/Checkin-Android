package com.checkin.app.checkin.data.resource

import android.webkit.URLUtil
import com.checkin.app.checkin.data.Converters

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.node.ArrayNode

@JsonIgnoreProperties(ignoreUnknown = true)
class ProblemModel(@JsonProperty("type") val type: String, @JsonProperty("status") val status: Int, @JsonProperty("title") val title: String) {
    @JsonProperty("detail")
    var detail: String? = null

    @JsonProperty("errors")
    var errors: ArrayNode? = null

    private var errorCode: ERROR_CODE? = null

    fun getErrorCode(): ERROR_CODE? {
        if (this.errorCode == null && URLUtil.isHttpsUrl(type)) {
            val arr = type.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            this.errorCode = ERROR_CODE.findByTag(arr[arr.size - 1])
        }
        return this.errorCode
    }

    enum class ERROR_CODE(internal var tag: String) {
        HTTP_ERROR(""), INVALID_VERSION("invalid_version"), DEPRECATED_VERSION("deprecated_version"),

        SESSION_USER_PENDING_MEMBER("session__user_pending_member"), SESSION_SCHEDULED_PENDING_CART("session__scheduled_writable_exists"),

        INVALID_PAYMENT_MODE_PROMO_AVAILED("offer__apply_invalid_payment_mode"), OFFER_REJECTED_APPLYING("offer__apply_rejected"),

        USER_MISSING_PHONE("user__missing_phone"), ACCOUNT_ALREADY_REGISTERED("account__already_registered");


        companion object {
            internal fun findByTag(tag: String): ERROR_CODE {
                for (code in values()) {
                    if (code.tag == tag)
                        return code
                }
                return HTTP_ERROR
            }
        }
    }

    companion object {
        fun fromResource(resource: Resource<*>): ProblemModel? {
            if (resource.hasErrorBody()) return try {
                Converters.objectMapper.treeToValue(resource.errorBody!!, ProblemModel::class.java)
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                null
            }
            return null
        }
    }
}
