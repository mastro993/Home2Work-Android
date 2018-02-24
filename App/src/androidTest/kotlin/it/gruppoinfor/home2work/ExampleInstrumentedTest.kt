package it.gruppoinfor.home2work

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import org.junit.Test
import org.junit.runner.RunWith

import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.activities.ShowUserActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.UserProfile

import org.junit.Assert.*

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


        HomeToWorkClient.getInstance().getUserProfile(7L, { userProfile -> val test = userProfile }) { e -> e.printStackTrace() }
    }
}
