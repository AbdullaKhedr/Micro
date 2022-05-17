package com.mobileapp.micro.viewmodel

import com.mobileapp.micro.model.Channel
import com.mobileapp.micro.repository.ChannelsRepositoryTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ChannelViewModelTest {
	private lateinit var channelsRepository: ChannelsRepositoryTest

	@Before
	fun setUp() {
		channelsRepository = ChannelsRepositoryTest()
	}

	@Test
	fun `Add channel test`() {
		val channel = Channel("1", "Add channel unit test", "This is just a test channel")
		channelsRepository.addChannel(channel)

		Assert.assertEquals(channelsRepository.getChannelById("1")?.channelName, "Add channel unit test")
	}
}