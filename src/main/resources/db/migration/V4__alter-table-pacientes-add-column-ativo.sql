alter table pacientes add column ativo boolean;
update pacientes set ativo = true;
alter table pacientes alter column ativo set not null;