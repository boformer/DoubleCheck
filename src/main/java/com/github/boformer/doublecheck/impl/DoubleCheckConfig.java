package com.github.boformer.doublecheck.impl;

import java.util.List;

import ninja.leaping.configurate.objectmapping.Setting;

public class DoubleCheckConfig
{
	@Setting("command-aliases.confirm")
	private List<String> confirmAliases;
	
	@Setting("command-aliases.deny")
	private List<String> denyAliases;
	
	@Setting("request-cache-size")
	private List<String> requestCacheSize;
	
	
}
