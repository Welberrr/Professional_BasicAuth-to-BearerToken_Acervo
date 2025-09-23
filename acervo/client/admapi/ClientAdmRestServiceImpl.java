package br.jus.stf.acervo.client.admapi;

import br.jus.stf.acervo.common.exception.AdmApiException;
import br.jus.stf.acervo.model.dto.SetorDto;
import br.jus.stf.acervo.model.dto.UsuarioDto;
import br.jus.stf.acervo.model.dto.UsuarioSimplesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Service
public class ClientAdmRestServiceImpl implements ClientAdmRestService {

    private static final Logger logger = LoggerFactory.getLogger(ClientAdmRestServiceImpl.class);

    private static final String SERVICO_SETOR_URL = "/setores";
    private static final String SERVICO_USUARIO_URL = "/usuarios";
    private static final String SERVICO_FUNCIONARIO_URL = "/funcionarios";

    @Value("${clientRegistrationId}")
    private String clientRegistrationId;

    @Value("${adm.api.host}")
    private String admApiHost;

    private final RestClient restClient;

    public ClientAdmRestServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    public SetorDto obtemSetor(Long id) {
        String endpoint = buildEndpoint(SERVICO_SETOR_URL + "/" + id);
        List<SetorDto> result = executeGetRequestForList(endpoint, null, new ParameterizedTypeReference<>() {});
        return result.stream().findFirst().orElse(null);
    }

    @Override
    public List<SetorDto> listaTodosSetores() {
        String endpoint = buildEndpoint(SERVICO_SETOR_URL);
        return executeGetRequestForList(endpoint, null, new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<SetorDto> listaGabinetesMinistros() {
        String endpoint = buildEndpoint(SERVICO_SETOR_URL + "/gabinetesMinistros");
        return executeGetRequestForList(endpoint, null, new ParameterizedTypeReference<>() {});
    }

    @Override
    public UsuarioDto obtemUsuario(String id) {
        String endpoint = buildEndpoint(SERVICO_USUARIO_URL + "/" + id);
        return executeGetRequestForSingleObject(endpoint, null, new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<UsuarioSimplesDto> listaTodosUsuariosSimples() {
        String endpoint = buildEndpoint(SERVICO_FUNCIONARIO_URL + "/usuarios");
        return executeGetRequestForList(endpoint, null, new ParameterizedTypeReference<>() {});
    }


    private String buildEndpoint(String path) {
        return admApiHost + path;
    }

    private <T> T executeGetRequestForSingleObject(String uri, Map<String, String> params, ParameterizedTypeReference<T> typeReference) {
        UriComponents uriComponents = buildUriComponents(uri, params);
        try {
            return restClient
                    .get()
                    .uri(uriComponents.toUri())
                    .attributes(clientRegistrationId(this.clientRegistrationId))
                    .retrieve()
                    .body(typeReference);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    private <T> List<T> executeGetRequestForList(String uri, Map<String, String> params, ParameterizedTypeReference<List<T>> typeReference) {
        UriComponents uriComponents = buildUriComponents(uri, params);
        try {
            ResponseEntity<List<T>> response = restClient
                    .get()
                    .uri(uriComponents.toUri())
                    .attributes(clientRegistrationId(this.clientRegistrationId))
                    .header("Content-Type", APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(typeReference);
            return Optional.ofNullable(response.getBody()).orElse(List.of());
        } catch (Exception e) {
            handleException(e);
            return List.of();
        }
    }

    private UriComponents buildUriComponents(String uri, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);
        if (params != null && !params.isEmpty()) {
            MultiValueMap<String, String> multiValueParams = new LinkedMultiValueMap<>();
            params.forEach(multiValueParams::add);
            builder.queryParams(multiValueParams);
        }
        return builder.encode().build();
    }

    private void handleException(Exception e) {
        String mensagem = Optional.ofNullable(e.getCause())
                .map(Throwable::getLocalizedMessage)
                .orElse(e.getLocalizedMessage());
        logger.error("Erro ao chamar API externa: {}", mensagem, e);
        throw new AdmApiException(mensagem);
    }
}