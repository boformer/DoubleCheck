package com.github.boformer.doublecheck.impl;

import com.google.inject.Inject;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;

import com.github.boformer.doublecheck.api.Request;
import com.github.boformer.doublecheck.api.ConfirmationService;
import com.google.common.base.Optional;

@Plugin(id = "DoubleCheck", name = "DoubleCheck", version = "0.1.0")
public class DoubleCheckPlugin implements ConfirmationService
{
	@Inject
	private Game game;

    @Inject
	private Logger logger;
	
	private LRUMap<CommandSource, Request> activeRequests;
	private Message commandMessage;
	
	@Subscribe
	public void onInit(PreInitializationEvent event)
	{
		try
		{
			game.getServiceManager().setProvider(this, ConfirmationService.class, this);
		}
		catch (ProviderExistsException e)
		{
			logger.warn("Confirmation Service was already registered by another plugin :(");
			return;
		}
		
		game.getCommandDispatcher().register(this, new ConfirmCommand(this), "confirm", "ok", "yes"); //TODO configurable aliases
		game.getCommandDispatcher().register(this, new DenyCommand(this), "deny", "cancel", "no");
		
		//TODO initialize config, copy defaults
		
		this.activeRequests = new LRUMap<>(100); //TODO configurable cache size 
		this.commandMessage =  Messages.of("Please /confirm or /deny the action."); //TODO configurable message 
	}

	@Override
	public void send(Request question)
	{
		activeRequests.put(question.getRecipient(), question);
		
		question.getRecipient().sendMessage(question.getMessages());
		question.getRecipient().sendMessage(commandMessage);
	}

	@Override
	public Optional<Request> getActiveRequest(CommandSource receipient)
	{
		return Optional.fromNullable(activeRequests.get(receipient));
	}
	
	@Override
	public void removeActiveRequest(CommandSource receipient)
	{
		activeRequests.remove(receipient);
	}
}
