package acc.br.resource;

import acc.br.service.PessoaService;
import acc.br.ValidationMovieGroups;
import acc.br.model.Pessoa;
import acc.br.repository.PessoaRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class PessoaResource {

    @Inject
    Validator validator;

    @Inject
    PessoaService pessoaService;

    @Inject
    PessoaRepository pessoaRepository;


    @GET
    public Response list()  {
        List<Pessoa> list = pessoaRepository.findAll().list();
        return Response.ok(list).build();
    }

    @POST
    @Transactional
    public Response createPessoa(Pessoa pessoa) {

        Set<ConstraintViolation<Pessoa>> validate = validator.validate(pessoa);
        if (validate.isEmpty()) {
            pessoaRepository.persist(pessoa);
            return Response.ok(pessoa).build();
        } else {
            String violations = validate.stream().map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Status.BAD_REQUEST).entity(violations).build();
        }
    }

    @POST
    @Path("/valid")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPessoaWithValid(@Valid @ConvertGroup(to = ValidationMovieGroups.Post.class) Pessoa pessoa) {

        pessoaRepository.persist(pessoa);
        return Response.ok(pessoa).build();
    }

    @POST
    @Path("/service")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPessoaWithPessoaService(Pessoa pessoa) {

        try {
            Pessoa pessoaToAdd = pessoaService.validate(pessoa);
            pessoaRepository.persist(pessoaToAdd);
            return Response.ok(pessoa).build();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));

            return Response.status(Status.BAD_REQUEST)
                    .entity(violations).build();

        }
    }
}