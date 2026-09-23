package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.PredictionConstants
import com.example.ui.GoalvixViewModel
import com.example.ui.PrivacyPolicyDialog
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun read_string_from_context() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GOALVIX", appName)
  }

  @Test
  fun test_intent_flow_and_unlock_state() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = GoalvixViewModel(app)

    // Initial state: not initiated, not unlocked
    assertFalse(viewModel.isAdFlowInitiated.value)
    assertFalse(viewModel.isPremiumUnlocked.value)

    // Trigger Intent flow
    viewModel.openAdsterraLink(app)

    // Verify Intent was dispatched
    val shadowApp = shadowOf(app)
    val nextIntent = shadowApp.nextStartedActivity
    assertEquals(Intent.ACTION_VIEW, nextIntent?.action)
    assertEquals(PredictionConstants.ADSTERRA_DIRECT_LINK, nextIntent?.dataString)

    // Verify local state recorded the ad flow
    assertTrue(viewModel.isAdFlowInitiated.value)
    assertFalse(viewModel.isPremiumUnlocked.value)

    // Simulate returning to the app (onResume)
    viewModel.onAppResumed()

    // Verify Premium content is now unlocked locally for this session
    assertTrue(viewModel.isPremiumUnlocked.value)
  }

  @Test
  fun test_manual_unlock_fallback() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = GoalvixViewModel(app)

    assertFalse(viewModel.isPremiumUnlocked.value)
    viewModel.onManualUnlockClicked()
    assertTrue(viewModel.isPremiumUnlocked.value)
  }

  @Test
  fun test_refresh_all_triggers_reloading() = runTest {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = GoalvixViewModel(app)

    viewModel.refreshInternal()
    assertFalse(viewModel.isRefreshing.value)
  }

  @Test
  fun test_share_prediction_intent() = runTest {
    val app = ApplicationProvider.getApplicationContext<Application>()
    com.example.util.ShareHelper.sharePrediction(
        context = app,
        imageUrl = null,
        title = "Free Prediction"
    )

    val shadowApp = shadowOf(app)
    val chooserIntent = shadowApp.nextStartedActivity
    assertEquals(Intent.ACTION_CHOOSER, chooserIntent?.action)

    @Suppress("DEPRECATION")
    val targetIntent = chooserIntent?.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
    assertEquals(Intent.ACTION_SEND, targetIntent?.action)
    assertTrue(targetIntent?.getStringExtra(Intent.EXTRA_TEXT)?.contains("Free Prediction") == true)
  }

  @Test
  fun test_privacy_policy_dialog_content() {
    var dismissed = false
    composeTestRule.setContent {
      MyApplicationTheme {
        PrivacyPolicyDialog(
          onDismiss = { dismissed = true }
        )
      }
    }

    composeTestRule.onNodeWithTag("privacy_policy_dialog").assertExists()
    composeTestRule.onNodeWithTag("dismiss_privacy_policy_button").performClick()
    assertTrue(dismissed)
  }

  @Test
  fun test_prediction_skeleton_renders() {
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.PredictionSkeletonPlaceholder(
          badgeColor = com.example.ui.theme.GoalvixGreenNeon,
          testTag = "test_skeleton"
        )
      }
    }

    composeTestRule.onNodeWithTag("test_skeleton").assertExists()
  }
}
