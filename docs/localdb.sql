create database videoup;

create table videoup_conf(
  idcc integer primary key  auto_increment,
  dni_def varchar(15),
  dni_rgexp varchar(45),
  n_vclub varchar(45),
  logo_vclub blob,
  using_web boolean,
  contrato text not null,
  pcode_rgexp varchar(45),
  movtypebarcode varchar(35) not null,
  gamtypebarcode varchar(35) not null,
  socvtypebarcode varchar(35) not null,
  maxoffrts int default 1,
  pagesize int default 27, 
  chgEstreno boolean,
  fracmin_extra int default 0
);

create table videoup_tags(
  idtg integer primary key  auto_increment,
  nametag varchar(15),
  dimwidth int not null,
  dimheight int not null
);
insert into videoup_tags values(1,'Movies Tag'),(2,'Games Tag'),(3,'Members Tag');

--itemtype 1=text, 2=barcode, 3=image, 4=field titulo, 5=field clasificacion, 6=field categoria, 7=field formato
-- SOCIO> 8=field nombre, 9=field apellidos, 10=field dni, 11=field codigo_socio, 12=addr 13=codp 14=city 15=prov 16=pobl
-- 17=tel_home 18=tel_movil 19=f_alta 20=f_vigen 21=foto
  -- cuando v>=4 el nombre del campo se guarda en texto
create table videoup_tagitems(
  idti integer primary key  auto_increment,
  idtg integer,
  itemtype int not null,
  title varchar(55) not null,
  texto varchar(255),
  bcodetype varchar(255),
  bcodevalue varchar(255),
  img longblob,
  dimwidth int not null,
  dimheight int not null,
  loc_x int not null,
  loc_y int not null,
  CONSTRAINT rel_twentynine FOREIGN KEY(idtg) REFERENCES videoup_tags(idtg)
);

create table videoup_catgs(
  idcg integer primary key auto_increment,
  catg varchar(155) not null,
  dcatg varchar(255),
  cond_tx text
);

create table videoup_formts(
  idcf integer primary key auto_increment,
  frmt varchar(155) not null,
  dfrmt varchar(255)
);

create table videoup_langs(
  idit integer primary key auto_increment,
  name varchar(155),
  img longblob NOT NULL
);

create table videoup_ctprrentas(
  idcpr integer primary key auto_increment,
  namec varchar(55) not null,
  costou decimal(8,2) not null,
  unidad_nm varchar(25) not null,
  unidad_mins int not null,
  uns_base int not null,
  cstu_xtra decimal(8,2) not null
);

create table videoup_movies(
  idcm integer primary key auto_increment,
  titulo varchar(155) not null,
  director varchar(155),
  trailer_url text,
  prgistas varchar(255),
  sinopsis text not null,
  ldate date not null,
  procmpy varchar(155),
  dmin int not null,
  clasif varchar(5) not null,
  catg int,
  idcpr int,
  estreno_until datetime default 0,
  valoracion int unsigned,
  num_alqs int default 0,
  num_solds int default 0,
  num_changes int default 0,
  anyo int,
  CONSTRAINT rel_one FOREIGN KEY(catg) REFERENCES videoup_catgs(idcg),
  CONSTRAINT rel_fifteen FOREIGN KEY(idcpr) REFERENCES videoup_ctprrentas(idcpr)
);

create table videoup_mimgs(
  idrl integer primary key,
  img longblob not null
);

create table videoup_games(
  idca integer primary key  auto_increment,
  titulo varchar(155) not null,
  trailer_url text,
  sinopsis text not null,
  ldate date not null,
  procmpy varchar(155),
  clasif varchar(5) not null,
  catg int,
  idcpr int,
  estreno_until datetime default 0,
  valoracion int unsigned,
  num_alqs int default 0,
  num_solds int default 0,
  num_changes int default 0,
  anyo int,
  CONSTRAINT rel_two FOREIGN KEY(catg) REFERENCES videoup_catgs(idcg),
  CONSTRAINT rel_sixteen FOREIGN KEY(idcpr) REFERENCES videoup_ctprrentas(idcpr)
);

create table videoup_gimgs(
  idrl integer primary key,
  img longblob not null
);

--status 0=apartado, 1=finalizado 2=cancelado
create table videoup_ventas(
  idvn integer primary key  auto_increment,
  idcli int unsigned not null,
  tp_env varchar(75),
  cst_env decimal(8,2),
  status int default 0,
  factura boolean default 0,
  cst_subtotal decimal(8,2),
  impuesto decimal(8,2),
  onfecha date,
  fromCredito decimal(8,2),
  toAdeudo decimal(8,2),
  CONSTRAINT rel_twentyseven FOREIGN KEY(idcli) REFERENCES videoup_customers(idct)
);

create table videoup_itemsvnt(
  idiv integer primary key  auto_increment,
  idvn int,
  idbc int,
  descr text not null,
  CONSTRAINT rel_three FOREIGN KEY(idvn) REFERENCES videoup_ventas(idvn),
  CONSTRAINT rel_twentyeigth FOREIGN KEY(idbc) REFERENCES videoup_bcodes(idbc)
);

--tpo tipo: 1=de N a M articulos (N=aux_n M=aux_m), 2=de fecha a fecha (aux_d1 a aux_d2),
  -- 3=lleve N pague M, 4=por dia(s) de la semana, 5=por N puntos (aux_n)
--tpsv tipo de beneficio: 1=porcentaje descuento, 2=costo fijo especial, 3=lleva M paga N
create table videoup_ctofrentas(
  idcof integer primary key  auto_increment,
  imovie boolean not null,
  ingame boolean not null,
  tpo int not null,
  aux_n int,
  aux_m int,
  aux_d1 date,
  aux_d2 date,
  tpsv int not null,
  pr_desc int,
  cst_spc decimal(8,2) not null,
  namer varchar(21) not null,
  apl_lunes BOOLEAN,
  apl_martes BOOLEAN,
  apl_miercoles BOOLEAN,
  apl_jueves BOOLEAN,
  apl_viernes BOOLEAN,
  apl_sabado BOOLEAN,
  apl_domingo BOOLEAN,
  bypuntos int,
  aplydias int default 0,
  priority int default 1
);

create table videoup_rnts_ctprof(
  idcpr int,
  idof int,
  CONSTRAINT rel_four FOREIGN KEY(idcpr) REFERENCES videoup_ctprrentas(idcpr),
  CONSTRAINT rel_five FOREIGN KEY(idof) REFERENCES videoup_ctofrentas(idcof)
);
create table VIDEOUP_CATGS_CTPROF(
  idcg int,
  idof int,
  CONSTRAINT THIRTYTWO FOREIGN KEY(idcg) REFERENCES videoup_catgs(idcg),
  CONSTRAINT THIRTYTHREE FOREIGN KEY(idof) REFERENCES videoup_ctofrentas(idcof)
);

create table VIDEOUP_FRMTS_CTPROF(
  idfr int,
  idof int,
  CONSTRAINT THIRTY FOREIGN KEY(idfr) REFERENCES videoup_formts(idcf),
  CONSTRAINT THIRTYone FOREIGN KEY(idof) REFERENCES videoup_ctofrentas(idcof)
);

create table videoup_itmsactpr(
  idcpr int not null,
  idrel int not null,
  ismov boolean not null,
  CONSTRAINT rel_six FOREIGN KEY(idcpr) REFERENCES videoup_ctprrentas(idcpr)
);

-- cod_cst si es null indica que no es socio es cliente externo (ventas)
-- idweb id si trabaja con la web o 0 si no
create table videoup_customers(
  idct integer primary key  auto_increment,
  idweb integer,
  name varchar(255) NOT NULL default '',
  applldos varchar(255) NOT NULL default '',
  dni varchar(35) unique not null,
  cod_cst varchar(12) unique,
  email varchar(100) default '',
  addr varchar(105) not null,
  codp varchar(12),
  city varchar(55),
  prov varchar(55),
  pobl varchar(55),
  tel_home varchar(12),
  tel_movil varchar(12),
  f_alta datetime,
  f_vigen datetime,
  credito decimal(8,2),
  num_alqs int default 0,
  num_solds int default 0,
  num_changes int default 0,
  fnac date
);

create table videoup_cimgs(
  idrl integer primary key,
  foto longblob not null
);

create table videoup_rentas(
  idrt integer primary key auto_increment,
  idcli int not null,
  cst_fin decimal(8,2) default 0,
  otcst_fin decimal(8,2) default 0,
  factura boolean default 0,
  impuesto decimal(8,2),
  fromCredito decimal(8,2),
  toAdeudo decimal(8,2),
  CONSTRAINT rel_eigth FOREIGN KEY(idcli) REFERENCES videoup_customers(idct)
);

-- status 0=Solicitada, 1=Cancelada, 2=Finalizada, 3=En curso, 4=finalizada pagada, 5=finalizada por pagar, 6=cambiado
-- 7=en curso con adelanto
-- onchange indica que fue parte de un cambio donde el valor es el idir del producto anterior si este es el item nuevo
--  o es el producto proximo si este es el item previo (el cual se marca status cambiado)
--nm_ctrprrent nombre de catalogo de precios que aplica
create table videoup_itemsrnt(
  idir integer primary key auto_increment,
  idrt int not null,
  idbc int not null,
  descr text not null,
  cst_ut decimal(8,2) not null,
  cst_utx decimal(8,2) not null,
  b_time int not null,
  u_time int not null,
  cst_fin decimal(8,2) default 0,
  cst_xt decimal(8,2) not null,
  cst_apli decimal(8,2) not null,
  nm_ctrprrent varchar(55) not null,
  ofrt_off int default 0,
  ofrt_cfin decimal(8,2) default 0,
  s_time datetime default 0,
  i_time datetime default 0,
  f_time datetime default 0,
  status int default 0,
  onchange int default 0,
  ismov boolean,
  fromCredito decimal(8,2),
  toAdeudo decimal(8,2),
  CONSTRAINT rel_seven FOREIGN KEY(idrt) REFERENCES videoup_rentas(idrt),
  CONSTRAINT rel_twentyfour FOREIGN KEY(idbc) REFERENCES videoup_bcodes(idbc)
);

create table videoup_itrntofrt(
  idir int,
  idofr int,
  CONSTRAINT rel_twentyfive FOREIGN KEY(idir) REFERENCES videoup_itemsrnt(idir),
  CONSTRAINT rel_twentysix FOREIGN KEY(idofr) REFERENCES videoup_ctofrentas(idcof)
);

create table videoup_rntsapplyitms(
  idir int,
  idof int,
  CONSTRAINT rel_twentytwo FOREIGN KEY(idir) REFERENCES videoup_itemsrnt(idir),
  CONSTRAINT rel_twentythree FOREIGN KEY(idof) REFERENCES videoup_ctofrentas(idcof)
);

create table videoup_docs(
  iddc integer primary key  auto_increment,
  idct integer,
  docname varchar(155) NOT NULL,
  docimg longblob NOT NULL
);

--personas autorizadas a alquilar a nombre del cliente
create table videoup_autrz(
  idtz integer primary key  auto_increment,
  idct integer,
  pname varchar(255) NOT NULL,
  CONSTRAINT rel_ten FOREIGN KEY(idct) REFERENCES videoup_customers(idct)
);

--notas del cliente
-- ntype 0=neutral, 1=negativa, 2=advertencia, 3=positiva
create table videoup_cstmrnotes(
  idnt integer primary key  auto_increment,
  idct integer,
  note text NOT NULL,
  fecha datetime,
  ntype integer,
  CONSTRAINT rel_eleven FOREIGN KEY(idct) REFERENCES videoup_customers(idct)
);

-- estado 0=disponible, 1=en alquiler, 2=vendida, 3=apartada para alquiler, 4=apartada para venta, 5=da\F1ada, 6=sustraida, 7=perdida
create table videoup_bcodes(
  idbc integer primary key auto_increment,
  barcode varchar(12) unique,
  vendible boolean not null,
  frmt int,
  pr_venta decimal(8,2) not null,
  status varchar(32),
  pr_compra decimal(8,2),
  CONSTRAINT rel_twentyone FOREIGN KEY(frmt) REFERENCES videoup_formts(idcf)
);

create table videoup_bcdmov(
  idbc integer,
  idcm integer,
  CONSTRAINT rel_seventeen FOREIGN KEY(idbc) REFERENCES videoup_bcodes(idbc),
  CONSTRAINT rel_eightteen FOREIGN KEY(idcm) REFERENCES videoup_movies(idcm)
);

create table videoup_bcdgam(
  idbc integer,
  idca integer,
  CONSTRAINT rel_nineteen FOREIGN KEY(idbc) REFERENCES videoup_bcodes(idbc),
  CONSTRAINT rel_twenty FOREIGN KEY(idca) REFERENCES videoup_games(idca)
);

create table videoup_bcdlang(
  idbl integer primary key auto_increment,
  idbc int not null,
  idit int not null,
  as_lang boolean not null,
  CONSTRAINT rel_thirteen FOREIGN KEY(idbc) REFERENCES videoup_bcodes(idbc),
  CONSTRAINT rel_fourteen FOREIGN KEY(idit) REFERENCES videoup_langs(idit)
);

create table videoup_taxes(
  idtx integer primary key auto_increment,
  namet varchar(25),
  porcent int unsigned not null,
  ap_rent boolean not null,
  ap_vent boolean not null,
  factur_onl boolean not null
);

create table videoup_puntos(
  idpn integer primary key auto_increment,
  idct integer not null,
  puntos int not null,
  asig_on datetime not null,
  v_hasta datetime not null,
  used_on datetime,
  used_pnts int,
  CONSTRAINT rel_twelve FOREIGN KEY(idct) REFERENCES videoup_customers(idct)
);

create table videoup_histcredito(
  idhc integer primary key auto_increment,
  idct integer not null,
  monto decimal(8,2),
  asig_on datetime not null,
  CONSTRAINT rel_thirtyfour FOREIGN KEY(idct) REFERENCES videoup_customers(idct)
);

create table videoup_bonos(
  idbn integer primary key auto_increment,
  dias integer not null,
  articls integer not null,
  hours integer not null,
  applymvs boolean,
  applygms boolean,
  costo decimal(8,2)
);

create table videoup_soldbonos(
  idsb integer primary key auto_increment,
  idcli integer not null,
  inicia date not null,
  hasta date not null,
  bonos integer not null,
  hours integer not null,
  applymvs boolean,
  applygms boolean,
  used integer not null,
  pagado decimal(8,2),
  CONSTRAINT rel_thirtyfive FOREIGN KEY(idcli) REFERENCES videoup_customers(idct)
);


