%dw 2.0

/**
* Creates a random name
*/
fun createDestination() : String = randomInt(1000000) as String

/**
* Creates an array of N random String messages
*/
fun createMessages(count : Number) : Array<String> = (1 to count) as Array map $ as String