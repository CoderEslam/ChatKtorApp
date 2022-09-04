package com.doubleclick.routes

import com.doubleclick.Repository.Repo
import com.doubleclick.authentication.JwtService
import com.doubleclick.data.model.LoginRequest
import com.doubleclick.data.model.RegesterRequest
import com.doubleclick.data.model.SimpleResponse
import com.doubleclick.data.model.User
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(REGISTER_REQUEST)
class UserRegisterRoute

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(LOGIN_REQUEST) // -> /v1/users/Login
class UserLoginRoute

@OptIn(KtorExperimentalLocationsAPI::class)
fun Route.UserRoutes(db: Repo, jwtService: JwtService, hashFunction: (String) -> String) {

    post<UserRegisterRoute> {

        val registerRequest = try {
            call.receive<RegesterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "miss some fields"))
            return@post
        }

        try {
            val user = User(
                registerRequest.email,
                hashFunction(registerRequest.password),
                registerRequest.name,
                registerRequest.token
            )
            db.addUser(user)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)));

        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred"))
        }
    }

    // -> /v1/users/Login
    post<UserLoginRoute> {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) { // this happend when i miss some fielde
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "miss some fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)
            if (user == null) { // this happend when user insert wrong email
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "wrong Email ID"))
            } else {
                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Password Incorrect"))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some problem Occurred"))
        }
    }


}