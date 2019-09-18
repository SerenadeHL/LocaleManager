package com.serenadehl.localemanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.*

/**
 * 作者：Serenade
 * 邮箱：SerenadeHL@163.com
 * 创建时间：2019-09-11 13:04:48
 */
object LocaleManager {
    private const val LANGUAGE = "locale_manager_language"
    private const val COUNTRY = "locale_manager_country"
    private const val VARIANT = "locale_manager_variant"

    private var mDefaultLocale: Locale? = null

    fun setDefaultLocale(locale: Locale) {
        mDefaultLocale = locale
    }

    /**
     * 设置语言
     */
    fun setLocale(context: Context, locale: Locale) {
        saveLocale(context, locale)
        inject(context, locale)
    }

    /**
     * 注入框架
     */
    fun inject(context: Context) {
        val locale = getSavedLocale(context) ?: mDefaultLocale ?: getSystemLocale()
        inject(context, locale)
    }

    /**
     * 注入框架
     */
    private fun inject(context: Context, locale: Locale?) {
        val resources = context.applicationContext.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.locales = localeList
            context.applicationContext.createConfigurationContext(config)
            Locale.setDefault(locale)
        }
        resources.updateConfiguration(config, dm)
    }

    /**
     * 获取修改资源后的Context
     */
    fun getContext(context: Context?): Context? {
        if (context == null) return context

        val locale = getSavedLocale(context) ?: mDefaultLocale ?: getSystemLocale()

        val res = context.resources
        val config = Configuration(res.configuration)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
            context
        }
    }

    /**
     * 获取系统设置Locale
     */
    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault().get(0)
        } else {
            Locale.getDefault()
        }
    }

    /**
     * 获取Locale
     */
    private fun getSavedLocale(context: Context): Locale? {
        val sharedPreferences = getSharedPreferences(context)
        val language = sharedPreferences.getString(LANGUAGE, "") ?: ""
        val country = sharedPreferences.getString(COUNTRY, "") ?: ""
        val variant = sharedPreferences.getString(VARIANT, "") ?: ""

        if (language.isEmpty() && country.isEmpty() && variant.isEmpty()) return null

        return Locale(language, country, variant)
    }

    /**
     * 保存Locale
     */
    @SuppressLint("ApplySharedPref")
    private fun saveLocale(context: Context, locale: Locale) {
        getSharedPreferences(context)
            .edit()
            .putString(LANGUAGE, locale.language)
            .putString(COUNTRY, locale.country)
            .putString(VARIANT, locale.variant)
            .commit()//立即保存，有可能切换语言后重启导致apply无法及时保存
    }

    /**
     * 获取SharedPreference
     */
    private fun getSharedPreferences(context: Context): SharedPreferences {
        val spName = context.applicationInfo.packageName
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }
}