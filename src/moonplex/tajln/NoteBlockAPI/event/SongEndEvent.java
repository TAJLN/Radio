package moonplex.tajln.NoteBlockAPI.event;

import moonplex.tajln.NoteBlockAPI.model.Song;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import moonplex.tajln.NoteBlockAPI.songplayer.SongPlayer;

/**
 * Called when a Song ends
 * or when no players are listening and auto destroy is enabled
 * @see SongPlayer
 *
 */
public class SongEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private SongPlayer song;

	public SongEndEvent(SongPlayer song) {
		this.song = song;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Returns SongPlayer which {@link Song} ends
	 * @return SongPlayer
	 */
	public SongPlayer getSongPlayer() {
		return song;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}