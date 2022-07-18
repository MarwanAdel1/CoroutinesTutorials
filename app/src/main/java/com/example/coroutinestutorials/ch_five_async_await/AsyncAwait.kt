import kotlinx.coroutines.*
import java.io.File

fun main() {
//    val launch = GlobalScope.launch {
//        val dataDeferred = getUserByIdFromNetwork(1312)
//        println("Not cancelled")
//        // do something with the data
//
//        val data = dataDeferred.await()
//        println(data)
//    }

//
//    val userId = 992 //992555
//    GlobalScope.launch {
//        val userDeferred = getUserByIdFromNetwork(userId).await()
//        val usersFromFileDeferred = readUsersFromFile("users.txt").await()
//        println("Finding user")
//
//        val userStoredInFile = checkUserExists(userDeferred, usersFromFileDeferred)
//
//        if (userStoredInFile) {
//            println("Found user in file!")
//        }else{
//            println("Not found in file!!!!!!!!")
//        }
//    }

    val scope = CustomScope()

    val userId = 992 //992555
    scope.launch {
        println("Test Deffered: ${getUserByIdFromNetwork(userId, scope).isActive}")
        println("Test Deffered: ${getUserByIdFromNetwork(userId, scope).isCompleted}")

        val userDeferred = getUserByIdFromNetwork(userId, scope).await()
        val usersFromFileDeferred = readUsersFromFile("users.txt", scope).await()
        println("Finding user")

        val userStoredInFile = checkUserExists(userDeferred, usersFromFileDeferred)

        if (userStoredInFile) {
            println("Found user in file!")
        } else {
            println("Not found in file!!!!!!!!")
        }

//
//        val x = getUserByIdFromNetwork(userId, scope)
//        println("Test Deffered: ${x.isActive}")
//        println("Test Deffered: ${x.isCompleted}")
//
//        val userDeferred = x.await()
//        val usersFromFileDeferred = readUsersFromFile("users.txt", scope).await()
//        println("Finding user")
//
//        println("Test Deffered: ${x.isActive}")
//        println("Test Deffered: ${x.isCompleted}")
    }
    
//    scope.onStop()

    Thread.sleep(5000)
    //launch.cancel()

//    while (true) { // stops the program from finishing
//    }
}

private suspend fun getUserByIdFromNetwork(
    userId: Int,
    parentScope: CoroutineScope
) =
    parentScope.async {
        if (!isActive) {
            return@async User(0, "", "")
        }
        println("Retrieving user from network")
        delay(3000)
        println("Still in the coroutine")

        return@async User(userId, "Filip", "Babic") // we simulate the network call
    }

data class User(val id: Int, val name: String, val lastName: String)

private fun readUsersFromFile(
    filePath: String,
    parentScope: CoroutineScope
) =
    parentScope.async {
        println("Reading the file of users")
        delay(1000)

        File(filePath)
            .readLines()
            .asSequence()
            .filter { it.isNotEmpty() }
            .map {
                val data = it.split(" ") // [id, name, lastName]

                if (data.size == 3) data else emptyList()
            }
            .filter {
                it.isNotEmpty()
            }
            .map {
                val userId = it[0].toInt()
                val name = it[1]
                val lastName = it[2]

                User(userId, name, lastName)
            }
            .toList()
    }

private fun checkUserExists(user: User, users: List<User>): Boolean {
    return user in users
}