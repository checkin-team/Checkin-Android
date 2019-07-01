package com.checkin.app.checkin

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented circle_quarter_filter, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under circle_quarter_filter.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.alcatraz.admin.project_alcatraz", appContext.packageName)
    }
}
