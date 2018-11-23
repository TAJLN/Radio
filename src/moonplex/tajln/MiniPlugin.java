package moonplex.tajln;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MiniPlugin implements Listener 
{
	private String _moduleName;
	private JavaPlugin _plugin;
	
	public MiniPlugin(String moduleName, JavaPlugin plugin) 
	{
        _moduleName = moduleName;
        _plugin = plugin;
        
        onEnable();
        
        registerEvents(this);
	}
	
	private void registerEvents(Listener listener)
	{
		_plugin.getServer().getPluginManager().registerEvents(listener, _plugin);
	}
	
	private final void onEnable()
	{
		log("Initializing...");
		enable();
		log("Enabled");
	}

	private void enable() { }
	
	public final String getName()
	{
		return _moduleName;
	}

	private void log(String message)
	{
		System.out.println( _moduleName + "> " + message);
	}
}
