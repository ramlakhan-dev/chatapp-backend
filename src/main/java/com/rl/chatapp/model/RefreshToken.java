package com.rl.chatapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    private String userId;

    private Instant expiryDate;
}
