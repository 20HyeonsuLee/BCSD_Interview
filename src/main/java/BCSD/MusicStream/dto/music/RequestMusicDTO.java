package BCSD.MusicStream.dto.music;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;

@Getter
@Builder
public class RequestMusicDTO {
    private Integer id;
    private String name;
    private String singerName;
    private Time duration;
    private String imageFilePath;
    private String soundFilePath;
}