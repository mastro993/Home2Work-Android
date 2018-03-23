package it.gruppoinfor.home2work

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        //assertEquals("it.gruppoinfor.home2work", appContext.getPackageName());


        HomeToWorkClient.getUserProfile(7L, { userProfile -> val test = userProfile }) { e -> e.printStackTrace() }
    }
}
