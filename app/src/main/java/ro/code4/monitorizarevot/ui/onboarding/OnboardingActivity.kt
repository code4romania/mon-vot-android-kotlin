package ro.code4.monitorizarevot.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.OnboardingAdapter
import ro.code4.monitorizarevot.adapters.helper.OnboardingScreen
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsActivity
import ro.code4.monitorizarevot.ui.section.PollingStationActivity

class OnboardingActivity : BaseAnalyticsActivity<OnboardingViewModel>() {
    override val layout: Int
        get() = R.layout.activity_onboarding
    override val screenName: Int
        get() = R.string.analytics_title_onboarding
    override val viewModel: OnboardingViewModel by viewModel()
    private lateinit var onboardingAdapter: OnboardingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onboarding().observe(this, Observer {
            setData(it)
        })
        backButton.setOnClickListener {
            onboardingViewPager.setCurrentItem(onboardingViewPager.currentItem - 1, true)
        }
        nextButton.setOnClickListener {
            if (onboardingViewPager.currentItem == onboardingAdapter.count - 1) {
                viewModel.onboardingCompleted()
                startActivityWithoutTrace(PollingStationActivity::class.java)
            } else {
                onboardingViewPager.setCurrentItem(onboardingViewPager.currentItem + 1, true)
            }
        }
    }

    private fun setData(screens: ArrayList<OnboardingScreen>) {
        onboardingAdapter = OnboardingAdapter(this, screens)
        onboardingViewPager.apply {
            adapter = onboardingAdapter
            addOnPageChangeListener(viewPagerPageChangeListener)
            currentItem = 0
        }

    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            private var currentPage: Int = -1
            override fun onPageSelected(position: Int) = Unit

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (positionOffset == 0f && positionOffsetPixels == 0) {
                    currentPage = position
                }
                val lastIdx = onboardingAdapter.count - 1
                val (nextButtonTextResId, backButtonVisibility) = when {
                    currentPage == 0 -> Pair(R.string.onboarding_next, View.GONE)
                    lastIdx == currentPage && lastIdx == position -> Pair(
                        R.string.onboarding_to_app,
                        View.VISIBLE
                    )
                    else -> Pair(R.string.onboarding_next, View.VISIBLE)
                }
                nextButton.text = getString(nextButtonTextResId)
                nextButton.requestLayout()
                backButton.visibility = backButtonVisibility

            }

            override fun onPageScrollStateChanged(state: Int) = Unit
        }
}