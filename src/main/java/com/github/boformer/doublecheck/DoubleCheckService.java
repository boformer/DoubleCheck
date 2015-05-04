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
package com.github.boformer.doublecheck;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Subscribe;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The default implementation of {@link ConfirmationService}.
 * 
 * <p>Use {@link DoubleCheck#initializeService(Game, Object)} to get an instance of the service.</p>
 */
public class DoubleCheckService implements ConfirmationService
{
    private final Cache<CommandSource, Request> requestCache;

    private final Game game;

    private final Text requestMessage;
    private final String confirmAlias;
    private final String denyAlias;

    DoubleCheckService(Game game, Text requestMessage, String confirmAlias, String denyAlias, int expirationTime)
    {
        this.game = game;

        requestCache = CacheBuilder.newBuilder().initialCapacity(4).expireAfterWrite(expirationTime, TimeUnit.SECONDS).build();

        this.requestMessage = requestMessage;
        this.confirmAlias = confirmAlias;
        this.denyAlias = denyAlias;
    }

    @Override
    public void send(CommandSource recipient, Request request)
    {
        requestCache.put(recipient, request);
        recipient.sendMessage(request.getMessage(), requestMessage);

        // Occupy the commands (Other DoubleCheck instances will listen for the event)
        game.getEventManager().post(new CommandOccupationEvent(this, recipient, confirmAlias));
        game.getEventManager().post(new CommandOccupationEvent(this, recipient, denyAlias));
    }

    @Override
    public Optional<Request> getActiveRequest(CommandSource recipient)
    {
        return Optional.fromNullable(requestCache.getIfPresent(recipient));
    }

    @Override
    public void removeActiveRequest(CommandSource recipient)
    {
        requestCache.invalidate(recipient);
    }

    @Subscribe(order = Order.LATE)
    public void onCommand(CommandEvent event)
    {
        boolean confirm = event.getCommand().equalsIgnoreCase(confirmAlias);
        boolean deny = event.getCommand().equalsIgnoreCase(denyAlias);

        if (confirm || deny)
        {
            Request request = requestCache.getIfPresent(event.getSource());
            if (request != null)
            {
                event.setCancelled(true);
                if (confirm) request.confirm(event.getSource());
                else request.deny(event.getSource());
                
                // Remove the request from cache
                requestCache.invalidate(event.getSource());
            }
        }
    }

    @Subscribe
    public void onCommandOccupation(CommandOccupationEvent event)
    {
        if (event.getService() == this) return;
        
    	if (event.getCommand().equalsIgnoreCase(confirmAlias) || event.getCommand().equalsIgnoreCase(denyAlias)) requestCache.invalidate(event
                .getSource());
    }
}
