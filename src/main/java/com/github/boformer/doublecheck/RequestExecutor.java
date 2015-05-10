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

import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

/**
 * Stores the command logic of the request that will be executed when the
 * request is sent, confirmed, denied or when the request expired.
 */
public interface RequestExecutor {

    /**
     * Executed when the request is being sent.
     *
     * @return A command executor
     * @throws CommandException If a user-facing error occurs while executing
     *         this command
     */
    CommandResult request(CommandSource src, CommandContext args) throws CommandException;

    /**
     * Executed when the recipient confirms the request.
     *
     * @return A command result
     * @throws CommandException If a user-facing error occurs while executing
     *         this command
     */
    CommandResult confirm(CommandSource src, CommandContext args) throws CommandException;

    /**
     * Executed when the recipient denies the request.
     *
     * @return A command result
     * @throws CommandException If a user-facing error occurs while executing
     *         this command
     */
    CommandResult deny(CommandSource src, CommandContext args) throws CommandException;

    /**
     * Executed when the request expires.
     *
     * @return A command result
     * @throws CommandException If a user-facing error occurs while executing
     *         this command
     */
    CommandResult expire(CommandSource src, CommandContext args) throws CommandException;
}
