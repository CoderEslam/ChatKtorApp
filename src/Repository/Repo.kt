package com.doubleclick.Repository

import com.doubleclick.Repository.DatabaseFactory.dbQuery
import com.doubleclick.data.model.Message
import com.doubleclick.data.model.User
import com.doubleclick.data.table.MessageTable
import com.doubleclick.data.table.MessageTable.id
import com.doubleclick.data.table.UserTable
import com.doubleclick.data.table.UserTable.email
import com.doubleclick.data.table.UserTable.hashPassword
import com.doubleclick.data.table.UserTable.name
import com.doubleclick.data.table.UserTable.token
import org.jetbrains.exposed.sql.*

class Repo {

//    =======================User=========================

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert {
                it[UserTable.email] = user.email
                it[UserTable.hashPassword] = user.hashPassword
                it[UserTable.name] = user.name
                it[UserTable.token] = user.token

            }
        }
    }

    suspend fun findUserByEmail(sender: String) = dbQuery {
        UserTable.select {
            UserTable.email.eq(sender)
        }
            .map {
                rowToUser(it)
            }
            .singleOrNull()
    }

    suspend fun getAllUsersInDB(): List<User> = dbQuery {
        UserTable.selectAll().map(::RowToUser)
    }


    private fun ResultRow.toUserInfo() = User(
        this[email],
        this[hashPassword],
        this[name],
        this[token],
    )

    private fun RowToUser(row: ResultRow) = User(
        email = row[UserTable.email],
        hashPassword = row[UserTable.hashPassword],
        name = row[UserTable.name],
        token = row[UserTable.token]
    )


    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            name = row[UserTable.name],
            token = row[UserTable.token]
        )
    }


//    =======================Note=========================

    suspend fun addMessage(message: Message) {
        dbQuery {
            MessageTable.insert {
                it[MessageTable.id] = message.id
                it[MessageTable.date] = message.date
                it[MessageTable.type] = message.type
                it[MessageTable.seen] = message.seen
                it[MessageTable.status] = message.status
                it[MessageTable.sender] = message.sender
                it[MessageTable.receiver] = message.receiver
                it[MessageTable.message] = message.message
            }
        }
    }


    suspend fun getAllMessage(sender: String, receiver: String): List<Message> = dbQuery {
        MessageTable.select {
            MessageTable.sender.eq(sender) and MessageTable.receiver.eq(receiver)
        }.mapNotNull {
            rowToNote(it);
        }
    }

    suspend fun updateMessage(message: Message, sender: String, receiver: String) {
        dbQuery {
            MessageTable.update(
                where = {
                    MessageTable.sender.eq(sender) and MessageTable.receiver.eq(receiver) and MessageTable.id.eq(message.id)
                }
            ) {
                it[MessageTable.message] = message.message
                it[MessageTable.date] = message.date
            }

        }
    }

    suspend fun deleteMessage(id: String, sender: String, receiver: String) {
        dbQuery {

            MessageTable.deleteWhere {
                MessageTable.sender.eq(sender) and MessageTable.id.eq(id) and MessageTable.receiver.eq(
                    receiver
                )
            }

        }
    }

    private fun rowToNote(row: ResultRow?): Message? {
        if (row == null) {
            return null
        }
        return Message(
            id = row[MessageTable.id],
            sender = row[MessageTable.sender],
            receiver = row[MessageTable.receiver],
            message = row[MessageTable.message],
            status = row[MessageTable.status],
            seen = row[MessageTable.seen],
            type = row[MessageTable.type],
            date = row[MessageTable.date]
        )
    }

}