package acc.br.service;

import acc.br.ValidationMovieGroups;
import acc.br.model.Pessoa;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;

@ApplicationScoped
public class PessoaService {

    public Pessoa validate(@Valid
                          @ConvertGroup(to = ValidationMovieGroups.PostVithService.class)
                           Pessoa pessoa) {
        return pessoa;
    }



}