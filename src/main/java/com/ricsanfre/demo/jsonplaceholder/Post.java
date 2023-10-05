package com.ricsanfre.demo.jsonplaceholder;

// Record
// Introduced in Java 14, Records are immutable data classes
// - Private final fields
// - Getters, Constructor, toString, hash and equal methods automatically created
//The equals, hashCode, and toString methods, as well as the private, final fields and public constructor, are generated by the Java compiler.
public record Post(
        Integer userId,
        Integer id,
        String title,
        String body) {

}