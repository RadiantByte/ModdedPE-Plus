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
package com.radiantbyte.moddedpe.plus.pesdk;

import android.content.Context;

import com.radiantbyte.moddedpe.plus.pesdk.nmod.NModAPI;
import com.radiantbyte.moddedpe.plus.pesdk.utils.MinecraftInfo;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class PESdk {
    private final MinecraftInfo mMinecraftInfo;
    private final NModAPI mNModAPI;
    private final GameManager mGameManager;
    private boolean mIsInited;

    public PESdk(Context context) {
        mMinecraftInfo = new MinecraftInfo(context);
        mNModAPI = new NModAPI(context);
        mGameManager = new GameManager(this);
        mIsInited = false;
    }

    public void init() {
        mNModAPI.initNModDatas();
        mIsInited = true;
    }

    public boolean isInited() {
        return mIsInited;
    }

    public NModAPI getNModAPI() {
        return mNModAPI;
    }

    public MinecraftInfo getMinecraftInfo() {
        return mMinecraftInfo;
    }


    public GameManager getGameManager() {
        return mGameManager;
    }
}
