package io.kliubun

import java.io.PrintStream
import java.util.*


class BTree<T : Comparable<T>>(private val minDegree: Int) {
    private var root: BTreeNode<T>? = null

    fun search(k: T): BTreeNode<*>? {
        return if (this.root == null) null else this.root!!.search(k)
    }

    fun print() {
        val sb = StringBuilder()
        root?.traversePreOrder(sb, "", "", true)
        println(sb.toString())
    }

    fun insert(k: T) {
        if (root == null) {
            root = BTreeNode(minDegree, true)
            root?.keys?.add(k)
        } else {
            if (root!!.isFull()) {
                val newRoot = BTreeNode<T>(minDegree, false)

                // Make old root as child of new root
                newRoot.children.add(root)

                // Split the old root and move 1 key to the new root
                newRoot.splitChild(0, root)

                // New root has two children now.  Decide which of the
                // two children is going to have new key
                if (newRoot.keys[0]!! > k) {
                    newRoot.children[0]!!.insertNonFull(k)
                } else {
                    newRoot.children[1]!!.insertNonFull(k)
                }

                // Change root
                root = newRoot
            } else {
                root?.insertNonFull(k)
            }
        }
    }

    class BTreeNode<T : Comparable<T>>(
            private val minDegree: Int,
            var leaf: Boolean
    ) {
        var keys: MutableList<T?>
        var children: MutableList<BTreeNode<T>?>

        init {
            keys = ArrayList()
            children = ArrayList()
        }

        fun isFull():Boolean {
            return keys.size == 2 * minDegree - 1
        }


        fun search(k: T): BTreeNode<*>? { // returns NULL if k is not present.

            // Find the first key greater than or equal to k
            var i = 0
            while (i < keys.size && keys[i]!! < k) {
                Thread.sleep(1)
                i++
            }

            // If the found key is equal to k, return this node
            if (i < keys.size && keys[i] == k) {
                return this
            }

            // If the key is not found here and this is a leaf node
            return if (leaf) null else children[i]!!.search(k)
            // Go to the appropriate child
        }

        fun traversePreOrder(sb: StringBuilder, padding: String?, pointer: String?, isLast: Boolean) {
            sb.append(padding)
            sb.append(pointer)
            sb.append(this.keys.joinToString())
            sb.append("\n")
            val paddingBuilder = StringBuilder(padding)
            if (isLast) {
                paddingBuilder.append("   ");
            } else {
                paddingBuilder.append("│  ");
            }
            val paddingForBoth = paddingBuilder.toString()
            for (i in 0 until children.size) {
                val pointer = if (i == children.size - 1) "└──" else "├──";
                children[i]?.traversePreOrder(sb, paddingForBoth, pointer, (i == children.size - 1))
            }
        }

        fun insertNonFull(k: T) {
            Thread.sleep(1)
            // Initialize index as index of rightmost element
            var keyIndex = keys.size - 1

            // If this is a leaf node
            if (leaf) {
                keys.add(null) // increase on one element
                // The following loop does two things
                // a) Finds the location of new key to be inserted
                // b) Moves all greater keys to one place ahead
                while (keyIndex >= 0 && keys[keyIndex]!! > k) {
                    keys[keyIndex + 1] = keys[keyIndex]
                    keyIndex--
                }

                // Insert the new key at found location
                keys[keyIndex + 1] = k
                // If this node is not leaf
            } else {
                // Find the child which is going to have the new key
                while (keyIndex >= 0 && keys[keyIndex]!! > k)
                    keyIndex--

                if (children[keyIndex + 1]!!.isFull()) {
                    // If the child is full, then split it
                    splitChild(keyIndex + 1, children[keyIndex + 1])

                    // After split, the middle key of C[i] goes up and C[i] is splitted into two.
                    // See which of the two is going to have the new key
                    if (keys[keyIndex + 1]!! < k)
                        keyIndex++

                }
                children[keyIndex + 1]!!.insertNonFull(k)
            }
        }

        // A utility function to split the child y of this node
        // Note that y must be full when this function is called
        fun splitChild(index: Int, nodeY: BTreeNode<T>?) {
            val currentNumOfKeys = keys.size

            // Create a new node which is going to store (t-1) keys of nodeY
            val nodeZ = BTreeNode<T>(nodeY!!.minDegree, nodeY.leaf)

            // Copy the last (minDegree-1) keys of y to z
            repeat(nodeY.minDegree - 1) {
                nodeZ.keys.add(nodeY.keys[minDegree])
                nodeY.keys.removeAt(minDegree)
            }

            // Copy the last minDegree children of y to z
            if (!nodeY.leaf) {
                repeat(minDegree) {
                    nodeZ.children.add(nodeY.children[minDegree])
                    nodeY.children.removeAt(minDegree)
                }
            }

            // Since this node is going to have a new child, create space of new child
            children.add(index + 1, nodeZ)

            // Copy the middle key of nodeY to this node
            keys.add(index, nodeY.keys[minDegree - 1])
            nodeY.keys.removeAt(minDegree - 1)
        }

    }
}

