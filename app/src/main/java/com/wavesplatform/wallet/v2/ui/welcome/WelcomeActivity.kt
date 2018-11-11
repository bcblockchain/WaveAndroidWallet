package com.wavesplatform.wallet.v2.ui.welcome

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.asksira.loopingviewpager.LoopingViewPager
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v2.data.model.local.Language
import com.wavesplatform.wallet.v2.data.model.local.WelcomeItem
import com.wavesplatform.wallet.v2.ui.auth.choose_account.ChooseAccountActivity
import com.wavesplatform.wallet.v2.ui.auth.import_account.ImportAccountActivity
import com.wavesplatform.wallet.v2.ui.auth.new_account.NewAccountActivity
import com.wavesplatform.wallet.v2.ui.base.view.BaseDrawerActivity
import com.wavesplatform.wallet.v2.ui.language.change_welcome.ChangeLanguageBottomSheetFragment
import com.wavesplatform.wallet.v2.ui.splash.SplashActivity
import com.wavesplatform.wallet.v2.util.launchActivity
import com.wavesplatform.wallet.v2.util.notNull
import kotlinx.android.synthetic.main.activity_welcome.*
import pers.victor.ext.click
import java.util.*
import javax.inject.Inject


class WelcomeActivity : BaseDrawerActivity(), WelcomeView {

    @Inject
    @InjectPresenter
    lateinit var presenter: WelcomePresenter

    private var menu: Menu? = null

    @ProvidePresenter
    fun providePresenter(): WelcomePresenter = presenter

    override fun configLayoutRes() = R.layout.activity_welcome

    companion object {
        var REQUEST_NEW_ACCOUNT = 55
        var REQUEST_SIGN_IN = 56
        var REQUEST_IMPORT_ACC = 57
    }

    private fun createDataBundle(): Bundle {
        val options = Bundle()
        options.putString("animation", "left_slide")
        return options
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.basic50)
        setNavigationBarColor(R.color.basic50)
        setupToolbar(toolbar_view)

        button_create_account.click {
            launchActivity<NewAccountActivity>(requestCode = REQUEST_NEW_ACCOUNT, options = createDataBundle())
        }

        relative_sign_in.click {
            launchActivity<ChooseAccountActivity>(clear = true, requestCode = REQUEST_SIGN_IN)
        }

        relative_import_acc.click {
            launchActivity<ImportAccountActivity>(REQUEST_IMPORT_ACC)
        }

        view_pager.setPageTransformer(false) { page, position ->
            val root = page.findViewById<LinearLayout>(R.id.linear_root)
            if (position <= -1.0F || position >= 1.0F) {
                root.alpha = 0.0F
            } else if (position == 0.0F) {
                root.alpha = 1.0F
            } else {
                root.alpha = 1.0F - Math.abs(position);
            }
        }
        view_pager.adapter = WelcomeItemsPagerAdapter(this, populateList(), true)
        view_pager.offscreenPageLimit = 5
        view_pager_indicator.count = view_pager.indicatorCount
        view_pager.setIndicatorPageChangeListener(object : LoopingViewPager.IndicatorPageChangeListener {
            override fun onIndicatorProgress(selectingPosition: Int, progress: Float) {
                view_pager_indicator.setProgress(selectingPosition, progress)
            }

            override fun onIndicatorPageChange(newIndicatorPosition: Int) {
            }
        })
    }

    private fun populateList(): ArrayList<WelcomeItem> {
        return arrayListOf(WelcomeItem(R.drawable.ic_userimg_blockchain_80, R.string.welcome_blockchain_title, R.string.welcome_blockchain_description),
                WelcomeItem(R.drawable.ic_userimg_wallet_80, R.string.welcome_wallet_title, R.string.welcome_wallet_description),
                WelcomeItem(R.drawable.ic_userimg_dex_80, R.string.welcome_dex_title, R.string.welcome_dex_description),
                WelcomeItem(R.drawable.ic_userimg_token_80, R.string.welcome_token_title, R.string.welcome_token_description))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_language -> {
                val dialog = ChangeLanguageBottomSheetFragment()
                dialog.languageChooseListener = object : ChangeLanguageBottomSheetFragment.LanguageSelectListener {
                    override fun onLanguageSelected() {
                        menu.notNull { updateMenuTitle() }
                    }
                }
                dialog.show(supportFragmentManager, dialog::class.java.simpleName)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_language, menu)
        updateMenuTitle()
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateMenuTitle() {
        val bedMenuItem = menu?.findItem(R.id.action_change_language)
        bedMenuItem?.title = getString(Language.getLanguageByCode(preferencesHelper.getLanguage()).code)
    }

    override fun onBackPressed() {
        finish()
        launchActivity<SplashActivity>(clear = true) {
            putExtra(SplashActivity.EXIT, true)
        }
    }
}
