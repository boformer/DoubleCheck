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
package com.github.boformer.doublecheck.api;

import java.util.Calendar;
import java.util.Date;

import org.spongepowered.api.util.command.CommandSource;

/**
 * The default implementation of the {@link Request} interface.
 * 
 * <p>Plugins can create their own request classes by extending this class.</p>
 */
public abstract class AbstractRequest implements Request
{
	private final CommandSource recipient;
	private final Date expirationDate;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param recipient The recipient of the request (e.g. a player or the console)
	 * @param expirationTime Number of seconds until the request expires
	 */
	public AbstractRequest(CommandSource recipient, int expirationTime)
	{
		this.recipient = recipient;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, expirationTime);
		
		this.expirationDate = calendar.getTime();
	}

	@Override
	public CommandSource getRecipient()
	{
		return recipient;
	}

	@Override
	public boolean isExpired()
	{
		return expirationDate.before(new Date());
	}
}
