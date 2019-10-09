package ro.code4.monitorizarevot.ui.onboarding

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.OnboardingAdapter
import ro.code4.monitorizarevot.adapters.helper.OnboardingScreen
import ro.code4.monitorizarevot.ui.base.BaseActivity

class OnboardingActivity : BaseActivity<OnboardingViewModel>() {
    override val layout: Int
        get() = R.layout.activity_onboarding
    override val viewModel: OnboardingViewModel by viewModel()
    private lateinit var onboardingAdapter: OnboardingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onboarding().observe(this, Observer {
            setData(it)
        })
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
//            btnOnBoardingSkip.text = if (lastIdx == currentPage && lastIdx == position) getString(R.string.onboarding_final_finish) else getString(R.string.onboarding_finish)
            }

            override fun onPageScrollStateChanged(state: Int) = Unit
        }
}