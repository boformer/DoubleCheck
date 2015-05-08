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
import org.spongepowered.api.util.command.spec.CommandExecutor;

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

    private final Cache<CommandSource, RequestData> activeRequestByRecipient;

    private final Cache<UUID, RequestData> activeRequestById;

    ConfirmationServiceImpl(String confirmAlias, String denyAlias, int expirationTime)
    {
        this.activeRequestByRecipient = CacheBuilder.newBuilder().initialCapacity(4).build();
        this.activeRequestById = CacheBuilder.newBuilder()
                .initialCapacity(4)
                .expireAfterWrite(expirationTime, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<UUID, RequestData>() {

                    @Override
                    public void onRemoval(RemovalNotification<UUID, RequestData> notification) {
                        RequestData data = notification.getValue();

                        if (notification.getCause() == RemovalCause.EXPIRED)
                        {
                            CommandExecutor executor = data.getRequest().getExpirationExecutor();

                            if (executor != null) {
                                try {
                                    executor.execute(data.getRecipient(), data.getArguments());
                                } catch (CommandException e) {
                                }
                            }
                        }
                        else
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

    private Text formatMessage(UUID requestId)
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
    public Collection<RequestData> getActiveRequests()
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

    @Subscribe(order = Order.LAST)
    public void onInitialization(InitializationEvent event)
    {
        this.game = event.getGame();

        try {
            this.game.getServiceManager().setProvider(this, ConfirmationService.class, this);
        } catch (ProviderExistsException e) {
            return;
        }

        // TODO create a config to edit default values/messages

        this.game.getCommandDispatcher().register(this, new ConfirmationCommand(true), "confirm");
        this.game.getCommandDispatcher().register(this, new ConfirmationCommand(true), "deny");
    }

    @Override
    public void send(Request request, CommandSource recipient, CommandContext args)
    {
        RequestData data = new RequestData(request, recipient, args);

        UUID requestId;
        do {
            requestId = UUID.randomUUID();
        } while (this.activeRequestById.getIfPresent(requestId) != null);

        this.activeRequestByRecipient.put(recipient, data);
        this.activeRequestById.put(requestId, data);

        recipient.sendMessage(request.getMessage(), formatMessage(requestId));
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
            try
            {
                requestId = UUID.fromString(arguments);
            } catch (IllegalArgumentException e) {
            }

            RequestData data;

            if (requestId != null) {
                data = ConfirmationServiceImpl.this.activeRequestById.getIfPresent(requestId);
            } else {
                data = ConfirmationServiceImpl.this.activeRequestByRecipient.getIfPresent(source);
            }

            if (data == null)
            {
                if (requestId != null) {
                    source.sendMessage(NO_REQUEST_EXPIRED_MESSAGE);
                } else {
                    source.sendMessage(NO_REQUEST_GENERIC_MESSAGE);
                }

                return Optional.of(CommandResult.empty());
            }

            CommandExecutor executor = this.confirm ? data.getRequest().getConfirmExecutor() : data.getRequest().getDenyExecutor();

            if (executor == null) {
                return Optional.of(CommandResult.empty());
            }

            return Optional.of(executor.execute(data.getRecipient(), data.getArguments()));
        }

        @Override
        public boolean testPermission(CommandSource source)
        {
            return true;
        }
    }
}
