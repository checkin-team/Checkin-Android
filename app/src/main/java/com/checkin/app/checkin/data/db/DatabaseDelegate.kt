package com.checkin.app.checkin.data.db

import com.checkin.app.checkin.data.BaseRepository
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import kotlin.reflect.KClass

class DatabaseDelegate<T : Any>(private val boxClass: KClass<T>) : Lazy<Box<T>> {
    private var cached: Box<T>? = null

    override val value: Box<T>
        get() {
            return cached ?: AppDatabase.store.boxFor(boxClass).also { cached = it }
        }

    override fun isInitialized(): Boolean = cached != null
}

inline fun <reified T : Any> BaseRepository.dbStore(): Lazy<Box<T>> = DatabaseDelegate(T::class)
