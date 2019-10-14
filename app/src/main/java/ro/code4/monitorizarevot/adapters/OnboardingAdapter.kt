package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.item_onboarding.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.OnboardingScreen
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.helper.toHtml


class OnboardingAdapter(
    private val context: Context,
    private val screens: ArrayList<OnboardingScreen>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.item_onboarding, container, false)

        val holder: ViewHolder
        if (view.tag != null) {
            holder = view.tag as ViewHolder
        } else {
            holder = ViewHolder(view)
            view.tag = holder
        }

        with(screens[position]) {
            holder.itemView.titleOnboarding.text = context.getString(titleResId)
            holder.itemView.descriptionOnboarding.text =
                context.getString(descriptionResId).toHtml()
            holder.itemView.icOnboarding.setImageResource(imageResId)
        }

        (container as ViewPager).addView(view)

        return view
    }

    override fun getCount(): Int = screens.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj as View


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

}