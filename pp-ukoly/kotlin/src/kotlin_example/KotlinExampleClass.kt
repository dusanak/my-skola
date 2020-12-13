package kotlin_example

// Interfaces work the same way as they do in Java. All classes inheriting from interfaces must implement
// the interface's methods.
interface Shape {
    fun area(): Int
    fun draw(): String
}

// Kotlin classes can have several constructors.
// Every class must have a primary constructor. Primary constructor is a part of the header.
// There can also be any number of secondary constructors.
// Constructor parameters with a var or val prefix are also class fields.
// In contrast with Java, class fields are public by default.
open class Rectangle(val x: Int, val y: Int): Shape {

    override fun area(): Int {
        return x * y
    }

    override fun draw(): String {
        var picture = ""
        picture += "*".repeat(x) + "\n"
        picture += ("*" + " ".repeat(x - 2) + "*" + "\n").repeat(y - 2)
        picture += "*".repeat(x)
        return picture
    }
}

// This is a child class. Classes can only inherit from open classes.
class Square(a: Int) : Rectangle(a, a)

