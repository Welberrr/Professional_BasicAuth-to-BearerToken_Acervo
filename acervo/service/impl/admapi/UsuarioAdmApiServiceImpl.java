package br.jus.stf.acervo.service.impl.admapi;

import br.jus.stf.acervo.client.admapi.ClientAdmRestService;
import br.jus.stf.acervo.model.dto.UsuarioDto;
import br.jus.stf.acervo.service.UsuarioService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class UsuarioAdmApiServiceImpl implements UsuarioService {

    private final ClientAdmRestService admApiClient;

    public UsuarioAdmApiServiceImpl(ClientAdmRestService admApiClient) {
        this.admApiClient = admApiClient;
    }

    @Cacheable(value = "usuarios", key = "#id") 
    @Override
    public UsuarioDto obtem(String id) {
        return admApiClient.obtemUsuario(id);
    }
    
    
    @Override
    public List<UsuarioDto> listaTodos(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UsuarioDto> listaPaginado(Integer pagina, Integer tamanho, Sort sort) {
        return null;
    }

    @Override
    public UsuarioDto insere(UsuarioDto usuario) {
        // TODO Auto-generated method stub
        return usuario;
    }

    @Override
    public UsuarioDto atualiza(UsuarioDto usuario) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(String id) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<UsuarioDto> pesquisar(UsuarioDto dto) {
        // TODO Auto-generated method stub
        return null;
    }
}