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

import static com.google.common.base.Preconditions.checkNotNull;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import javax.annotation.Nullable;

/**
 * A confirmation request that can be passed to a {@link ConfirmationService} to
 * send it to the recipient.
 *
 * <p>Plugins can create their own request classes which implement this
 * interface.</p>
 */
public class Request
{

    // default messages
    private static final Text DEFAULT_MESSAGE = Texts.of(TextColors.GOLD, "Please confirm or deny this action.");
    private static final Text DEFAULT_CONFIRM_MESSAGE = Texts.of(TextColors.GREEN, "Action confirmed.");
    private static final Text DEFAULT_DENY_MESSAGE = Texts.of(TextColors.RED, "Action denied.");
    private static final Text DEFAULT_EXPIRATION_MESSAGE = Texts.of(TextColors.GOLD, "Action request expired!");

    // default executors
    private static final CommandExecutor DEFAULT_CONFIRM_EXECUTOR = new CommandExecutor()
    {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
        {
            src.sendMessage(DEFAULT_CONFIRM_MESSAGE);
            return CommandResult.empty();
        }
    };
    private static final CommandExecutor DEFAULT_DENY_EXECUTOR = new CommandExecutor()
    {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
        {
            src.sendMessage(DEFAULT_DENY_MESSAGE);
            return CommandResult.empty();
        }
    };
    private static final CommandExecutor DEFAULT_EXPIRATION_EXECUTOR = new CommandExecutor()
    {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
        {
            src.sendMessage(DEFAULT_EXPIRATION_MESSAGE);
            return CommandResult.empty();
        }
    };

    private final Text message;
    private final CommandExecutor confirmExecutor;
    private final CommandExecutor denyExecutor;
    private final CommandExecutor expirationExecutor;

    /**
     * Gets the message that will be sent to the recipient when the request is
     * sent.
     *
     * @return The message
     */
    public Text getMessage()
    {
        return this.message;
    }

    /**
     * The command logic that will be executed when the recipient confirms the
     * request.
     *
     * @return A command executor
     */
    public CommandExecutor getConfirmExecutor()
    {
        return this.confirmExecutor;
    }

    /**
     * The command logic that will be executed when the recipient denies the
     * request.
     *
     * @return A command executor
     */
    public CommandExecutor getDenyExecutor()
    {
        return this.denyExecutor;
    }

    /**
     * The command logic that will be executed when the request expires.
     *
     * @return A command executor
     */
    public CommandExecutor getExpirationExecutor()
    {
        return this.expirationExecutor;
    }
    
    private Request(Text message, CommandExecutor confirmExecutor, CommandExecutor denyExecutor, CommandExecutor expirationExecutor)
    {
        this.message = message;
        this.confirmExecutor = confirmExecutor;
        this.denyExecutor = denyExecutor;
        this.expirationExecutor = expirationExecutor;
    }

    /**
     * Returns a new blank request builder.
     *
     * @return A request builder
     */
    public Request.Builder builder()
    {
        return new Request.Builder();
    }

    /**
     * Returns a new request builder with a default confirm, deny and expiration
     * executor and a default message.
     *
     * @return A request builder
     */
    public Request.Builder builderWithDefaults()
    {
        return new Request.Builder()
                .setMessage(DEFAULT_MESSAGE)
                .setConfirmExecutor(DEFAULT_CONFIRM_EXECUTOR)
                .setDenyExecutor(DEFAULT_DENY_EXECUTOR)
                .setExpirationExecutor(DEFAULT_EXPIRATION_EXECUTOR);
    }

    public class Builder
    {

        private Text message = null;
        private CommandExecutor confirmExecutor = null;
        private CommandExecutor denyExecutor = null;
        private CommandExecutor expirationExecutor = null;

        /**
         * Sets the message of the request.
         *
         * @param message A message
         * @return This builder
         */
        public Request.Builder setMessage(Text message)
        {
            checkNotNull(message, "message");
            this.message = message;
            return this;
        }

        /**
         * Sets the command logic that will be executed when the recipient
         * confirms the request.
         *
         * @param confirmExecutor A command executor
         * @return This builder
         */
        public Request.Builder setConfirmExecutor(@Nullable CommandExecutor confirmExecutor)
        {
            this.confirmExecutor = confirmExecutor;
            return this;
        }

        /**
         * Sets the command logic that will be executed when the recipient
         * denies the request.
         *
         * @param confirmExecutor A command executor
         * @return This builder
         */
        public Request.Builder setDenyExecutor(@Nullable CommandExecutor denyExecutor)
        {
            this.denyExecutor = denyExecutor;
            return this;
        }

        /**
         * Sets the command logic that will be executed when this request
         * expires.
         *
         * @param confirmExecutor A command executor
         * @return This builder
         */
        public Request.Builder setExpirationExecutor(@Nullable CommandExecutor expirationExecutor)
        {
            this.expirationExecutor = expirationExecutor;
            return this;
        }

        /**
         * Builds the request.
         *
         * @return The request instance
         */
        public Request build()
        {
            checkNotNull(this.message, "message");
            return new Request(this.message, this.confirmExecutor, this.denyExecutor, this.expirationExecutor);
        }
    }
}
