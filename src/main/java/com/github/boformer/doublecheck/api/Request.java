package com.github.boformer.doublecheck.api;

import java.util.List;

import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.util.command.CommandSource;

public interface Request 
{
	CommandSource getRecipient();
	List<Message> getMessages();
	
	boolean isExpired();
	
	void confirm();
	void deny();
}
