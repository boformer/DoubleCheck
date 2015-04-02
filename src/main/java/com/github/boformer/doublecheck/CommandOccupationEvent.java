package com.github.boformer.doublecheck;

import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.util.command.CommandSource;

class CommandOccupationEvent extends AbstractEvent
{
    private final CommandSource source;
    private final String command;

    public CommandOccupationEvent(CommandSource source, String command)
    {
        super();
        this.source = source;
        this.command = command;
    }

    public CommandSource getSource()
    {
        return source;
    }

    public String getCommand()
    {
        return command;
    }
}
