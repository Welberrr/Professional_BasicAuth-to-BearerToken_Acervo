package br.jus.stf.acervo.client.admapi;

import br.jus.stf.acervo.model.dto.SetorDto;
import br.jus.stf.acervo.model.dto.UsuarioDto;
import br.jus.stf.acervo.model.dto.UsuarioSimplesDto;

import java.util.List;

public interface ClientAdmRestService {

    SetorDto obtemSetor(Long id);
    List<SetorDto> listaTodosSetores();
    List<SetorDto> listaGabinetesMinistros();

    UsuarioDto obtemUsuario(String id);

    List<UsuarioSimplesDto> listaTodosUsuariosSimples();
}