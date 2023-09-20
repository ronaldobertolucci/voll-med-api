alter table medicos add column ativo boolean;
update medicos set ativo = true;
alter table medicos alter column ativo set not null;