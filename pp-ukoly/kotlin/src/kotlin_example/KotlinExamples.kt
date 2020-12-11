package kotlin_example

// All helper functions are written in Java.
// This is to demonstrate the full interoperability of Java and Kotlin.

import java_example.HelperFunctions.randomEvenInt
import java_example.HelperFunctions.randomEvenIntList


// In contrast with Java, Kotlin allows top-level functions. During compilation, Kotlin compiler creates a Java class
// and converts all these functions into static functions.
fun main(args : Array<String>) {
    exampleFunctional()
}

// Kotlin differentiates between immutable and mutable variables.
// An immutable variable is declared using the val keyword.
// A mutable one is declared using the keyword var.
fun exampleMutable() {
    // Val keyword is equivalent to the final keyword in Java. Such a variable is immutable.
    val finalInt: Int

    // Can assign a value the first time.
    finalInt = 1
    println(finalInt)

    // Will not compile, since the variable is immutable.
    // finalVariable += 2

    // Var keyword makes the variable mutable.
    var mutableInt = finalInt
    mutableInt += 1
    println(mutableInt)
}

// All types in Kotlin are non-nullable by default.
// To declare a type as nullable, a '?' is added to the type identifier.
fun exampleNullable() {
    // Nullable int is explicitly declared to be nullable.
    val nullableInt: Int? = randomEvenInt(1000)

    // Will not compile without a null check.
    // println(nullableInt + 10)

    if (nullableInt == null) {
        println("nullableInt is null")
    }

    // If a null check is performed, the variable is automatically cast to regular Int.
    println(nullableInt)

    val nullableList: List<Int?>? = randomEvenIntList(1000, 20)
    // Kotlin also provides a null check operator '?.'. It return either the value or a null.
    // It can be chained with an Elvis operator '?:' which return a value if the object on which
    // it's called is null.
    println(nullableList?.fold(0) { sum, element -> sum + (element?:0) } ?: "The list is null")
}

// Kotlin provides several collection functions which receive another function as a parameter and apply it to a list.
// It is common practice to define the function using a lambda function. This serves as an example of elements of
// functional programming in Kotlin.
fun exampleFunctional() {
    val randomIntList = randomEvenIntList(1000, 10)
    println(randomIntList)

    // Map constructs a new list by applying a function to each of the old list's elements.
    println(randomIntList.map { it?.plus(it) })

    // Fold is a aggregate function. It applies a function to all list elements and accumulates the results.
    println(randomIntList.fold(0) { sum, element -> sum + (element?:0) })

    // SortedBy return a sorted list using a comparison function.
    println(randomIntList.sortedBy { it ?: 0 })
}