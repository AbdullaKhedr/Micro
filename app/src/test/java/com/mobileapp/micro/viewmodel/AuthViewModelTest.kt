package com.mobileapp.micro.viewmodel

import com.mobileapp.micro.repository.AuthRepositoryTest
import com.mobileapp.micro.repository.UserRepositoryTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthViewModelTest {
	private lateinit var usersRepo: UserRepositoryTest

	@Before
	fun setUp() {
		usersRepo = UserRepositoryTest()
	}

	@Test
	fun `Valid sign in test`(): Unit = runBlocking {
		assertTrue(AuthRepositoryTest(usersRepo).signIn("testuser@gmail.com", "123123"))
	}

	@Test
	fun `Invalid sign in test`(): Unit = runBlocking {
		assertFalse(AuthRepositoryTest(usersRepo).signIn("testuser2@gmail.com", "333333"))
	}
}