package com.mobileapp.micro.repository

import com.mobileapp.micro.model.Channel

class ChannelsRepositoryTest {
	private val channels = mutableListOf<Channel>()

	fun addChannel(channel: Channel): Int {
		channels.add(channel)
		return channels.size-1
	}

	fun getChannelById(id: String): Channel? {
		return channels.find {it.channelId == id}
	}
}