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
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import static ninja.leaping.configurate.loader.FileConfigurationLoader.UTF8_CHARSET;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.config.ConfigFile;
import org.spongepowered.api.util.event.Order;
import org.spongepowered.api.util.event.Subscribe;

import com.github.boformer.doublecheck.api.ConfirmationService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Plugin(id = "DoubleCheck", name = "DoubleCheck", version = "0.1.0")
public class DoubleCheckPlugin
{
	@Inject
	private Game game;

	@Inject
	private Logger logger;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader configLoader;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private File configFile;

	private DoubleCheckConfirmationService confirmationService;


	@Subscribe(order = Order.PRE)
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
		
		URL fallbackConfigUrl = getClass().getClassLoader().getResource("DoubleCheck.conf");
		ConfigFile config = ConfigFile.parseFile(configFile).withFallback(fallbackConfigUrl);
		config.save(true);
		/*
		try
		{
			if (!configFile.exists()) 
			{
				configFile.createNewFile();
				
				ConfigurationLoader initialConfigLoader = HoconConfigurationLoader.builder()
						.setSource(Resources.asCharSource(fallbackConfigURL, UTF8_CHARSET))
						.setSink(Files.asCharSink(configFile, UTF8_CHARSET)).build();
			
				initialConfigLoader.save(initialConfigLoader.load());
			}
		}
		catch (IOException e)
		{
			logger.error("The default configuration could not be created!", e);
		}
		*/

		game.getCommandDispatcher().register(this, new ConfirmCommand(confirmationService), "confirm", "ok", "yes"); //TODO configurable aliases
		game.getCommandDispatcher().register(this, new DenyCommand(confirmationService), "deny", "cancel", "no");
	}
}
