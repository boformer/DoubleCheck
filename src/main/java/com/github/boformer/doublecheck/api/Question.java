package com.github.boformer.doublecheck.api;

import java.util.List;

import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.util.command.CommandSource;

public interface Question 
{
	CommandSource getReceipient();
	List<Message> getMessages();
	
	boolean isExpired();
	
	void onConfirmation();
	void onDenial();
}
