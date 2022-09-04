package com.doubleclick.plugins

import com.doubleclick.Repository.DatabaseFactory
import com.doubleclick.Repository.Repo
import com.doubleclick.authentication.JwtService
import com.doubleclick.authentication.hash
import com.doubleclick.routes.UserRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


fun Application.configureRouting() {
    install(Locations) {

    }
    println("Working")
    DatabaseFactory.init()
    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    install(Authentication) {

        jwt("jwt") {
            verifier(jwtService.varifier)
            realm = "Chat Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user // return user
            }

        }
    }


    routing {
        get("/") {
            call.respondText("Hello World!")

        }
        get<MyLocation> {
            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
        }
        // Register nested routes
        get<Type.Edit> {
            call.respondText("Inside $it")
        }
        get<Type.List> {
            call.respondText("Inside $it")
        }

        UserRoutes(db = db, jwtService = jwtService, hashFunction = hashFunction)


        get("/allUser") {
//            val email = call.parameters["email"]
            call.respond(db.getAllUsersInDB().toString());
        }

    }
}

@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@Location("/type/{name}")
data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}
