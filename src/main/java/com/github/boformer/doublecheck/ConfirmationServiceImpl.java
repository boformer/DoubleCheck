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

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation of {@link ConfirmationService}.
 *
 * <p>Use {@link DoubleCheck#initializeService(Game)} to initialize the service
 * in the {@code PRE_INITIALIZATION} game state.</p>
 *
 * <p>Use {@link DoubleCheck#initializeService(Game)} to get the service in the
 * {@code POST_INITIALIZATION} game state.</p>
 */
public class ConfirmationServiceImpl implements ConfirmationService, PluginContainer
{

    // fake plugin metadata
    private static final String VERSION = "2.0.0";

    private static final String NAME = "DoubleCheck";
    private static final String ID = "DoubleCheck";

    // messages
    private static final Text NO_REQUEST_EXPIRED_MESSAGE = Texts.of(TextColors.RED, "The confirmation request expired!");
    private static final Text NO_REQUEST_GENERIC_MESSAGE = Texts.of(TextColors.RED, "No active confirmation request!");

    private static final Optional<Text> CONFIRM_DESC = Optional.<Text>of(Texts.of("Confirm an action"));
    private static final Optional<Text> DENY_DESC = Optional.<Text>of(Texts.of("Deny an action"));

    // service variables
    private Game game;
    private final String confirmAlias;
    private final String denyAlias;

    private final Cache<CommandSource, Request> activeRequestByRecipient;

    private final Cache<UUID, Request> activeRequestById;

    ConfirmationServiceImpl(String confirmAlias, String denyAlias, int expirationTime)
    {
        this.activeRequestByRecipient = CacheBuilder.newBuilder().initialCapacity(4).build();
        this.activeRequestById = CacheBuilder.newBuilder()
                .initialCapacity(4)
                .expireAfterWrite(expirationTime, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<UUID, Request>() {

                    @Override
                    public void onRemoval(RemovalNotification<UUID, Request> notification) {
                        Request data = notification.getValue();

                        if (notification.getCause() == RemovalCause.EXPIRED)
                        {
                            try {
                                data.getExecutor().expire(data.getRecipient(), data.getArguments());
                            } catch (CommandException e) {}
                        }
                        else // removed by confirm/deny
                        {
                            if (data == ConfirmationServiceImpl.this.activeRequestByRecipient.getIfPresent(data.getRecipient()))
                            {
                                ConfirmationServiceImpl.this.activeRequestByRecipient.invalidate(data.getRecipient());
                            }
                        }
                    }
                }).build();

        this.confirmAlias = confirmAlias;
        this.denyAlias = denyAlias;
    }

    private Text formatActionMessage(UUID requestId)
    {
        // TODO make this message customizable (config)
        return Texts.of(
                "    ",
                Texts.builder("[Confirm]").style(TextStyles.UNDERLINE)
                        .onClick(TextActions.runCommand(this.confirmAlias + " " + requestId.toString())).build(),
                "    ",
                Texts.builder("[Deny]").style(TextStyles.UNDERLINE).onClick(TextActions.runCommand(this.denyAlias + " " + requestId.toString()))
                        .build());
    }

    @Override
    public Collection<Request> getActiveRequests()
    {
        return this.activeRequestById.asMap().values();
    }

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public Object getInstance()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getVersion()
    {
        return VERSION;
    }

    @Subscribe(order = Order.LAST) // Last to allow future version to override this implementation
    public void onInitialization(InitializationEvent event)
    {
        this.game = event.getGame();

        try {
            this.game.getServiceManager().setProvider(this, ConfirmationService.class, this);
        } catch (ProviderExistsException e) {
            return;
        }

        // TODO create a config to edit default values/messages and aliases

        this.game.getCommandDispatcher().register(this, new ConfirmationCommand(false), "confirm");
        this.game.getCommandDispatcher().register(this, new ConfirmationCommand(true), "deny");
    }

    @Override
    public Optional<Request> send(RequestExecutor requestExecutor, CommandSource recipient, CommandContext args)
    {
    	Request request = new RequestImpl(requestExecutor, recipient, args);
    	
        // find unused request ID
        UUID requestId;
        do {
            requestId = UUID.randomUUID();
        } while (this.activeRequestById.getIfPresent(requestId) != null);
    	
        // display request
        try {
            requestExecutor.request(recipient, args);
        } catch (CommandException e) {
            // cancel the request if a command exception occurs
            recipient.sendMessage(e.getText());
            return Optional.absent();
        }
        recipient.sendMessage(formatActionMessage(requestId));
        
    	// add to cache
        this.activeRequestByRecipient.put(recipient, request);
        this.activeRequestById.put(requestId, request);

        return Optional.of(request);
    }

    private class ConfirmationCommand implements CommandCallable
    {
        private final boolean confirm;

        private ConfirmationCommand(boolean confirm)
        {
            this.confirm = confirm;
        }

        @Override
        public Optional<Text> getHelp(CommandSource source)
        {
            return this.confirm ? CONFIRM_DESC : DENY_DESC;
        }

        @Override
        public Optional<Text> getShortDescription(CommandSource source)
        {
            return this.confirm ? CONFIRM_DESC : DENY_DESC;
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException
        {
            return Collections.<String>emptyList();
        }

        @Override
        public Text getUsage(CommandSource source)
        {
            return Texts.of();
        }

        @Override
        public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException
        {
            UUID requestId = null;
            Request request = null;
            try
            {
                requestId = UUID.fromString(arguments);
                request = ConfirmationServiceImpl.this.activeRequestById.getIfPresent(requestId);
            } catch (IllegalArgumentException e) {
                // ignore argument, search for the latest request
                request = ConfirmationServiceImpl.this.activeRequestByRecipient.getIfPresent(source);
            }

            if (request == null)
            {
                if (requestId != null) {
                    source.sendMessage(NO_REQUEST_EXPIRED_MESSAGE);
                } else {
                    source.sendMessage(NO_REQUEST_GENERIC_MESSAGE);
                }

                return Optional.of(CommandResult.empty());
            }
            
            // delete the request from cache
            activeRequestById.asMap().values().remove(request);
            
            // execute the confirm/deny command logic
            RequestExecutor executor = request.getExecutor();
            if(this.confirm) return Optional.of(executor.confirm(request.getRecipient(), request.getArguments()));
            else return Optional.of(executor.deny(request.getRecipient(), request.getArguments()));
        }

        @Override
        public boolean testPermission(CommandSource source)
        {
            return true;
        }
    }
    
    private class RequestImpl implements Request
    {

        private final RequestExecutor request;
        private final CommandSource recipient;
        private final CommandContext arguments;

        RequestImpl(RequestExecutor request, CommandSource recipient, CommandContext arguments)
        {
            this.request = request;
            this.recipient = recipient;
            this.arguments = arguments;
        }

        @Override
        public CommandContext getArguments()
        {
            return this.arguments;
        }

        @Override
        public CommandSource getRecipient()
        {
            return this.recipient;
        }

        @Override
        public RequestExecutor getExecutor()
        {
            return this.request;
        }

    }
}
