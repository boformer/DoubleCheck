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

import org.apache.commons.collections4.map.LRUMap;
import org.spongepowered.api.Game;
import org.spongepowered.api.util.command.CommandSource;

import com.github.boformer.doublecheck.api.ConfirmationService;
import com.github.boformer.doublecheck.api.Request;
import com.google.common.base.Optional;

class DoubleCheckService implements ConfirmationService
{
	private LRUMap<CommandSource, Request> activeRequests;
	
	public DoubleCheckService(Game game, Object plugin)
	{
		this.activeRequests = new LRUMap<>(100); //TODO configurable cache size 
	}

	@Override
	public void send(Request question)
	{
		activeRequests.put(question.getRecipient(), question);
		
		question.getRecipient().sendMessage(question.getMessage());
		question.getRecipient().sendMessage("Please /confirm or /deny the action."); //TODO configurable message 
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
