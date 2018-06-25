CREATE TABLE sle_payroll_types
(company_id    	varchar(10)     not null
,id         	varchar(4)      not null
,name	     	varchar(64)     not null
,frequency     	varchar(24)     not null
,currency_id	numeric(4)		not null
,payslip_fmt	varchar(32)		not null
,inactive		numeric(1)		default 0 not null
,last_benefit	date
,audit_user     varchar(64)
,audit_dte      date
,audit_track    numeric(10)      default 0
)storage (initial 512 K next 256 K);

alter table sle_payroll_types
add constraint pk_payroll_type
	primary key (company_id,id) using index tablespace sleindex;

create or replace view sle$user_company
(company_id
,company_name
,username
,constraint pk$UserCompany    primary key (username,company_id) rely disable novalidate
)
as 
select 
co.id
,co.name
,us.username
from sle_user_company usco
inner join sle_company co on co.id = usco.company_id
inner join sle_users us on us.id = usco.user_id;


create or replace view sle$user_payrolls
(company_id
,company_name
,payroll_id
,payroll_name
,username
,constraint pk$UserPayroll    primary key (username,company_id,payroll_id) rely disable novalidate
)
as 
select 
co.id
,co.name
,payr.id
,payr.name
,us.username
from sle_user_payrolls upay
inner join sle_company co on upay.company_id = co.id
inner join sle_payroll_types payr on upay.company_id = payr.company_id and upay.payroll_id = payr.id
inner join sle_users us on us.id = upay.user_id;

-- Datos para probar en ambiente de Plan C, requerida para obterner datos en la vista sle$user_payrolls
---Definión de nóminas
insert into sle_payroll_types values('01','01','NOMINA SEMANAL -BDA','7','937','payslip01',0,null,user,sysdate,0);
insert into sle_payroll_types values('01','02','NOMINA QUINCENAL -DDA','15','937','payslip01',0,null,user,sysdate,0);

--Usuarios nómina
insert into sle_user_payrolls values(1,'01','01');
insert into sle_user_payrolls values(1,'01','02');
