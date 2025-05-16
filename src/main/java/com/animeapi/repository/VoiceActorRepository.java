package com.animeapi.repository;

import com.animeapi.model.VoiceActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceActorRepository extends JpaRepository<VoiceActor, Long> {
}
