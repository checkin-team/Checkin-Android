package com.checkin.app.checkin.accounts

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.node.ObjectNode

data class AccountModel(
        val pk: Long,
        @JsonProperty("target_pk") val targetPk: Long,
        @JsonProperty("acc_type")
        @JsonDeserialize(using = ACCOUNT_TYPE.AccountTypeDeserializer::class)
        @JsonSerialize(using = ACCOUNT_TYPE.AccountTypeSerializer::class)
        val accountType: ACCOUNT_TYPE,
        val pic: String?,
        val name: String,
        val detail: ObjectNode?
) {

    val formatAccountType: String = when (accountType) {
        ACCOUNT_TYPE.USER -> "User's account"
        ACCOUNT_TYPE.SHOP_OWNER -> "Owner's account"
        ACCOUNT_TYPE.SHOP_ADMIN -> "Admin's account"
        ACCOUNT_TYPE.RESTAURANT_MANAGER -> "Manager's account"
        ACCOUNT_TYPE.RESTAURANT_WAITER -> "Waiter's account"
        ACCOUNT_TYPE.RESTAURANT_COOK -> "Cook's account"
    }

    override fun toString(): String = name
}

enum class ACCOUNT_TYPE(val id: Int) {
    USER(201), SHOP_OWNER(202), SHOP_ADMIN(203), RESTAURANT_MANAGER(204), RESTAURANT_WAITER(205), RESTAURANT_COOK(206);

    companion object {
        fun getById(id: Int): ACCOUNT_TYPE = values().find { it.id == id } ?: USER
    }

    class AccountTypeDeserializer : JsonDeserializer<ACCOUNT_TYPE>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): ACCOUNT_TYPE = getById(p.text.toInt())
    }

    class AccountTypeSerializer : JsonSerializer<ACCOUNT_TYPE>() {
        override fun serialize(value: ACCOUNT_TYPE?, gen: JsonGenerator, serializers: SerializerProvider?) {
            value?.id?.let { gen.writeNumber(it) }
        }
    }
}