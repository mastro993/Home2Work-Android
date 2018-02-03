package it.gruppoinfor.home2work;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.activities.ShowUserActivity;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.UserProfile;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("it.gruppoinfor.home2work", appContext.getPackageName());


        HomeToWorkClient.getInstance().getUserProfile(7l, new OnSuccessListener<UserProfile>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                UserProfile test = userProfile;
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
