package com.mobileapp.micro.repository

class AuthRepositoryTest(private var UsersRepo: UserRepositoryTest) {
	fun signIn(username: String, password: String): Boolean {
		val user:List<String>? = UsersRepo.getUserById(username)
		return (user!=null && password == user[1])
	}
}