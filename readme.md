# Work Sample for Mobile Aspect, Kotlin Variant

### Solution
* Solution is in the EQMobileWork folder
* The outer project is an Android app that implements the library and transmits the user location 
when the fab action button is clicked

### Usage
```kotlin
val lib = Library()

// Using a custom location event
lib.log(LocationEvent(0F, 0F, System.currentTimeMillis() / 1000L, "empty"))

// Or by passing the app context, the last know location of the user will be used
lib.log(this)
```

### Tests
`src/test/java/com/example/eqmobilework/LibraryTest.kt`
