package com.leandro.unibook

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform