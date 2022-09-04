package com.doubleclick.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.doubleclick.data.model.User

/* JSON Web Token*/
class JwtService {

    private val issuer = "ChatServer"

    /* System . get environment*/

    /*
    * to see you token
    *https://jwt.io/
     */
    private val jwtSecret = System.getenv("JWT_SECRET") // Eslam
    private val algorithm = Algorithm.HMAC512(jwtSecret)


    val varifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build();
    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("ChatAuth")
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .sign(algorithm);
    }
}