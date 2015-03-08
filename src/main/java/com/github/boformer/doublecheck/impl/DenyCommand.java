/*
 * This file is part of DoubleCheck, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Felix Schmidt <http://homepage.rub.de/Felix.Schmidt-c2n/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.boformer.doublecheck.impl;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import com.github.boformer.doublecheck.api.ConfirmationService;
import com.github.boformer.doublecheck.api.Request;
import com.google.common.base.Optional;

class DenyCommand implements CommandCallable
{
	private final DoubleCheckService confirmationService;
	
	public DenyCommand(DoubleCheckService confirmationService)
	{
		this.confirmationService = confirmationService;
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
		
		Request request = confirmationService.getActiveRequest(source).orNull();
		
		if(request == null) 
		{
			source.sendMessage(Messages.of("There is no action to deny.")); //TODO configurable message 
			return true;
		}
		else if(request.isExpired()) 
		{
			source.sendMessage(Messages.of("The confirmation request is expired.")); //TODO configurable message 
			confirmationService.removeActiveRequest(source);
			return true;
		}
		
		request.deny();
		confirmationService.removeActiveRequest(source);
		
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
