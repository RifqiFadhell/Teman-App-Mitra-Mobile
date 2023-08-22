package id.teman.app.mitra.common

import java.util.concurrent.atomic.AtomicBoolean

class Event<T>(private val data: T) {
    private var consumed = AtomicBoolean(false)
    fun consumeOnce(consumer: (T) -> Unit) {
        if (consumed.compareAndSet(false, true)) {
            consumer(data)
        }
    }

    fun peek(): T? = if (consumed.get()) null else data
    fun disposed() {
        consumed.compareAndSet(false, true)
    }
}