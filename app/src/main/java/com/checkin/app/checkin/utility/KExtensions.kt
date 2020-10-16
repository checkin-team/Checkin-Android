package com.checkin.app.checkin.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.LocationManager
import android.net.Uri
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.checkin.app.checkin.misc.models.GeolocationModel
import com.checkin.app.checkin.misc.models.LocationModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.sign

/*
    Collections
 */
inline fun <T> Collection<T>?.isNotEmpty(): Boolean = this != null && !this.isEmpty()

inline fun <T> Collection<T>?.hasAtleastSize(minSize: Int): Boolean = this != null && this.size >= minSize

inline fun <T> Collection<T>.indexOfFirstOrNull(predicate: (T) -> Boolean) = indexOfFirst(predicate).takeIf { it != -1 }

/*
    Views
 */
fun TabLayout.setTabBackground(@ColorInt color: Int) = (0 until tabCount).forEach {
    getTabAt(it)?.view?.setBackgroundColor(color)
}

fun ImageView.blackAndWhite() {
    colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0.0f) })
}

fun EditText.onDone(block: (editable: Editable) -> Unit) {
    // TODO: Design a better way to do it!
    doAfterTextChanged { block(it ?: return@doAfterTextChanged) }
//    setOnEditorActionListener { _, actionId, event ->
//        when (actionId) {
    // cancel event is missed here
//            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
//                block(text)
//                true
//            }
//            else -> false
//        }
//    }
    // focus is changed a lot needlessly (like when calling setText)
//    setOnFocusChangeListener { _, hasFocus ->
//        if (!hasFocus) block(text)
//    }
}


fun Context.toast(msg: String?) = Utils.toast(this, msg)
fun Context.toast(@StringRes msgRes: Int) = Utils.toast(this, msgRes)

fun Fragment.toast(msg: String?) = context?.run { toast(msg) }
fun Fragment.toast(@StringRes msgRes: Int) = context?.run { toast(msgRes) }

fun Activity.snack(msg: String) = Utils.snack(this, msg)
fun View.snack(@StringRes msgRes: Int) = Utils.snack(this, msgRes)
fun Activity.errorSnack(msg: String) = Utils.errorSnack(this, msg)

fun Context.navigateBackToHome() = Utils.navigateBackToHome(this)

val Activity.firebaseAnalytics
    get() = FirebaseAnalytics.getInstance(this)

/*
    Data Utils
 */
internal fun internalNavigateToLocation(context: Context, latitude: Double, longitude: Double) {
    val gmmIntentUri = Uri.parse("google.navigation:q=${latitude},${longitude}")
    Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        `package` = "com.google.android.apps.maps"
        ContextCompat.startActivity(context, this, null)
    }
}

fun LocationModel.navigateToLocation(context: Context) = internalNavigateToLocation(context, latitude, longitude)

fun GeolocationModel.navigateToLocation(context: Context) = internalNavigateToLocation(context, latitude, longitude)

fun String.callPhoneNumber(context: Context) {
    val phoneIntent = Uri.parse("tel:$this")
    ContextCompat.startActivity(context, Intent(Intent.ACTION_DIAL, phoneIntent), null)
}

fun String.toHtml() = Utils.fromHtml(this)

fun Double.toCurrency(context: Context) = Utils.formatCurrencyAmount(context, this)

fun <K, V> MutableMap<K, V>.putAll(vararg pairs: Pair<K, V>) {
    for ((key, value) in pairs) {
        put(key, value)
    }
}

/*
    Datetime
 */
// Number of days between two Calendar instances
operator fun Calendar.minus(other: Calendar): Int {
    val end = Calendar.getInstance().apply {
        timeInMillis = this@minus.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val start = Calendar.getInstance().apply {
        timeInMillis = other.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val duration = end.timeInMillis - start.timeInMillis
    return (sign(duration.toDouble()) * TimeUnit.MILLISECONDS.toDays(abs(duration))).toInt()
}

fun Date.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar }

fun Date.add(unit: Int, amount: Int): Date = toCalendar()
        .apply { add(unit, amount) }
        .time

fun Throwable.log(tag: String, errMsg: String? = null) = Utils.logErrors(tag, this, errMsg)

/*
    Lifecycle
 */
val LifecycleOwner.coroutineLifecycleScope: LifecycleCoroutineScope
    get() = lifecycleScope

/*
    Permissions
 */
val Context.hasLocationPermission: Boolean
    get() = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.isPermissionGranted(permission: String) = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

val Context.isLocationEnabled: Boolean
    get() = LocationManagerCompat.isLocationEnabled(getSystemService(Context.LOCATION_SERVICE) as LocationManager)
