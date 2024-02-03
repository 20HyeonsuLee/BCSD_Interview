package BCSD.MusicStream.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ModifyMemberInfoDTO {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "BrithDate cannot be blank")
    private LocalDate birthDate;

    @NotNull(message = "AuthorityID cannot be null")
    private Integer authorityId;
}