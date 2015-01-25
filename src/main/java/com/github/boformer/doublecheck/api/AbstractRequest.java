package com.github.boformer.doublecheck.api;

import java.util.Calendar;
import java.util.Date;
import org.spongepowered.api.util.command.CommandSource;

public abstract class AbstractRequest implements Request
{
	private final CommandSource recipient;
	private final Date expirationDate;
	
	public AbstractRequest(CommandSource recipient, int expirationTime)
	{
		this.recipient = recipient;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, expirationTime);
		
		this.expirationDate = calendar.getTime();
	}

	@Override
	public CommandSource getRecipient()
	{
		return recipient;
	}

	@Override
	public boolean isExpired()
	{
		return expirationDate.before(new Date());
	}
}
