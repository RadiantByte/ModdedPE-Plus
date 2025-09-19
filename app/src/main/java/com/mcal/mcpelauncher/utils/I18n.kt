/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcpelauncher.utils

import android.app.Activity
import android.os.Build
import com.mcal.mcpelauncher.data.Preferences
import java.util.*

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object I18n {
    @JvmStatic
    fun setLanguage(context: Activity) {
        val defaultLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        val config = context.resources.configuration
        val newLocale = when (Preferences.languageType) {
            0 -> Locale.getDefault()
            1 -> Locale.ENGLISH
            2 -> Locale.SIMPLIFIED_CHINESE
            3 -> Locale.JAPANESE
            4 -> Locale.Builder().setLanguage("ru").setRegion("RU").build()
            5 -> Locale.CHINESE
            6 -> Locale.Builder().setLanguage("tr").build() // Турецкий язык
            7 -> Locale.Builder().setLanguage("pt").build() // Португальский
            8 -> Locale.FRENCH
            9 -> Locale.Builder().setLanguage("th").build() // Тайский
            10 -> Locale.Builder().setLanguage("kk").build() // Казахский
            11 -> Locale.Builder().setLanguage("uk").build() // Украинский
            else -> Locale.getDefault()
        }
        
        config.setLocale(newLocale)
        
        if (defaultLocale != newLocale) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.createConfigurationContext(config)
            } else {
                @Suppress("DEPRECATION")
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
        }
    }
}