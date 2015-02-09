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

import java.io.File;

import com.google.inject.Inject;

import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;

import com.github.boformer.doublecheck.api.Request;
import com.github.boformer.doublecheck.api.ConfirmationService;
import com.google.common.base.Optional;

@Plugin(id = "DoubleCheck", name = "DoubleCheck", version = "0.1.0")
public class DoubleCheckPlugin
{
	@Inject
	private Game game;

    @Inject
	private Logger logger;
    
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader configManager;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfig;
	
	private DoubleCheckConfirmationService confirmationService;
	
	@Subscribe
	public void onPreInitialization(PreInitializationEvent event)
	{
		this.confirmationService = new DoubleCheckConfirmationService(game, this);
		
		try
		{
			game.getServiceManager().setProvider(this, ConfirmationService.class, confirmationService);
		}
		catch (ProviderExistsException e)
		{
			logger.warn("Confirmation Service was already registered by another plugin :(");
			
			this.confirmationService = null;
			return;
		}
		
		game.getCommandDispatcher().register(this, new ConfirmCommand(confirmationService), "confirm", "ok", "yes"); //TODO configurable aliases
		game.getCommandDispatcher().register(this, new DenyCommand(confirmationService), "deny", "cancel", "no");
	}
}
