package com.checkin.app.checkin.accounts

import com.checkin.app.checkin.utility.*
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.node.ObjectNode
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient

@Entity
data class AccountModel(
        @Id var id: Long,
        val pk: Long,
        @JsonProperty("target_pk") val targetPk: Long,
        @JsonProperty("acc_type")
        @JsonDeserialize(using = ACCOUNT_TYPE.Companion.Deserializer::class)
        @JsonSerialize(using = ACCOUNT_TYPE.Companion.Serializer::class)
        @Convert(converter = ACCOUNT_TYPE.Companion.Converter::class, dbType = Int::class)
        val accountType: ACCOUNT_TYPE,
        val pic: String?,
        val name: String,
        @Transient val detail: ObjectNode?
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

enum class ACCOUNT_TYPE(override val value: Int) : EnumIntType {
    USER(201), SHOP_OWNER(202), SHOP_ADMIN(203),
    RESTAURANT_MANAGER(204), RESTAURANT_WAITER(205), RESTAURANT_COOK(206);

    companion object : EnumIntGetter<ACCOUNT_TYPE>() {
        override fun getByValue(value: Int): ACCOUNT_TYPE = EnumIntType.getByValue(value)

        class Deserializer : EnumDeserializer<ACCOUNT_TYPE, Int>(this)
        class Serializer : EnumSerializer<ACCOUNT_TYPE, Int>(this)
        class Converter : EnumConverter<ACCOUNT_TYPE, Int>(this)
    }
}