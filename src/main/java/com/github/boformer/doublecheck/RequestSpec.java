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

import javax.annotation.Nullable;

import com.github.boformer.doublecheck.util.MessageCommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

/**
 * A flexible implementation of {@link RequestExecutor}.
 *
 * <p>Use the {@link RequestSpec#builder()} method to build a new instance.</p>
 */
public class RequestSpec implements RequestExecutor
{

    // default executors
    private static final CommandExecutor DEFAULT_REQUEST_EXECUTOR = new MessageCommandExecutor(Texts.of(TextColors.GOLD,
            "Please confirm or deny this action."));
    private static final CommandExecutor DEFAULT_CONFIRM_EXECUTOR = new MessageCommandExecutor(Texts.of(TextColors.GREEN, "Action confirmed."));
    private static final CommandExecutor DEFAULT_DENY_EXECUTOR = new MessageCommandExecutor(Texts.of(TextColors.RED, "Action denied."));
    private static final CommandExecutor DEFAULT_EXPIRATION_EXECUTOR = new MessageCommandExecutor(
            Texts.of(TextColors.GOLD, "Action request expired!"));

    private final CommandExecutor requestExecutor;
    private final CommandExecutor confirmExecutor;
    private final CommandExecutor denyExecutor;
    private final CommandExecutor expirationExecutor;

    @Override
    public CommandResult request(CommandSource src, CommandContext args) throws CommandException
    {
        if (this.requestExecutor != null) {
            return this.requestExecutor.execute(src, args);
        } else {
            return CommandResult.empty();
        }
    }

    @Override
    public CommandResult confirm(CommandSource src, CommandContext args) throws CommandException
    {
        if (this.confirmExecutor != null) {
            return this.confirmExecutor.execute(src, args);
        } else {
            return CommandResult.empty();
        }
    }

    @Override
    public CommandResult deny(CommandSource src, CommandContext args) throws CommandException
    {
        if (this.denyExecutor != null) {
            return this.denyExecutor.execute(src, args);
        } else {
            return CommandResult.empty();
        }
    }

    @Override
    public CommandResult expire(CommandSource src, CommandContext args) throws CommandException
    {
        if (this.expirationExecutor != null) {
            return this.expirationExecutor.execute(src, args);
        } else {
            return CommandResult.empty();
        }
    }

    private RequestSpec(CommandExecutor requestExecutor, CommandExecutor confirmExecutor, CommandExecutor denyExecutor,
            CommandExecutor expirationExecutor)
    {
        this.requestExecutor = requestExecutor;
        this.confirmExecutor = confirmExecutor;
        this.denyExecutor = denyExecutor;
        this.expirationExecutor = expirationExecutor;
    }

    /**
     * Returns a new blank {@link RequestSpec} builder.
     *
     * @return A request builder
     */
    public static RequestSpec.Builder builder()
    {
        return new RequestSpec.Builder();
    }

    /**
     * Returns a new {@link RequestSpec} builder with a default request,
     * confirm, deny and expiration executor.
     *
     * @return A request builder
     */
    public static RequestSpec.Builder builderWithDefaults()
    {
        return new RequestSpec.Builder()
                .setRequestExecutor(DEFAULT_REQUEST_EXECUTOR)
                .setConfirmExecutor(DEFAULT_CONFIRM_EXECUTOR)
                .setDenyExecutor(DEFAULT_DENY_EXECUTOR)
                .setExpirationExecutor(DEFAULT_EXPIRATION_EXECUTOR);
    }

    public static class Builder
    {

        private CommandExecutor requestExecutor = null;
        private CommandExecutor confirmExecutor = null;
        private CommandExecutor denyExecutor = null;
        private CommandExecutor expirationExecutor = null;

        private Builder() {
        }

        /**
         * Sets the command logic that will be executed when request is sent.
         *
         * @param requestExecutor A command executor
         * @return This builder
         */
        public RequestSpec.Builder setRequestExecutor(@Nullable CommandExecutor requestExecutor)
        {
            this.requestExecutor = requestExecutor;
            return this;
        }

        /**
         * Sets the command logic that will be executed when the recipient
         * confirms the request.
         *
         * @param confirmExecutor A command executor
         * @return This builder
         */
        public RequestSpec.Builder setConfirmExecutor(@Nullable CommandExecutor confirmExecutor)
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
        public RequestSpec.Builder setDenyExecutor(@Nullable CommandExecutor denyExecutor)
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
        public RequestSpec.Builder setExpirationExecutor(@Nullable CommandExecutor expirationExecutor)
        {
            this.expirationExecutor = expirationExecutor;
            return this;
        }

        /**
         * Builds the {@link RequestSpec}.
         *
         * @return The {@link RequestSpec} instance
         */
        public RequestSpec build()
        {
            return new RequestSpec(this.requestExecutor, this.confirmExecutor, this.denyExecutor, this.expirationExecutor);
        }
    }
}
