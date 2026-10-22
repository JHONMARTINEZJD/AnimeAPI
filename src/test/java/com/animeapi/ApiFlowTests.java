package com.animeapi;

import com.animeapi.model.Anime;
import com.animeapi.model.ERole;
import com.animeapi.model.User;
import com.animeapi.repository.AnimeRepository;
import com.animeapi.repository.RoleRepository;
import com.animeapi.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de punta a punta contra la base real (Postgres en CI); cada prueba se revierte al terminar.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiFlowTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UserRepository users;
    @Autowired RoleRepository roles;
    @Autowired PasswordEncoder encoder;
    @Autowired AnimeRepository animes;

    private String body(Object o) throws Exception {
        return json.writeValueAsString(o);
    }

    private void register(String username, String password) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", username, "email", username + "@test.dev", "password", password))))
                .andExpect(status().isOk());
    }

    private String login(String username, String password) throws Exception {
        String res = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", username, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(res).get("token").asText();
    }

    private String adminToken() throws Exception {
        User admin = new User("admin_test", "admin_test@test.dev", encoder.encode("secret123"));
        admin.setRoles(Set.of(roles.findByName(ERole.ROLE_ADMIN).orElseThrow()));
        users.save(admin);
        return login("admin_test", "secret123");
    }

    @Test
    void registroYLoginDevuelvenToken() throws Exception {
        register("naruto", "secret123");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", "naruto", "password", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void registroIgnoraRolAdminPedidoPorElCliente() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", "itachi", "email", "itachi@test.dev",
                                "password", "secret123", "roles", Set.of("admin")))))
                .andExpect(status().isOk());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", "itachi", "password", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void registroRechazaUsuarioRepetido() throws Exception {
        register("sasuke", "secret123");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", "sasuke", "email", "otro@test.dev", "password", "secret123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroValidaLaEntrada() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", "ab", "email", "no-es-correo", "password", "123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginConClaveIncorrectaDa401() throws Exception {
        register("sakura", "secret123");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("username", "sakura", "password", "otra-clave"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void catalogoEsPublico() throws Exception {
        mvc.perform(get("/api/animes")).andExpect(status().isOk());
    }

    @Test
    void animeInexistenteDa404() throws Exception {
        mvc.perform(get("/api/animes/999999")).andExpect(status().isNotFound());
    }

    @Test
    void crearAnimeSinTokenDa401() throws Exception {
        mvc.perform(post("/api/animes").contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("title", "Bleach"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearAnimeComoUsuarioNormalDa403() throws Exception {
        register("kakashi", "secret123");
        String token = login("kakashi", "secret123");
        mvc.perform(post("/api/animes").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("title", "Bleach"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreaAnimeYQuedaConsultable() throws Exception {
        String token = adminToken();
        String res = mvc.perform(post("/api/animes").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("title", "Bleach", "episodes", 366, "rating", 8.2, "status", "FINISHED"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode created = json.readTree(res);

        mvc.perform(get("/api/animes/" + created.get("id").asLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bleach"))
                .andExpect(jsonPath("$.episodes").value(366));
    }

    @Test
    void ratingFueraDeRangoDa400() throws Exception {
        String token = adminToken();
        mvc.perform(post("/api/animes").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("title", "Bleach", "rating", 11))))
                .andExpect(status().isBadRequest());
    }

    private Long anime(String title) {
        Anime a = new Anime();
        a.setTitle(title);
        return animes.save(a).getId();
    }

    private String userToken(String username) throws Exception {
        register(username, "secret123");
        return login(username, "secret123");
    }

    private long idOf(String path, String token) throws Exception {
        String res = mvc.perform(get(path).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(res).get(0).get("id").asLong();
    }

    @Test
    void resenasPublicasNoExponenDatosPrivadosDelAutor() throws Exception {
        Long animeId = anime("Monster");
        String token = userToken("tenma");
        mvc.perform(post("/api/animes/" + animeId + "/review").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("score", 9, "comment", "Obra maestra"))))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/animes/" + animeId + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username").value("tenma"))
                .andExpect(jsonPath("$[0].user.password").doesNotExist())
                .andExpect(jsonPath("$[0].user.email").doesNotExist())
                .andExpect(jsonPath("$[0].user.roles").doesNotExist());
    }

    @Test
    void nadieEditaNiBorraLaResenaDeOtro() throws Exception {
        Long animeId = anime("Monster");
        String duena = userToken("nina");
        String otro = userToken("johan");
        String res = mvc.perform(post("/api/animes/" + animeId + "/review").header("Authorization", "Bearer " + duena)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("score", 8))))
                .andReturn().getResponse().getContentAsString();
        long reviewId = json.readTree(res).get("id").asLong();

        mvc.perform(put("/api/reviews/" + reviewId).header("Authorization", "Bearer " + otro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("score", 1))))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/reviews/" + reviewId).header("Authorization", "Bearer " + otro))
                .andExpect(status().isForbidden());
    }

    @Test
    void nadieBorraFavoritosNiListaDeOtro() throws Exception {
        Long animeId = anime("Monster");
        String duena = userToken("lotte");
        String otro = userToken("roberto");
        String auth = "Bearer " + duena;
        mvc.perform(post("/api/animes/" + animeId + "/favorite").header("Authorization", auth))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/animes/" + animeId + "/watchlist").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(Map.of("status", "WATCHING"))))
                .andExpect(status().isCreated());

        mvc.perform(delete("/api/favorites/" + idOf("/api/users/favorites", duena)).header("Authorization", "Bearer " + otro))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/watchlist/" + idOf("/api/users/watchlist", duena)).header("Authorization", "Bearer " + otro))
                .andExpect(status().isForbidden());
        // La lista de cada uno solo trae lo suyo.
        mvc.perform(get("/api/users/favorites").header("Authorization", "Bearer " + otro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
