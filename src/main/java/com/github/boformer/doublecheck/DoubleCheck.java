package com.github.boformer.doublecheck;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DoubleCheck
{
	/**
	 * The default message that is displayed after when a request is sent:
	 * 
	 * <p>{@code "You have 15 seconds to /confirm or /deny the action."}</p>
	 */
	//TODO translations
	public final static Text REQUEST_MESSAGE = Texts.of(TextColors.GOLD, "You have 15 seconds to ", TextColors.RED, "/confirm", 
			TextColors.GOLD, " or ", TextColors.RED, "/deny", TextColors.GOLD, " the action.");
	
	/**
	 * The default alias of the confirm command ({@code "confirm"}).
	 */
	public final static String CONFIRM_ALIAS = "confirm";
	
	/**
	 * The default alias of the deny command ({@code "deny"}).
	 */
	public final static String DENY_ALIAS = "deny";
	
	/**
	 * The default expiration time of a request (15 seconds).
	 */
	public final static int EXPIRATION_TIME = 15;
	
	/**
	 * Initializes a new {@link ConfirmationService} with the default parameters.
	 * 
	 * <p>This is the recommended way to create the service.</p>
	 * 
	 * @param game The game
	 * @param plugin The plugin instance
	 * @return The service
	 */
	public static ConfirmationService initializeService(Game game, Object plugin) 
	{
		return initializeService(game, plugin, REQUEST_MESSAGE, CONFIRM_ALIAS, DENY_ALIAS, EXPIRATION_TIME);
	}
	
	/**
	 * Initializes a new {@link ConfirmationService} with the specified parameters.
	 * 
	 * <p>Only use this if the default message and aliases are not suitable.
	 * Otherwise, use the recommended {@link DoubleCheck#initializeService(Game, Object)} method.</p>
	 * 
	 * @param game The game
	 * @param plugin The plugin instance
	 * @param requestMessage The request message
	 * @param confirmAlias The confirm command alias
	 * @param denyAlias The deny command alias
	 * @param expirationTime The expiration time in seconds
	 * @return The service
	 */
	public static ConfirmationService initializeService(Game game, Object plugin, Text requestMessage, String confirmAlias, String denyAlias, int expirationTime) 
	{
		checkNotNull(game);
		checkNotNull(plugin);
		
		ConfirmationService service = new DoubleCheckService(game, requestMessage, confirmAlias, denyAlias, expirationTime);
		game.getEventManager().register(plugin, service);
		
		return service;
	}
	
	private static class DoubleCheckService implements ConfirmationService 
	{
		private final Cache<CommandSource, Request> requestCache;
		
		private final Game game;
		
		private final Text requestMessage;
		private final String confirmAlias;
		private final String denyAlias;
		
		public DoubleCheckService(Game game, Text requestMessage, String confirmAlias, String denyAlias, int expirationTime)
		{
			this.game = game;
			
			this.requestCache = CacheBuilder.newBuilder()
					.initialCapacity(4)
					.expireAfterWrite(expirationTime, TimeUnit.SECONDS)
					.build();
			
			this.requestMessage = requestMessage;
			this.confirmAlias = confirmAlias;
			this.denyAlias = denyAlias;
		}
		
		@Override
		public void send(CommandSource recipient, Request request)
		{
			this.requestCache.put(recipient, request);
			recipient.sendMessage(request.getMessage(), this.requestMessage);
			
			// Occupy the commands
			game.getEventManager().post(new CommandOccupationEvent(recipient, confirmAlias));
			game.getEventManager().post(new CommandOccupationEvent(recipient, denyAlias));
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
		
		@Subscribe
		public void onCommand(CommandEvent event) 
		{
			boolean confirm = event.getCommand().equalsIgnoreCase(confirmAlias);
			boolean deny = event.getCommand().equalsIgnoreCase(denyAlias);
			
			if(confirm || deny) 
			{
				Request request = requestCache.getIfPresent(event.getSource());
				if(request != null) 
				{
					event.setCancelled(true);
					if(confirm)	request.confirm(event.getSource());
					else request.deny(event.getSource());
				}
			}
		}
		
		@Subscribe
		public void onCommandOccupation(CommandOccupationEvent event) 
		{
			if(event.getCommand().equalsIgnoreCase(confirmAlias) || event.getCommand().equalsIgnoreCase(denyAlias)) 
			{
				requestCache.invalidate(event.getSource());
			}
		}
		
		private class CommandOccupationEvent extends AbstractEvent
		{
			private final CommandSource source;
			private final String command;
			
			public CommandOccupationEvent(CommandSource source, String command)
			{
				super();
				this.source = source;
				this.command = command;
			}
			
			CommandSource getSource()
			{
				return source;
			}
			
			String getCommand()
			{
				return command;
			}
		}
	}
}
