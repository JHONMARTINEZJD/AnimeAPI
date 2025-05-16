package com.animeapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "characters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;

    @ManyToMany
    @JoinTable(name = "character_voice_actors",
               joinColumns = @JoinColumn(name = "character_id"),
               inverseJoinColumns = @JoinColumn(name = "voice_actor_id"))
    private Set<VoiceActor> voiceActors = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    public Set<VoiceActor> getVoiceActors() {
        return voiceActors;
    }

    public void setVoiceActors(Set<VoiceActor> voiceActors) {
        this.voiceActors = voiceActors;
    }
}
