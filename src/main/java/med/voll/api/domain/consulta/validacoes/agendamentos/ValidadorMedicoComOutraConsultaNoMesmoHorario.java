package med.voll.api.domain.consulta.validacoes.agendamentos;

import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoComOutraConsultaNoMesmoHorario implements ValidadorAgendamentoDeConsulta{

    @Autowired
    private ConsultaRepository repository;

    public void validar(DadosAgendamentoConsulta dados) {
        var inicioDoDia = dados.data().withHour(1);
        var fimDoDia = dados.data().withHour(23);
        var consultasDoMedicoNoDia = repository.findAllByMedicoIdAndDataBetween(dados.idMedico(), inicioDoDia, fimDoDia);
        if (consultasDoMedicoNoDia.stream().anyMatch(
                consulta -> {
                    var inicioDaConsulta = consulta.getData();
                    var fimDaConsulta = consulta.getData().plusHours(1);
                    return consulta.getMotivoCancelamento() == null
                            && !dados.data().isAfter(fimDaConsulta)
                            && !dados.data().isBefore(inicioDaConsulta.minusHours(1));
                })
        ) {
            throw new RuntimeException("Médico já possui uma consulta agendada nesse intervalo");
        }
    }
}
