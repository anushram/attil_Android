package com.illagu.attil.home.offers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.illagu.attil.R
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.databinding.ActivityFilterBinding


class FilterActivity : SubModuleActivity() {

    private val context: Context = this@FilterActivity
    private val binding by lazy { ActivityFilterBinding.inflate(layoutInflater) }

    var filterType = 0
    var filterPrice = 0
    var filterView = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initToolBar()
        getIntentValue()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.filters)
            setSupportActionBar(binding.lnToolbar.toolbar)
            assert(supportActionBar != null)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            binding.lnToolbar.toolbar.navigationIcon = ContextCompat.getDrawable(
                context,
                R.drawable.ic_action_back
            )
            binding.lnToolbar.toolbar.layoutDirection =
                View.LAYOUT_DIRECTION_LTR
            binding.lnToolbar.toolbar.setNavigationOnClickListener { onBackPressed() }
        } catch (bug: Exception) {
            bug.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            filterPrice = intent.getIntExtra("filterPrice", 0)
            filterType = intent.getIntExtra("filterType", 0)
            filterView = intent.getIntExtra("filterView", 0)
            populateUiElement()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {

            binding.rgType.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_local -> filterType = 1
                    R.id.rb_branded -> filterType = 2
                    else -> {
                    }
                }
            }

            binding.rgPrice.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_low -> filterPrice = 1
                    R.id.rb_high -> filterPrice = 2
                    else -> {
                    }
                }
            }

            binding.rgView.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_mostly_viewed -> filterView = 1
                    R.id.rb_new_arrivals -> filterView = 2
                    R.id.rb_top_selling -> filterView = 3
                    else -> {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateUiElement() {
        try {

            if (filterPrice == 1) {
                binding.rbLow.isChecked = true
            } else if (filterPrice == 2) {
                binding.rbLow.isChecked = true
            }

            if (filterType == 1) {
                binding.rbLocal.isChecked = true
            } else if (filterType == 2) {
                binding.rbBranded.isChecked = true
            }

            if (filterView == 1) {
                binding.rbMostlyViewed.isChecked = true
            } else if (filterView == 2) {
                binding.rbNewArrivals.isChecked = true
            } else if (filterView == 3) {
                binding.rbTopSelling.isChecked = true
            }

        } catch (e: Exception) {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_skip, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuSkip: MenuItem = menu.findItem(R.id.action_skip)
        menuSkip.isVisible = true
        menuSkip.title = getString(R.string.done)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_skip -> {
                handleResponse()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleResponse() {
        try {
            val intent = Intent()
            intent.putExtra("filterType", filterType)
            intent.putExtra("filterPrice", filterPrice)
            intent.putExtra("filterView", filterView)
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}