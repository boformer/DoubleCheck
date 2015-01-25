package com.github.boformer.answerme.api;

import java.util.List;

import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.util.command.CommandSource;

public interface Question 
{
	CommandSource getReceipient();
	List<Message> getMessages();
	
	boolean isExpired();
	
	void onYesAnswer();
	void onNoAnswer();
}
