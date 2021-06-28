package org.robojackets.apiary

import org.robojackets.apiary.entity.RocketLaunch
import org.robojackets.apiary.network.SpaceXApi

class SpaceXSDK {
    private val api = SpaceXApi()

    @Throws(Exception::class) suspend fun getLaunches(): List<RocketLaunch> {
        return api.getAllLaunches()
    }
}