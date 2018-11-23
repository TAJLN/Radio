package moonplex.tajln.NoteBlockAPI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CompatibilityUtils {

	public static final String OBC_DIR = Bukkit.getServer().getClass().getPackage().getName();
	public static final String NMS_DIR = OBC_DIR.replaceFirst("org.bukkit.craftbukkit", "net.minecraft.server");

	public static Class<?> getMinecraftClass(String name) {
		try {
			return Class.forName(NMS_DIR + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getCraftBukkitClass(String name) {
		try {
			return Class.forName(OBC_DIR + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isPost1_12() {
		if (!isSoundCategoryCompatible() || Bukkit.getVersion().contains("1.11")) {
			return false;
		}
		return true;
	}

	protected static boolean isSoundCategoryCompatible() {
		if (Bukkit.getVersion().contains("1.7")
				|| Bukkit.getVersion().contains("1.8")
				|| Bukkit.getVersion().contains("1.9")
				|| Bukkit.getVersion().contains("1.10")) {
			return false;
		} else {
			return true;
		}
	}

	public static void playSound(Player player, Location location, String sound, 
			SoundCategory category, float volume, float pitch) {
		try {
			if (isSoundCategoryCompatible()) {
				Method method = Player.class.getMethod("playSound", Location.class, String.class, 
						Class.forName("org.bukkit.SoundCategory"), float.class, float.class);
				Class<? extends Enum> soundCategory =
						(Class<? extends Enum>) Class.forName("org.bukkit.SoundCategory");
				Enum<?> soundCategoryEnum = Enum.valueOf(soundCategory, category.name());
				method.invoke(player, location, sound, soundCategoryEnum, volume, pitch);
			} else {
				Method method = Player.class.getMethod("playSound", Location.class, 
						String.class, float.class, float.class);
				method.invoke(player, location, sound, volume, pitch);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void playSound(Player player, Location location, Sound sound, 
			SoundCategory category, float volume, float pitch) {
		try {
			if (isSoundCategoryCompatible()) {
				Method method = Player.class.getMethod("playSound", Location.class, Sound.class, 
						Class.forName("org.bukkit.SoundCategory"), float.class, float.class);
				Class<? extends Enum> soundCategory = 
						(Class<? extends Enum>) Class.forName("org.bukkit.SoundCategory");
				Enum<?> soundCategoryEnum = Enum.valueOf(soundCategory, category.name());
				method.invoke(player, location, sound, soundCategoryEnum, volume, pitch);
			} else {
				Method method = Player.class.getMethod("playSound", Location.class, 
						Sound.class, float.class, float.class);
				method.invoke(player, location, sound, volume, pitch);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<CustomInstrument> get1_12Instruments(){
		ArrayList<CustomInstrument> instruments = new ArrayList<>();
		instruments.add(new CustomInstrument((byte) 0, "Guitar", "guitar.ogg"));
		instruments.add(new CustomInstrument((byte) 0, "Flute", "flute.ogg"));
		instruments.add(new CustomInstrument((byte) 0, "Bell", "bell.ogg"));
		instruments.add(new CustomInstrument((byte) 0, "Chime", "icechime.ogg"));
		instruments.add(new CustomInstrument((byte) 0, "Xylophone", "xylobone.ogg"));
		return instruments;
	}
	
}
