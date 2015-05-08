package com.github.boformer.doublecheck;

import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

/**
 * Stores the data of a sent confirmation request.
 */
public class RequestData
{

    private final Request request;
    private final CommandSource recipient;
    private final CommandContext arguments;

    RequestData(Request request, CommandSource recipient, CommandContext arguments)
    {
        this.request = request;
        this.recipient = recipient;
        this.arguments = arguments;
    }

    /**
     * Returns the passed arguments.
     *
     * @return The arguments
     */
    public CommandContext getArguments()
    {
        return this.arguments;
    }

    /**
     * Returns the recipient of the request.
     *
     * @return The recipient
     */
    public CommandSource getRecipient()
    {
        return this.recipient;
    }

    /**
     * Returns the request object.
     *
     * @return The request
     */
    public Request getRequest()
    {
        return this.request;
    }
}
