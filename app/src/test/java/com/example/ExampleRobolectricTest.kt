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
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ETFC Stream", appName)
    }

    @Test
    fun `test role based access control hierarchy`() {
        // Free user cannot access Standard PPV or VIP
        assertFalse(UserRole.FREE_USER.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD))
        assertFalse(UserRole.FREE_USER.canAccessTier(ContentTier.VIP_RINGSIDE_EXCLUSIVE))
        assertFalse(UserRole.FREE_USER.canModerate())

        // PPV buyer can access PPV Main card but not VIP multi-cam
        assertTrue(UserRole.PPV_BUYER.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD))
        assertFalse(UserRole.PPV_BUYER.canAccessTier(ContentTier.VIP_RINGSIDE_EXCLUSIVE))

        // VIP Member can access standard and VIP tiers
        assertTrue(UserRole.VIP_MEMBER.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD))
        assertTrue(UserRole.VIP_MEMBER.canAccessTier(ContentTier.VIP_RINGSIDE_EXCLUSIVE))
        assertFalse(UserRole.VIP_MEMBER.canModerate())

        // Moderator can moderate and access content
        assertTrue(UserRole.MODERATOR.canModerate())
        assertTrue(UserRole.MODERATOR.canAccessTier(ContentTier.MODERATION_TOOLS))
    }
}
