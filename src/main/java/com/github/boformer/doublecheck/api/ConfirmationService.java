package com.github.boformer.doublecheck.api;

import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

public interface ConfirmationService
{
	void send(Request request);
	
	Optional<Request> getActiveRequest(CommandSource receipient);

	void removeActiveRequest(CommandSource receipient);
}
