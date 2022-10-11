package com.deanu.storyapp.login

import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.deanu.storyapp.EspressoIdlingResource
import com.deanu.storyapp.R
import com.deanu.storyapp.common.data.utils.launchFragmentInHiltContainer
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun whenLoginSuccess_shouldNavigateToHome() {
        // Given
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // When
        launchFragmentInHiltContainer<LoginFragment> {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.loginFragment)

            this.viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    Navigation.setViewNavController(this.requireView(), navController)
                }
            }
        }

        val edtEmail = onView(
            Matchers.allOf(
                withId(R.id.edt),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.edt_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        edtEmail.perform(
            ViewActions.replaceText("deanudeanu@gmail.com"),
            ViewActions.closeSoftKeyboard()
        )

        val edtPassword = onView(
            Matchers.allOf(
                withId(R.id.edt),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.edt_password),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        edtPassword.perform(
            ViewActions.replaceText("deanudeanu"),
            ViewActions.closeSoftKeyboard()
        )

        onView(withId(R.id.btn_sign_in)).perform(click())
        onView(isRoot()).perform(waitFor(5000))

        // Then
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.homeFragment)
    }

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }


    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }

    }
}