package med.voll.api.domain.consulta.validacoes.cancelamentos;

import lombok.NoArgsConstructor;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosCancelamentoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Component
@NoArgsConstructor
public class ValidadorHorarioAntecedenciaCancelamento implements ValidadorCancelamentoDeConsulta{

    @Autowired
    private ConsultaRepository repository;

    public ValidadorHorarioAntecedenciaCancelamento(ConsultaRepository repository) {
        this.repository = repository;
    }

    public void validar(DadosCancelamentoConsulta dados) {
        try {
            var consulta = repository.findById(dados.idConsulta()).orElseThrow();
            var dataConsulta = consulta.getData();
            var agora = LocalDateTime.now();
            var diferencaEmMinutos = Duration.between(agora, dataConsulta).toHours();

            if (diferencaEmMinutos < 24) {
                throw new RuntimeException("Consulta só pode ser cancelada com antecedência de 24h");
            }
        } catch (NoSuchElementException exception) {
            throw new RuntimeException("Consulta não encontrada");
        }
    }
}
