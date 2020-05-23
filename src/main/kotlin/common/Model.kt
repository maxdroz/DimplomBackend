package common

interface Model<T: Model<T>> {
    fun trimmed(): T
}