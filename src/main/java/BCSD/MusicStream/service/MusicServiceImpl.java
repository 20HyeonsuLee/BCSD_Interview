package BCSD.MusicStream.service;

import BCSD.MusicStream.domain.*;
import BCSD.MusicStream.dto.Lyrics.RequestLyricsDTO;
import BCSD.MusicStream.dto.music.ModifyMusicDTO;
import BCSD.MusicStream.dto.music.RequestMusicDTO;
import BCSD.MusicStream.dto.music.UploadMusicDTO;
import BCSD.MusicStream.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService{
    private final MusicRepository musicRepository;
    private final LyricsRepository lyricsRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository userRepository;

    private static final String MUSIC_SOUND_DIR = "src/main/resources/static/musicSound/";
    private static final String MUSIC_IMAGE_DIR = "src/main/resources/static/musicImage/";
    @Override
    public List<RequestMusicDTO> getMusicByMusicNameOrSingerName(String targetText) throws MalformedURLException {
        List<Music> musicList = musicRepository.findByNameContainingOrSingerNameContaining(targetText, targetText);
        List<RequestMusicDTO> musicDTOList = new ArrayList<>(musicList.size());
        for(Music music: musicList) {
            String musicSoundUrl = MUSIC_SOUND_DIR + music.getSoundFileName();
            String musicImageUrl = MUSIC_IMAGE_DIR + music.getImageFileName();
            musicDTOList.add(RequestMusicDTO.builder()
                    .id(music.getId())
                    .name(music.getName())
                    .singerName(music.getSingerName())
                    .duration(music.getDuration())
                    .imageFilePath(musicImageUrl)
                    .soundFilePath(musicSoundUrl)
                    .build());
        }
        return musicDTOList;
    }

    @Override
    public RequestLyricsDTO getLyricsByMusicId(Integer musicId) {
        Lyrics lyrics = lyricsRepository.findById(musicId.longValue()).orElseThrow(() -> new EntityNotFoundException());
        return RequestLyricsDTO.builder()
                .lyrics(lyrics.getContents())
                .build();
    }

    @Override
    public Integer addMusic(UploadMusicDTO uploadMusicDTO, Integer memberId) throws IOException, UnsupportedAudioFileException {
        Category category = categoryRepository.findById(uploadMusicDTO.getCategoryId().longValue()).orElseThrow(() -> new  EntityNotFoundException("해당 id의 카테고리를 찾지 못했습니다."));
        Member member = userRepository.findById(memberId.longValue()).orElseThrow(() -> new  EntityNotFoundException("해당 id의 유저를 찾지 못했습니다."));

        Music music  = Music.builder()
                .name(uploadMusicDTO.getName())
                .singerName(uploadMusicDTO.getSingerName())
                .category(category)
                .member(member)
                .duration(uploadMusicDTO.getDuration())
                .imageFileName("")
                .soundFileName("")
                .build();
        Integer musicId = musicRepository.save(music).getId();
        String soundFileName = uploadMusicDTO.getSoundFile().getOriginalFilename();
        String imageFileName = uploadMusicDTO.getImageFile().getOriginalFilename();
        soundFileName = musicId + soundFileName.substring(soundFileName.indexOf('.'), soundFileName.length());
        imageFileName = musicId + imageFileName.substring(imageFileName.indexOf('.'), imageFileName.length());
        music.setId(musicId);
        music.setImageFileName(imageFileName);
        music.setSoundFileName(soundFileName);
        Path soundFilePath = Paths.get(MUSIC_SOUND_DIR + soundFileName);
        Path imageFilePath = Paths.get(MUSIC_IMAGE_DIR + imageFileName);
        Files.copy(uploadMusicDTO.getSoundFile().getInputStream(), soundFilePath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(uploadMusicDTO.getImageFile().getInputStream(), imageFilePath, StandardCopyOption.REPLACE_EXISTING);
        musicRepository.save(music);
        lyricsRepository.save(Lyrics.builder().contents(uploadMusicDTO.getLyrics()).build());
        return musicId;
    }

    @Override
    public void modifyMusic(ModifyMusicDTO modifyMusicDTO) throws UnsupportedAudioFileException, IOException {
        Category category = categoryRepository.findById(modifyMusicDTO.getCategoryId().longValue()).orElseThrow(() -> new  EntityNotFoundException("해당 id의 카테고리를 찾지 못했습니다."));
        Music music  = Music.builder()
                .name(modifyMusicDTO.getName())
                .singerName(modifyMusicDTO.getSingerName())
                .category(category)
                .build();
        Integer musicId = musicRepository.save(music).getId();
        String soundFileName = modifyMusicDTO.getSoundFile().getName();
        String imageFileName = modifyMusicDTO.getImageFile().getName();
        music.setImageFileName(musicId + imageFileName.substring(imageFileName.indexOf('.'), imageFileName.length()));
        music.setSoundFileName(musicId + soundFileName.substring(soundFileName.indexOf('.'), soundFileName.length()));
    }

    @Override
    public void deleteMusic(Integer musicId, Integer memberId) {
        Music music = musicRepository.findById(musicId.longValue()).orElseThrow(() -> new EntityNotFoundException("음악을 찾을 수 없습니다."));
        if(music.getMember().getId() != memberId) new EntityNotFoundException();
        File soundFile = new File(MUSIC_SOUND_DIR + music.getSoundFileName());
        File imageFile = new File(MUSIC_IMAGE_DIR + music.getImageFileName());
        musicRepository.deleteById(musicId.longValue());
        soundFile.delete();
        imageFile.delete();
    }
}
