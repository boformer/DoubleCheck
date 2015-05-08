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

import org.spongepowered.api.Game;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import java.util.Collection;

/**
 * The Service that can be used by other plugins to send confirmation requests
 * to a player or the console.
 *
 * <p>Use {@link DoubleCheck#initializeService(Game)} to initialize the service
 * in the {@code PRE_INITIALIZATION} game state.</p>
 *
 * <p>Use {@link DoubleCheck#initializeService(Game)} to get the service in the
 * {@code POST_INITIALIZATION} game state.</p>
 */
public interface ConfirmationService
{

    /**
     * Sends a new request to the specified recipient.
     *
     * @param request The request
     * @param recipient The recipient
     * @param args The arguments
     */
    void send(Request request, CommandSource recipient, CommandContext args);

    /**
     * Gets all active requests. The returned collection is mutable.
     *
     * @return A collection of requests
     */
    Collection<RequestData> getActiveRequests();

    /**
     * Get the version of the service.
     *
     * @return The service version
     */
    String getVersion();
}
