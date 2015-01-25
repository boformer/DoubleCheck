package com.github.boformer.answerme.api;

import java.util.Calendar;
import java.util.Date;
import org.spongepowered.api.util.command.CommandSource;

public abstract class AbstractQuestion implements Question
{
	private final CommandSource receipient;
	private final Date expirationDate;
	
	public AbstractQuestion(CommandSource receipient, int expirationTime)
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
