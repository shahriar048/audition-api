package com.audition.model;

import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("PMD.MethodArgumentCouldBeFinal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;

    @Nullable
    private List<AuditionComment> comments;

    public AuditionPost(int userId, int id, String title, String body) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

}
