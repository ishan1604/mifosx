ALTER TABLE  `m_appuser` ADD  `password_never_expire` TINYINT NOT NULL DEFAULT  '0' COMMENT  'define if the password, should be check for validity period or not';