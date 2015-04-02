package com.github.boformer.doublecheck;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

class DoubleCheckService implements ConfirmationService
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

        if (confirm || deny)
        {
            Request request = requestCache.getIfPresent(event.getSource());
            if (request != null)
            {
                event.setCancelled(true);
                if (confirm) request.confirm(event.getSource());
                else request.deny(event.getSource());
            }
        }
    }

    @Subscribe
    public void onCommandOccupation(CommandOccupationEvent event)
    {
        if (event.getCommand().equalsIgnoreCase(confirmAlias) || event.getCommand().equalsIgnoreCase(denyAlias)) requestCache.invalidate(event
                .getSource());
    }
}
