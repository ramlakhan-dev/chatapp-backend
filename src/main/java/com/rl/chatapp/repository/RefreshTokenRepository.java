package com.rl.chatapp.repository;

import com.rl.chatapp.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    void deleteByUserId(String userId);
}
