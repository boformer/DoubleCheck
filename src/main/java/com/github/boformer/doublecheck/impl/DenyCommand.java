package com.github.boformer.doublecheck.impl;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import com.github.boformer.doublecheck.api.Request;
import com.google.common.base.Optional;

public class DenyCommand implements CommandCallable
{
	private final DoubleCheckPlugin plugin;
	
	public DenyCommand(DoubleCheckPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException
	{
		return Collections.emptyList();
	}

	@Override
	public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException
	{
		if(!testPermission(source)) return false;
		
		Request request = plugin.getActiveRequest(source).orNull();
		
		if(request == null) 
		{
			source.sendMessage(Messages.of("There is no action to deny.")); //TODO configurable message 
			return true;
		}
		else if(request.isExpired()) 
		{
			source.sendMessage(Messages.of("The confirmation request is expired.")); //TODO configurable message 
			plugin.removeActiveRequest(source);
			return true;
		}
		
		request.deny();
		plugin.removeActiveRequest(source);
		
		return true;
	}

	@Override
	public boolean testPermission(CommandSource source)
	{
		return source.hasPermission("doublecheck.command.deny");
	}

	@Override
	public Optional<String> getShortDescription()
	{
		return Optional.fromNullable("Deny a requested action");
	}

	@Override
	public Optional<String> getHelp()
	{
		return Optional.fromNullable("Deny a requested action");
	}

	@Override
	public String getUsage()
	{
		return "";
	}
}
