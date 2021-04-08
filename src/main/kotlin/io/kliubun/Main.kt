package io.kliubun


fun main(args: Array<String>) {
    println(Integer.valueOf(1).compareTo(Integer.valueOf(2)))
    val tree = BTree<Int>(3)
    for (item in 1 until 2501)
        tree.insert(item)

    val start = System.currentTimeMillis()
    tree.insert(2501)
    val end = System.currentTimeMillis()
    println("Millis: ${end-start}")
}
