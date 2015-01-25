package com.github.boformer.doublecheck.api;

import java.util.Calendar;
import java.util.Date;
import org.spongepowered.api.util.command.CommandSource;

public abstract class AbstracRequest implements Request
{
	private final CommandSource receipient;
	private final Date expirationDate;
	
	public AbstracRequest(CommandSource receipient, int expirationTime)
	{
		this.receipient = receipient;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, expirationTime);
		
		this.expirationDate = calendar.getTime();
	}

	@Override
	public CommandSource getReceipient()
	{
		return receipient;
	}

	@Override
	public boolean isExpired()
	{
		return expirationDate.before(new Date());
	}
}
