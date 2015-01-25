package com.github.boformer.answerme.impl;

import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;

import com.github.boformer.answerme.api.Question;
import com.github.boformer.answerme.api.QuestionService;
import com.google.common.base.Optional;

@Plugin(id = "AnswerMe", name = "AnswerMe", version = "0.1.0")
public class AnswerMePlugin implements QuestionService
{
	//TODO injection
	private Game game;
	private Logger logger;
	
	private LRUMap<CommandSource, Question> activeQuestions;
	private Message answerCmdMessage;
	
	@Subscribe
	public void onInit(PreInitializationEvent event)
	{
		try
		{
			game.getServiceManager().setProvider(this, QuestionService.class, this);
		}
		catch (ProviderExistsException e)
		{
			logger.warn("Question Service was already registered by another plugin :(");
			return;
		}
		
		//TODO initialize config, copy defaults
		
		this.activeQuestions = new LRUMap<>(100); //TODO configurable cache size 
		this.answerCmdMessage =  Messages.of("Confirm with /yes or cancel with /no"); //TODO configurable message 
	}

	@Override
	public void ask(Question question)
	{
		activeQuestions.put(question.getReceipient(), question);
		
		question.getReceipient().sendMessage(question.getMessages());
		question.getReceipient().sendMessage(answerCmdMessage);
	}

	@Override
	public Optional<Question> getActiveQuestion(CommandSource receipient)
	{
		return Optional.fromNullable(activeQuestions.get(receipient));
	}
}
