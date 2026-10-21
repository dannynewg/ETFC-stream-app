package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.ContentTier
import com.example.data.models.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals(if (BuildConfig.IS_ADMIN_APP) "ETFC Promoter CRM" else "ETFC Stream", appName)
    }

    @Test
    fun `test role based access control hierarchy`() {
        // Guest can only access free prelims
        assertTrue(UserRole.GUEST.canAccessTier(ContentTier.FREE_PRELIMS))
        assertFalse(UserRole.GUEST.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD))
        assertFalse(UserRole.GUEST.canModerate())

        // User can access standard PPV and VIP streams
        assertTrue(UserRole.USER.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD))
        assertTrue(UserRole.USER.canAccessTier(ContentTier.VIP_RINGSIDE_EXCLUSIVE))
        assertFalse(UserRole.USER.canModerate())

        // Admin can access everything and moderate
        assertTrue(UserRole.ADMIN.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD))
        assertTrue(UserRole.ADMIN.canAccessTier(ContentTier.MODERATION_TOOLS))
        assertTrue(UserRole.ADMIN.canModerate())
    }
}
