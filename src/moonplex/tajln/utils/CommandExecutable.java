package moonplex.tajln.utils;

import org.bukkit.command.CommandSender;

public abstract interface CommandExecutable
{
  public abstract boolean onCommand(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString);
}