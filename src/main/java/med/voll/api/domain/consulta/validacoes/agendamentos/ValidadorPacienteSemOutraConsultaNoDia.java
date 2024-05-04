package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteSemOutraConsultaNoDia implements ValidadorAgendamentoDeConsulta {

    @Autowired
    private ConsultaRepository repository;

    public void validar(DadosAgendamentoConsulta dados) {
        var primeiroHorario = dados.data().withHour(7);
        var ultimoHorario = dados.data().withHour(18);
        var pacientePossuiOutraConsultaNoDia = repository.existsByPacienteIdAndMotivoCancelamentoAndDataBetween(
                dados.idPaciente(), null, primeiroHorario, ultimoHorario
        );
        if (pacientePossuiOutraConsultaNoDia) {
            throw new RuntimeException("Paciente já possui uma consulta agendada nesse dia");
        }
    }

}
