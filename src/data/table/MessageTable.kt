package com.doubleclick.data.table

import org.jetbrains.exposed.sql.Table

object MessageTable : Table() {

    val id = varchar("id", 512)

    // to connect as forigne key
    val sender = varchar("sender", 512).references(UserTable.email)
    val receiver = text("receiver")
    val message = text("message")
    val status = text("status")
    val seen = text("seen")
    val type = text("type")

    val date = long("date")

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}