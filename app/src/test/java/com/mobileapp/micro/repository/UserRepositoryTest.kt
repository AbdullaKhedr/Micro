package com.mobileapp.micro.repository

class UserRepositoryTest {
	private var users = mutableListOf<String>()
	private var passwords = mutableListOf<String>()

	init {
		users.add("testuser@gmail.com")
		passwords.add("123123")
		users.add("testuser2@gmail.com")
		passwords.add("321321")
	}

	fun getUserById(uid: String): List<String>? {
		val uIndex = users.indexOf(uid)
		if(uIndex >= 0)
			return listOf(users[uIndex], passwords[uIndex])
		return null
	}
}