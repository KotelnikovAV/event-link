package ru.eventlink.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties("comment.like")
public class LikeCommentConfig {
    private final int maxLikesModalView;
    private final int maxLikesListView;
}
