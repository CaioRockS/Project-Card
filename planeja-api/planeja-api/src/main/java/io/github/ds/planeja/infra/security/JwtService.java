
package io.github.ds.planeja.infra.security;

import io.github.ds.planeja.dominio.usuario.UsuarioRepository;
import io.github.ds.planeja.dominio.usuario.model.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class JwtService {

    private static final String ALGORITMO = "HmacSHA256";

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-seconds}")
    private long expirationSeconds;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    // Geração do token
    public String gerarToken(UsuarioEntity usuario) {

        long expiration = Instant.now()
                .plusSeconds(expirationSeconds)
                .getEpochSecond();

        String header = base64Url("""
                {"alg": "HS256", "typ": "JWT"}
                """);

        String payload = base64Url("""
                {"sub": "%s", "nome": "%s", "exp": %d}
                """.formatted(
                usuario.getId().toString(),
                usuario.getNome(),
                expiration
        ));

        String signature = assinar(header + "." + payload);

        return header + "." + payload + "." + signature;
    }

    // Assinatura do token
    private String assinar(String conteudo) {

        try {
            Mac mac = Mac.getInstance(ALGORITMO);

            byte[] bytesSecret = secret.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec chavePrivada =
                    new SecretKeySpec(bytesSecret, ALGORITMO);

            mac.init(chavePrivada);

            byte[] assinatura = mac.doFinal(
                    conteudo.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(assinatura);

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar JWT.", e);
        }
    }

    // Codificação Base64 URL
    private String base64Url(String valor) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }

    // Validação do token
    public boolean isTokenValido(String token) {

        try {

            String[] partesDoToken = token.split("\\.");

            // Verifica se o JWT possui três partes.
            if (partesDoToken.length != 3) {
                return false;
            }

            String header = partesDoToken[0];
            String payload = partesDoToken[1];
            String assinaturaRecebida = partesDoToken[2];

            // Recalcula a assinatura.
            String assinaturaEsperada = assinar(header + "." + payload);

            // Compara as assinaturas.
            if (!MessageDigest.isEqual(
                    assinaturaEsperada.getBytes(StandardCharsets.UTF_8),
                    assinaturaRecebida.getBytes(StandardCharsets.UTF_8))) {

                return false;
            }

            // Decodifica o payload.
            byte[] payloadDecodificado =
                    Base64.getUrlDecoder().decode(payload);

            String payloadString = new String(
                    payloadDecodificado,
                    StandardCharsets.UTF_8
            );

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.readTree(payloadString);

            // Verifica a existência da expiração.
            if (json.get("exp") == null) {
                return false;
            }

            long expiraEm = json.get("exp").asLong();

            long dataHoraAtual = Instant.now().getEpochSecond();

            // Verifica se o token ainda está válido.
            return dataHoraAtual < expiraEm;

        } catch (Exception e) {
            return false;
        }
    }

    // Recupera o usuário através do identificador do JWT.
    public UsuarioEntity getUsuario(String token) {

        try {

            String[] partesToken = token.split("\\.");

            if (partesToken.length != 3) {
                throw new IllegalArgumentException("JWT inválido.");
            }

            byte[] payloadDecodificado =
                    Base64.getUrlDecoder().decode(partesToken[1]);

            String payload = new String(
                    payloadDecodificado,
                    StandardCharsets.UTF_8
            );

            ObjectMapper mapper = new ObjectMapper();

            JsonNode json = mapper.readTree(payload);

            String id = json.get("sub").asString();

            return usuarioRepository.findById(UUID.fromString(id))
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Erro ao tentar resolver usuario."
                            )
                    );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao recuperar usuário do JWT.", e
            );
        }
    }
}
