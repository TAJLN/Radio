package moonplex.tajln.NoteBlockAPI;

import moonplex.tajln.NoteBlockAPI.model.Sound;

public class CustomInstrument{
	
	private byte index;
	private String name;
	private String soundFileName;
	private org.bukkit.Sound sound;

	public CustomInstrument(byte index, String name, String soundFileName) {
		this.index = index;
		this.name = name;
		this.soundFileName = soundFileName.replaceAll(".ogg", "");
		if (this.soundFileName.equalsIgnoreCase("pling")){
			this.sound = Sound.NOTE_PLING.bukkitSound();
		}
	}

	public byte getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public org.bukkit.Sound getSound() {
		return sound;
	}

	public String getSoundfile() {
		return soundFileName;
	}

}
