package com.mike_dev.spikescroll

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform