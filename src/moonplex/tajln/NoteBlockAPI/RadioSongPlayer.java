package moonplex.tajln.NoteBlockAPI;

import org.bukkit.entity.Player;

import moonplex.tajln.NoteBlockAPI.utils.InstrumentUtils;

public class RadioSongPlayer extends SongPlayer {

	public RadioSongPlayer(Song song) {
		super(song);
		makeNewClone(moonplex.tajln.NoteBlockAPI.songplayer.RadioSongPlayer.class);
	}

	public RadioSongPlayer(Song song, SoundCategory soundCategory) {
		super(song, soundCategory);
		makeNewClone(moonplex.tajln.NoteBlockAPI.songplayer.RadioSongPlayer.class);
	}
	
	public RadioSongPlayer(moonplex.tajln.NoteBlockAPI.songplayer.SongPlayer songPlayer) {
		super(songPlayer);
	}

	@Override
	public void playTick(Player player, int tick) {
		byte playerVolume = NoteBlockAPI.getPlayerVolume(player);

		for (Layer layer : song.getLayerHashMap().values()) {
			Note note = layer.getNote(tick);
			if (note == null) {
				continue;
			}

			float volume = (layer.getVolume() * (int) this.volume * (int) playerVolume) / 1000000F;
			float pitch = NotePitch.getPitch(note.getKey() - 33);

			if (InstrumentUtils.isCustomInstrument(note.getInstrument())) {
				CustomInstrument instrument = song.getCustomInstruments()
						[note.getInstrument() - InstrumentUtils.getCustomInstrumentFirstIndex()];

				if (instrument.getSound() != null) {
					CompatibilityUtils.playSound(player, player.getEyeLocation(),
							instrument.getSound(),
							this.soundCategory, volume, pitch);
				} else {
					CompatibilityUtils.playSound(player, player.getEyeLocation(),
							instrument.getSoundfile(),
							this.soundCategory, volume, pitch);
				}
			} else {
				CompatibilityUtils.playSound(player, player.getEyeLocation(),
						InstrumentUtils.getInstrument(note.getInstrument()), this.soundCategory, volume, pitch);
			}
		}
	}
	
}
