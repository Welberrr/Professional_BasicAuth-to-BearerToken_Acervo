package br.jus.stf.acervo.service.impl.admapi;

import br.jus.stf.acervo.client.admapi.ClientAdmRestService;
import br.jus.stf.acervo.model.dto.UsuarioSimplesDto;
import br.jus.stf.acervo.service.UsuarioSimplesCustomService;
import br.jus.stf.acervo.service.UsuarioSimplesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioSimplesAdmApiServiceImpl implements UsuarioSimplesService, UsuarioSimplesCustomService {

    private final ClientAdmRestService admApiClient;

    public UsuarioSimplesAdmApiServiceImpl(ClientAdmRestService admApiClient) {
        this.admApiClient = admApiClient;
    }

    @Cacheable("usuarios_simples") // <-- MELHORIA: Cache para a lista de usuários
    @Override
    public List<UsuarioSimplesDto> listaTodos(Sort sort) {
        // Lógica antiga removida, agora apenas delegamos a chamada
        return admApiClient.listaTodosUsuariosSimples();
    }

    @Override
    public List<UsuarioSimplesDto> pesquisaPorNome(String nome) {
        // A lógica de negócio aqui foi MANTIDA.
        // Ela agora se beneficia da chamada otimizada e cacheada do listaTodos().
        List<UsuarioSimplesDto> lista = listaTodos(Sort.unsorted());

        return lista
                .stream()
                .filter(usuario -> usuario.getUsuario().toUpperCase().contains(nome.toUpperCase()))
                .collect(Collectors.toList());
    }

    // --- MÉTODOS STUB (Mantidos como no original do Acervo) ---

    @Override
    public UsuarioSimplesDto insere(UsuarioSimplesDto usuarioSimplesDto) {
        return null;
    }

    @Override
    public UsuarioSimplesDto atualiza(UsuarioSimplesDto usuarioSimplesDto) {
        return null;
    }

    @Override
    public void remove(String aLong) {
    }

    @Override
    public UsuarioSimplesDto obtem(String id) {
        return null;
    }

    @Override
    public List<UsuarioSimplesDto> listaPaginado(Integer pagina, Integer tamanho, Sort sort) {
        return null;
    }

    @Override
    public List<UsuarioSimplesDto> pesquisar(UsuarioSimplesDto dto) {
        // TODO Auto-generated method stub
        return null;
    }
}