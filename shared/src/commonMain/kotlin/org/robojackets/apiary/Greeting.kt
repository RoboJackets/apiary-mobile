package org.robojackets.apiary

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}