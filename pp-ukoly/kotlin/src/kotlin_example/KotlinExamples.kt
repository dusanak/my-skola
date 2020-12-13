package kotlin_example

// Helper functions are written in Java.
// This is to demonstrate the full interoperability of Java and Kotlin.

import java_example.HelperFunctions.randomEvenInt
import java_example.HelperFunctions.randomEvenIntList

// In contrast with Java, Kotlin allows top-level functions. During compilation, Kotlin compiler creates a Java class
// and converts all top-level functions into this class's static functions.
fun main(args : Array<String>) {
    exampleMutable()
    println()
    exampleNullable()
    println()
    exampleFunctional()
    println()
    exampleObject()
    println()
    exampleTypeChecks()
}

// Kotlin differentiates between immutable and mutable variables.
// An immutable variable is declared using the val keyword.
// A mutable one is declared using the keyword var.
fun exampleMutable() {
    // Val keyword is equivalent to the final keyword in Java. Such a variable is immutable after a value is assigned.
    val finalInt: Int

    // Can assign a value the first time.
    finalInt = 1
    println(finalInt)

    // Will not compile, since the variable is immutable.
    // finalInt += 2

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

// Because Kotlin is an object-oriented language, it must have classes and objects.
// Kotlin has a bit different syntax when it comes to classes.
fun exampleObject() {
    val square = Square(4)

    // Kotlin supports the creation of extension functions, which are defined outside the class itself,
    // in contrast to regular class methods. These functions can access only of class's public fields and interface.
    // The receiving class is not actually modified, however dot-notation is used to call extension methods,
    // which improves code clarity.
    fun Rectangle.circumference(): Int {
        return this.x * 2 + this.y * 2
    }
    println(square.circumference())
}

// Kotlin has a nice way of handling type checking and casting.
// Using a keyword "is", Kotlin performs a type check and in the subsequent block,
// the variable is safely cast to the appropriate type.
// There is also an unsafe way to cast types and that is the keyword "as".
fun exampleTypeChecks() {
    // This might seem to be a raw list. Kotlin however does not have raw collection at all unlike Java.
    // Usually when creating a list, a type should included. But because the list is initialized with values,
    // Kotlin can infer its type.
    val objects = listOf(null, 7, 4, "Test", Rectangle(5, 4), Square(3))

    objects.forEach { obj ->
        if (obj == null) {
            println("Object is $obj")
        }

        if (obj is Int) {
            println("$obj is " + if ((obj % 2) == 0) "even" else "odd")
        }

        if (obj is String) {
            println(obj)
        }

        // There even is a "!is" which checks if an object is not of a type.
        // It still correctly casts to the appropriate type in the else block.
        if (obj !is Shape) {
            println("Not a shape")
        } else {
            println(obj.draw())
        }
    }

    // We can override type inference by explicitly stating the variable's type.
    val x: Any = 5

    // Will not compile
    // println(x + 10)

    // Throws exception at runtime because Int cannot be cast to Float
    try {
        println(x as Float)
    } catch (e: Exception) {
        println(e)
    }

    // In this case, the type cast works.
    println(x as Int + 10)
}