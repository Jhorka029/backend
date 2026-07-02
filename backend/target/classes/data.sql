-- ============================================================
-- DATOS DE PRUEBA – 20 ESTUDIANTES, 20 DOCENTES, CURSOS REALES POR PROGRAMA
-- ============================================================

-- ESTUDIANTES
INSERT IGNORE INTO estudiantes (codigo_estudiante, dni, nombre_completo, email, telefono, direccion, fecha_nacimiento, fecha_registro, estado) VALUES
('EST-001', '10000001', 'Carlos Alberto García Mendoza', 'carlos.garcia@alumno.iespp.edu.pe', '987654001', 'Av. Los Olivos 123, Lima', '2002-03-15', NOW(), 'Activo'),
('EST-002', '10000002', 'María Elena Torres Sánchez', 'maria.torres@alumno.iespp.edu.pe', '987654002', 'Jr. Las Flores 456, Arequipa', '2003-07-22', NOW(), 'Activo'),
('EST-003', '10000003', 'José Luis Fernández Rojas', 'jose.fernandez@alumno.iespp.edu.pe', '987654003', 'Calle Real 789, Cusco', '2001-11-10', NOW(), 'Activo'),
('EST-004', '10000004', 'Ana Cecilia Martínez López', 'ana.martinez@alumno.iespp.edu.pe', '987654004', 'Av. Primavera 321, Trujillo', '2002-05-08', NOW(), 'Activo'),
('EST-005', '10000005', 'Miguel Ángel Rodríguez Díaz', 'miguel.rodriguez@alumno.iespp.edu.pe', '987654005', 'Pje. San Martín 654, Chiclayo', '2003-09-30', NOW(), 'Activo'),
('EST-006', '10000006', 'Lucía Fernanda Castillo Vargas', 'lucia.castillo@alumno.iespp.edu.pe', '987654006', 'Jr. Amazonas 147, Iquitos', '2002-01-18', NOW(), 'Activo'),
('EST-007', '10000007', 'Diego Alejandro Morales Paredes', 'diego.morales@alumno.iespp.edu.pe', '987654007', 'Av. El Sol 258, Huancayo', '2004-04-25', NOW(), 'Activo'),
('EST-008', '10000008', 'Valeria Sofía Huamán Quispe', 'valeria.huaman@alumno.iespp.edu.pe', '987654008', 'Calle Los Pinos 369, Cusco', '2003-12-02', NOW(), 'Activo'),
('EST-009', '10000009', 'Andrés Felipe Chávez Medina', 'andres.chavez@alumno.iespp.edu.pe', '987654009', 'Av. Universitaria 741, Lima', '2002-08-14', NOW(), 'Activo'),
('EST-010', '10000010', 'Camila Alejandra Ríos Navarro', 'camila.rios@alumno.iespp.edu.pe', '987654010', 'Jr. Las Begonias 852, Piura', '2001-06-27', NOW(), 'Activo'),
('EST-011', '10000011', 'Jorge Antonio Salazar Vega', 'jorge.salazar@alumno.iespp.edu.pe', '987654011', 'Calle Los Cedros 963, Tacna', '2004-02-19', NOW(), 'Activo'),
('EST-012', '10000012', 'Paola Andrea Miranda Guzmán', 'paola.miranda@alumno.iespp.edu.pe', '987654012', 'Av. La Marina 159, Lima', '2003-10-05', NOW(), 'Activo'),
('EST-013', '10000013', 'Renato David Pacheco Delgado', 'renato.pacheco@alumno.iespp.edu.pe', '987654013', 'Jr. Los Claveles 357, Arequipa', '2002-04-11', NOW(), 'Activo'),
('EST-014', '10000014', 'Gabriela Isabel Peña Herrera', 'gabriela.pena@alumno.iespp.edu.pe', '987654014', 'Pje. Los Álamos 486, Huaraz', '2001-09-28', NOW(), 'Activo'),
('EST-015', '10000015', 'Fernando Jesús Córdova Ramos', 'fernando.cordova@alumno.iespp.edu.pe', '987654015', 'Av. Grau 264, Ica', '2004-07-16', NOW(), 'Activo'),
('EST-016', '10000016', 'Daniela Estefanía Campos Flores', 'daniela.campos@alumno.iespp.edu.pe', '987654016', 'Jr. Unión 573, Puno', '2003-01-03', NOW(), 'Activo'),
('EST-017', '10000017', 'Luis Enrique Guamán Huertas', 'luis.guaman@alumno.iespp.edu.pe', '987654017', 'Calle Los Olivos 318, Moquegua', '2002-12-21', NOW(), 'Activo'),
('EST-018', '10000018', 'Katherine Alexandra Velásquez Ortiz', 'katherine.velasquez@alumno.iespp.edu.pe', '987654018', 'Av. Ejército 429, Ayacucho', '2004-05-09', NOW(), 'Activo'),
('EST-019', '10000019', 'Sebastián Ignacio Villanueva Tapia', 'sebastian.villanueva@alumno.iespp.edu.pe', '987654019', 'Jr. Las Palmeras 682, Cajamarca', '2001-08-07', NOW(), 'Activo'),
('EST-020', '10000020', 'Ximena Patricia Aguilar Benavides', 'ximena.aguilar@alumno.iespp.edu.pe', '987654020', 'Av. Los Laureles 791, Lima', '2003-11-26', NOW(), 'Activo');

-- DOCENTES
INSERT IGNORE INTO docentes (codigo_docente, dni, nombre_completo, email, telefono, direccion, especialidad, fecha_nacimiento, fecha_registro, estado) VALUES
('DOC-001', '20000001', 'Dr. Ricardo Antonio Gutiérrez Silva', 'ricardo.gutierrez@iespp.edu.pe', '976543001', 'Av. Principal 234, Lima', 'Matemática', '1975-04-12', NOW(), 'Activo'),
('DOC-002', '20000002', 'Mg. Elena Patricia Castro Mendoza', 'elena.castro@iespp.edu.pe', '976543002', 'Jr. Los Sauces 567, Arequipa', 'Comunicación', '1980-09-28', NOW(), 'Activo'),
('DOC-003', '20000003', 'Lic. Hugo Martín Delgado Paredes', 'hugo.delgado@iespp.edu.pe', '976543003', 'Av. Los Incas 890, Cusco', 'Historia y Geografía', '1978-02-15', NOW(), 'Activo'),
('DOC-004', '20000004', 'Dra. Silvia Beatriz Vargas Luna', 'silvia.vargas@iespp.edu.pe', '976543004', 'Calle Real 432, Trujillo', 'Ciencia y Ambiente', '1982-07-01', NOW(), 'Activo'),
('DOC-005', '20000005', 'Mg. Carlos Enrique León Romero', 'carlos.leon@iespp.edu.pe', '976543005', 'Jr. Las Dalias 765, Chiclayo', 'Inglés', '1976-11-20', NOW(), 'Activo'),
('DOC-006', '20000006', 'Lic. Patricia Mercedes Rivas Campos', 'patricia.rivas@iespp.edu.pe', '976543006', 'Av. Los Rosales 198, Iquitos', 'Educación Física', '1984-03-09', NOW(), 'Activo'),
('DOC-007', '20000007', 'Mg. Fernando Javier Peña Castillo', 'fernando.pena@iespp.edu.pe', '976543007', 'Calle Los Claveles 276, Huancayo', 'Matemática', '1979-08-17', NOW(), 'Activo'),
('DOC-008', '20000008', 'Dra. Rosa María Vargas Quispe', 'rosa.vargas@iespp.edu.pe', '976543008', 'Jr. Los Geranios 543, Cusco', 'Ciencia y Ambiente', '1981-05-30', NOW(), 'Activo'),
('DOC-009', '20000009', 'Lic. Alberto José Ríos Navarro', 'alberto.rios@iespp.edu.pe', '976543009', 'Av. El Bosque 876, Lima', 'Historia y Geografía', '1977-12-14', NOW(), 'Activo'),
('DOC-010', '20000010', 'Mg. Carmen Rosa Huamán Torres', 'carmen.huaman@iespp.edu.pe', '976543010', 'Jr. Las Orquídeas 234, Piura', 'Comunicación', '1983-06-22', NOW(), 'Activo'),
('DOC-011', '20000011', 'Lic. Pedro Ángel Salazar Vega', 'pedro.salazar@iespp.edu.pe', '976543011', 'Calle Los Eucaliptos 678, Tacna', 'Inglés', '1974-01-11', NOW(), 'Activo'),
('DOC-012', '20000012', 'Dra. Mercedes del Pilar Miranda Guzmán', 'mercedes.miranda@iespp.edu.pe', '976543012', 'Av. Los Tulipanes 345, Lima', 'Matemática', '1985-10-03', NOW(), 'Activo'),
('DOC-013', '20000013', 'Mg. Gustavo Adolfo Pacheco Delgado', 'gustavo.pacheco@iespp.edu.pe', '976543013', 'Jr. Los Cerezos 567, Arequipa', 'Educación Física', '1978-04-27', NOW(), 'Activo'),
('DOC-014', '20000014', 'Lic. Mónica Isabel Peña Herrera', 'monica.pena@iespp.edu.pe', '976543014', 'Av. Los Pinos 789, Huaraz', 'Comunicación', '1982-09-18', NOW(), 'Activo'),
('DOC-015', '20000015', 'Mg. Víctor Manuel Córdova Ramos', 'victor.cordova@iespp.edu.pe', '976543015', 'Calle Las Gardenias 123, Ica', 'Historia y Geografía', '1976-02-08', NOW(), 'Activo'),
('DOC-016', '20000016', 'Lic. Fiorella Katherine Campos Flores', 'fiorella.campos@iespp.edu.pe', '976543016', 'Jr. Los Jazmines 456, Puno', 'Ciencia y Ambiente', '1984-07-15', NOW(), 'Activo'),
('DOC-017', '20000017', 'Dr. Óscar Raúl Guamán Huertas', 'oscar.guaman@iespp.edu.pe', '976543017', 'Av. Los Olivos 321, Moquegua', 'Inglés', '1975-11-29', NOW(), 'Activo'),
('DOC-018', '20000018', 'Mg. Pamela Andrea Velásquez Ortiz', 'pamela.velasquez@iespp.edu.pe', '976543018', 'Jr. Los Lirios 654, Ayacucho', 'Matemática', '1980-05-12', NOW(), 'Activo'),
('DOC-019', '20000019', 'Lic. Teodoro Javier Villanueva Tapia', 'teodoro.villanueva@iespp.edu.pe', '976543019', 'Calle Los Abetos 987, Cajamarca', 'Educación Física', '1977-03-25', NOW(), 'Activo'),
('DOC-020', '20000020', 'Dra. Natalia Beatriz Aguilar Benavides', 'natalia.aguilar@iespp.edu.pe', '976543020', 'Av. Los Castaños 789, Lima', 'Comunicación', '1983-08-06', NOW(), 'Activo');

-- PROGRAMAS DE ESTUDIO
INSERT IGNORE INTO programas_estudio (codigo, nombre, descripcion, estado) VALUES
('PROG-01', 'Educación Inicial EIB', 'Formación docente para educación inicial con enfoque intercultural bilingüe', 'Activo'),
('PROG-02', 'Educación Primaria EIB', 'Formación docente para educación primaria con enfoque intercultural bilingüe', 'Activo'),
('PROG-03', 'Educación Secundaria – Ciencia y Tecnología', 'Formación docente especializada en ciencia y tecnología', 'Activo'),
('PROG-04', 'Educación Secundaria – Comunicación', 'Formación docente especializada en comunicación', 'Activo'),
('PROG-05', 'Educación Secundaria – Ciudadanía y CC.SS.', 'Formación docente especializada en ciudadanía y ciencias sociales', 'Activo');

-- CURSOS REALES (extraídos del plan de estudios de la web institucional)
INSERT IGNORE INTO cursos (codigo, nombre, descripcion, creditos, horas, programa_id, ciclo, fecha_registro, estado) VALUES
-- EDUCACIÓN INICIAL EIB (programa_id=1) - Currículo real desde la web
('CUR-001', 'Lectura y escritura en la Educación Superior', 'Comprensión y producción de textos académicos', 3, 64, 1, 1, NOW(), 'Activo'),
('CUR-002', 'Resolución de problemas matemáticos I', 'Razonamiento lógico y resolución de problemas numéricos', 4, 80, 1, 1, NOW(), 'Activo'),
('CUR-003', 'Desarrollo personal I', 'Autoconocimiento, empatía y habilidades socioemocionales', 3, 64, 1, 1, NOW(), 'Activo'),
('CUR-004', 'Práctica e investigación I', 'Observación y análisis del contexto educativo', 4, 96, 1, 1, NOW(), 'Activo'),
('CUR-005', 'Lengua Indígena u Originaria I', 'Fundamentos de lengua originaria y comunicación intercultural', 3, 64, 1, 1, NOW(), 'Activo'),
('CUR-006', 'Fundamentos de la Educación Inicial EIB', 'Bases teóricas de la educación inicial intercultural bilingüe', 3, 64, 1, 1, NOW(), 'Activo'),
('CUR-007', 'Resolución de problemas matemáticos II', 'Razonamiento estadístico y probabilístico', 4, 80, 1, 2, NOW(), 'Activo'),
('CUR-008', 'Comunicación Oral en la Educación Superior', 'Expresión oral, argumentación y debate académico', 3, 64, 1, 2, NOW(), 'Activo'),
('CUR-009', 'Historia, Sociedad y Diversidad', 'Análisis histórico-social y diversidad cultural peruana', 3, 64, 1, 2, NOW(), 'Activo'),
('CUR-010', 'Planificación y Evaluación para el Aprendizaje en EIB I', 'Estrategias de planificación curricular y evaluación formativa', 3, 64, 1, 2, NOW(), 'Activo'),
('CUR-011', 'Arte, Creatividad y Aprendizaje', 'Expresión artística como estrategia pedagógica', 3, 64, 1, 3, NOW(), 'Activo'),
('CUR-012', 'Comunicación en Castellano I', 'Desarrollo de competencias comunicativas en lengua castellana', 3, 64, 1, 3, NOW(), 'Activo'),
('CUR-013', 'Desarrollo Personal y Social en la Primera Infancia', 'Psicología del desarrollo infantil temprano', 3, 64, 1, 3, NOW(), 'Activo'),
('CUR-014', 'Ciencia y Epistemologías', 'Pensamiento científico y saberes ancestrales', 3, 64, 1, 4, NOW(), 'Activo'),
('CUR-015', 'Desarrollo del Bilingüismo en la Primera Infancia', 'Adquisición y desarrollo de lenguas en niños bilingües', 3, 64, 1, 4, NOW(), 'Activo'),
('CUR-016', 'Desarrollo de Psicomotricidad en la Primera Infancia', 'Psicomotricidad, movimiento y aprendizaje infantil', 3, 64, 1, 4, NOW(), 'Activo'),
('CUR-017', 'Inglés para Principiantes I', 'Nivel básico del idioma inglés con enfoque comunicativo', 3, 80, 1, 5, NOW(), 'Activo'),
('CUR-018', 'Inclusión educativa para la atención a la diversidad', 'Estrategias pedagógicas inclusivas y atención a necesidades educativas', 3, 64, 1, 5, NOW(), 'Activo'),
('CUR-019', 'Alfabetización Científica', 'Enseñanza de ciencias naturales en educación inicial', 3, 64, 1, 6, NOW(), 'Activo'),
('CUR-020', 'Desarrollo de las matemáticas en la primera infancia', 'Didáctica de la matemática para niños de 0 a 5 años', 3, 64, 1, 6, NOW(), 'Activo'),
('CUR-021', 'Ética y Filosofía para el Pensamiento Crítico', 'Reflexión ética y pensamiento crítico en la práctica docente', 3, 64, 1, 7, NOW(), 'Activo'),
('CUR-022', 'Expresión del Arte en la Primera Infancia', 'Lenguajes artísticos y expresión creativa en niños pequeños', 3, 64, 1, 7, NOW(), 'Activo'),
('CUR-023', 'Aprendizaje y Desarrollo de Lenguas en Niños Bilingües', 'Estrategias para la enseñanza de lenguas en contextos bilingües', 3, 64, 1, 8, NOW(), 'Activo'),
('CUR-024', 'Atención a Necesidades Educativas Especiales', 'Identificación e intervención temprana de NEE', 3, 64, 1, 8, NOW(), 'Activo'),
('CUR-025', 'Escuela, Familia y Comunidad', 'Relación escuela-familia y participación comunitaria en EIB', 3, 64, 1, 9, NOW(), 'Activo'),
('CUR-026', 'Políticas y Gestión en EIB', 'Gestión educativa con enfoque intercultural bilingüe', 3, 64, 1, 10, NOW(), 'Activo'),

-- EDUCACIÓN PRIMARIA EIB (programa_id=2)
('CUR-027', 'Didáctica de la Matemática en Primaria', 'Estrategias para la enseñanza de matemática en nivel primaria', 4, 80, 2, 3, NOW(), 'Activo'),
('CUR-028', 'Didáctica de la Comunicación en Primaria', 'Enseñanza de lectura y escritura en educación primaria', 4, 80, 2, 3, NOW(), 'Activo'),
('CUR-029', 'Gestión del Aula en Primaria', 'Organización y manejo del aula en educación primaria', 3, 64, 2, 5, NOW(), 'Activo'),

-- EDUCACIÓN SECUNDARIA – Ciencia y Tecnología (programa_id=3)
('CUR-030', 'Física General', 'Principios de mecánica, termodinámica y electromagnetismo', 4, 80, 3, 2, NOW(), 'Activo'),
('CUR-031', 'Química General', 'Estructura atómica, enlaces y reacciones químicas', 4, 80, 3, 2, NOW(), 'Activo'),
('CUR-032', 'Biología Celular y Molecular', 'Estructura y función celular, genética básica', 4, 80, 3, 3, NOW(), 'Activo'),

-- EDUCACIÓN SECUNDARIA – Comunicación (programa_id=4)
('CUR-033', 'Literatura Peruana y Latinoamericana', 'Análisis de obras literarias representativas', 3, 64, 4, 2, NOW(), 'Activo'),
('CUR-034', 'Lingüística General', 'Estudio del lenguaje humano y teorías lingüísticas', 3, 64, 4, 3, NOW(), 'Activo'),
('CUR-035', 'Didáctica de la Comunicación en Secundaria', 'Estrategias para la enseñanza de comunicación', 3, 64, 4, 5, NOW(), 'Activo'),

-- EDUCACIÓN SECUNDARIA – Ciudadanía y CC.SS. (programa_id=5)
('CUR-036', 'Historia del Perú Contemporáneo', 'Procesos históricos peruanos de los siglos XIX-XX', 3, 64, 5, 2, NOW(), 'Activo'),
('CUR-037', 'Geografía y Desarrollo Sostenible', 'Espacio geográfico, recursos naturales y sostenibilidad', 3, 64, 5, 3, NOW(), 'Activo');
