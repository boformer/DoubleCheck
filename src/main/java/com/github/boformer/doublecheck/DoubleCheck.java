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

/**
 * A static class that initializes the {@link ConfirmationService} instance.
 */
public class DoubleCheck
{
    private static final String CONFIRM_ALIAS = "confirm";
    private static final String DENY_ALIAS = "deny";
    private static final int EXPIRATION_TIME = 15;

    /**
     * Initializes the {@link ConfirmationService}. This method must be called 
     * in the {@code PRE_INITIALIZATION} game state.
     *
     * @param game The game
     */
    public static void initializeService(Game game)
    {
        checkNotNull(game, "game");
        
        ConfirmationService service = new ConfirmationServiceImpl(CONFIRM_ALIAS, DENY_ALIAS, EXPIRATION_TIME);
        
        game.getEventManager().register(service, service);
    }

    /**
     * Gets the {@link ConfirmationService} from the service manager. 
     * 
     * Do not call this method before the {@code POST_INITIALIZATION} game state.
     * 
     * <p>This method throws an exception if the service was not registered with 
     * {@link DoubleCheck#initializeService(Game)}</p>
     * @param game
     * @return
     */
    public static ConfirmationService getService(Game game)
    {
        checkNotNull(game, "game");
        
        return game.getServiceManager().provideUnchecked(ConfirmationService.class);
    }
    
    private DoubleCheck() {}
}
