package moonplex.tajln.NoteBlockAPI;

import java.util.HashMap;

public class Layer{

	private HashMap<Integer, Note> notesAtTicks = new HashMap<Integer, Note>();
	private byte volume = 100;
	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Note getNote(int tick) {
		return notesAtTicks.get(tick);
	}

	public void setNote(int tick, Note note) {
		notesAtTicks.put(tick, note);
	}

	public byte getVolume() {
		return volume;
	}

	public void setVolume(byte volume) {
		this.volume = volume;
	}

	public HashMap<Integer, Note> getHashMap() {
		return notesAtTicks;
	}

	public void setHashMap(HashMap<Integer, Note> hashMap) {
		this.notesAtTicks = hashMap;
	}
	
}
