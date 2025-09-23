package br.jus.stf.acervo.service.impl.admapi;

import br.jus.stf.acervo.client.admapi.ClientAdmRestService;
import br.jus.stf.acervo.model.dto.SetorDto;
import br.jus.stf.acervo.service.SetorService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class SetorAdmApiServiceImpl implements SetorService {

    private final ClientAdmRestService admApiClient;

    public SetorAdmApiServiceImpl(ClientAdmRestService admApiClient) {
        this.admApiClient = admApiClient;
    }

    @Override
    public SetorDto obtem(Long id) {
        return admApiClient.obtemSetor(id);
    }

    @Cacheable("setores")
    @Override
    public List<SetorDto> listaTodos(Sort sort) {
        return admApiClient.listaTodosSetores();
    }

    @Cacheable("setores")
    @Override
    public List<SetorDto> gabinetesMinistros() {
        return admApiClient.listaGabinetesMinistros();
    }
    

    @Override
    public SetorDto insere(SetorDto setorDto) {
        return setorDto;
    }

    @Override
    public SetorDto atualiza(SetorDto setorDto) {
        return null;
    }

    @Override
    public void remove(Long aLong) {
    }

    @Override
    public List<SetorDto> pesquisar(SetorDto dto) {
        return null;
    }

    @Override
    public List<SetorDto> listaPaginado(Integer pagina, Integer tamanho, Sort sort) {
        return null;
    }
}