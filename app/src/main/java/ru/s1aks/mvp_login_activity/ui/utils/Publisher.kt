package ru.s1aks.mvp_login_activity.ui.utils

import android.os.Handler

class Publisher<T>(private val isSingle: Boolean = false) {

    private val subscribers: MutableSet<Subscriber<T?>> = mutableSetOf()

    private var value: T? = null

    private var hasFirstValue = false

    fun subscribe(uiHandler: Handler, callback: (T?) -> Unit) {
        val subscriber = Subscriber(uiHandler, callback)
        subscribers.add(subscriber)
        if (hasFirstValue) {
            subscriber.invoke(value)
        }
    }

    fun unsubscribeAll() {
        subscribers.clear()
    }

    fun post(value: T) {
        if (!isSingle) {
            hasFirstValue = true
            this.value = value
        }

        subscribers.forEach {
            it.invoke(value)
        }
    }
}

private data class Subscriber<T>(
    private val uiHandler: Handler,
    private val callback: (T?) -> Unit,
) {
    fun invoke(value: T?) {
        uiHandler.post {
            callback.invoke(value)
        }
    }
}