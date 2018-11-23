package moonplex.tajln.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
 
 
 
 
 
@SuppressWarnings("all")
public abstract class SimpleCommand
	extends Command
	implements CommandExecutable, PluginIdentifiableCommand, CommandInfo{
		
	private Plugin plugin;
	private CommandManager commandManager;
	private boolean onlyIngame;
   
	public SimpleCommand(CommandManager commandManager, String name){
		
		super(name);

		this.commandManager = commandManager;

		if (commandManager == null){
			
		try
		{
        throw new RegisterCommandException("failed to register the command " + name);
		}
		catch (RegisterCommandException e)
		{
			e.printStackTrace();
         
 
			return;
		}
    }
     
 
		this.plugin = this.commandManager.getPlugin();
     
 
 
		if (!getClass().isAnnotationPresent(CommandInfo.class)){
			
		try
		{
        throw new InvalidAnnotationException("no annotations found");
		}
		catch (InvalidAnnotationException e)
		{
        e.printStackTrace();
         
 
		return;
		}
    }
     
 
    setDescription(description());
    setUsage(usage());
    setPermission(permission());
    setPermissionMessage(commandManager.NOPERMS);
    setOnlyIngame(onlyIngame());
}

	public boolean execute(CommandSender sender, String command, String[] args)
	{
		boolean success = false;
     
 
		if (!this.plugin.isEnabled())
    {
		try
		{
        throw new CommandException("Unhandled exception while executing command " + command + "in plugin " + this.plugin.getDescription().getFullName() + ": plugin is not enabled!");
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
    }
     
 
	if (!testPermission(sender))
    {
		return true;
    }

    if ((onlyIngame()) && (!(sender instanceof Player)))
    {
		sender.sendMessage(this.commandManager.ONLYINGAME_MESSAGE);
		return true;
    }

    try
    {
		success = onCommand(sender, command, args);
    }
	catch (Throwable ex)
    {
		throw new CommandException("Unhandled exception executing command '" + command + "' in plugin " + this.plugin.getDescription().getFullName(), ex);
    }
     
		if ((!success) && (this.usageMessage.length() > 0)) {
		String[] arrayOfString;
		int j = (arrayOfString = this.usageMessage.replace("<command>", command).split("\n")).length; for (int i = 0; i < j; i++) { String line = arrayOfString[i];
         
			sender.sendMessage(line);
		}
    }
     
		return success;
	}

	public abstract boolean onCommand(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString);
 
	public void addAlias(String alias)
	{
		List<String> aliasez = getAliases();
		aliasez.add(alias);
		setAliases(aliasez);
	}
 
	public void addAliases(String... aliases)
	{
		List<String> aliasez = getAliases();
		String[] arrayOfString; int j = (arrayOfString = aliases).length; for (int i = 0; i < j; i++) { String alias = arrayOfString[i];
       
		aliasez.add(alias);
	}
		setAliases(aliasez);
	}

 
	public void removeAlias(String alias)
	{
		List<String> aliasez = getAliases();
		aliasez.remove(alias);
		setAliases(aliasez);
	}

 
	public void removeAliases()
	{
		List<String> aliasez = new ArrayList<String>();
		setAliases(aliasez);
	}
   
	public boolean isOnlyIngame()
	{
		return this.onlyIngame;
	}
   
	public void setOnlyIngame(boolean onlyIngame)
	{
		this.onlyIngame = onlyIngame;
	}
   
	protected void setPlugin(Plugin plugin)
	{
		this.plugin = plugin;
	}

 
	public Plugin getPlugin()
	{
		return this.plugin;
	}

 
	public CommandInfo getCommandInfo()
	{
		return (CommandInfo)getClass().getAnnotation(CommandInfo.class);
	}
   
 
	public String description()
	{
		return getCommandInfo().description();
	}
   
 
	public String usage()
	{
		return getCommandInfo().usage();
	}
   
 
	public String permission()
	{
		return getCommandInfo().permission();
	}
   
 
	public boolean onlyIngame()
	{
		return getCommandInfo().onlyIngame();
	}
   
 
	public Class<? extends Annotation> annotationType()
	{
		return getCommandInfo().annotationType();
	}
}