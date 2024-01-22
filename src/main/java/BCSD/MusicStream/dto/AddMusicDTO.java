package BCSD.MusicStream.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class AddMusicDTO {
    private String musicName;
    private Integer categoryId;
    private String singerName;
    private Integer userId;
    private LocalTime musicTime;
    private MultipartFile musicFile;
    private MultipartFile musicIcon;
}
