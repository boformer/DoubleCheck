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

import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class DoubleCheck
{
    /**
     * The default message that is displayed after when a request is sent:
     *
     * <p>{@code "You have 15 seconds to /confirm or /deny the action."}</p>
     */
    //TODO translations
    public final static Text REQUEST_MESSAGE = Texts.of(TextColors.GOLD, "You have 15 seconds to ", TextColors.RED, "/confirm", TextColors.GOLD,
            " or ", TextColors.RED, "/deny", TextColors.GOLD, " the action.");

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
     * Initializes a new {@link ConfirmationService} with the default
     * parameters.
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
     * Initializes a new {@link ConfirmationService} with the specified
     * parameters.
     *
     * <p>Only use this if the default message and aliases are not suitable.
     * Otherwise, use the recommended
     * {@link DoubleCheck#initializeService(Game, Object)} method.</p>
     *
     * @param game The game
     * @param plugin The plugin instance
     * @param requestMessage The request message
     * @param confirmAlias The confirm command alias
     * @param denyAlias The deny command alias
     * @param expirationTime The expiration time in seconds
     * @return The service
     */
    public static ConfirmationService initializeService(Game game, Object plugin, Text requestMessage, String confirmAlias, String denyAlias,
            int expirationTime)
    {
        checkNotNull(game, "game");
        checkNotNull(plugin, "plugin");
        checkNotNull(requestMessage, "requestMessage");
        checkNotNull(confirmAlias, "confirmAlias");
        checkNotNull(denyAlias, "denyAlias");
        checkNotNull(expirationTime, "expirationTime");
        
        ConfirmationService service = new DoubleCheckService(game, requestMessage, confirmAlias, denyAlias, expirationTime);
        game.getEventManager().register(plugin, service);

        return service;
    }
}
