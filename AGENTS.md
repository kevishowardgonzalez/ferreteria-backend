AGENTS.md — Ferretería Los Maestros
1. Contexto del proyecto
- Proyecto: Ferretería Los Maestros.
- Institución: Duoc UC.
- Carrera: Ingeniería en Informática.
- Asignatura: Desarrollo FullStack II.
- Evaluación: Evaluación Parcial 3.
- Repositorio backend: https://github.com/kevishowardgonzalez/ferreteria-backend
El objetivo es desarrollar un sistema Full Stack funcional, seguro, integrado y documentado, respetando los contenidos impartidos en clases y los requisitos de la evaluación.

El desarrollo se realizará de manera colaborativa entre el estudiante, ChatGPT y Codex, mediante etapas previamente autorizadas.
2. Documentación académica obligatoria
Antes de implementar modificaciones, revisar los documentos disponibles en la carpeta Documentacion/:
- Instrucciones oficiales de la Evaluación Parcial 3.
- Rúbrica de evaluación.
- Guía 15.
- Guía 16.
- Guía 17.
- Guía 18.
- Otros documentos académicos proporcionados por el docente.
Las instrucciones oficiales del profesor y la rúbrica tienen prioridad sobre cualquier propuesta técnica.

Si una funcionalidad, dependencia o tecnología no está contemplada en los contenidos impartidos, consultar antes de incorporarla.

No inventar requisitos académicos.
3. Alcance del repositorio
Este repositorio contiene exclusivamente el backend de Ferretería Los Maestros.

Los microservicios existentes son:
- ferreteria-auth
- ferreteria-usuarios
- ferreteria-roles
- ferreteria-productos
- ferreteria-categorias
- ferreteria-inventario
- ferreteria-carrito
- ferreteria-boletas
- ferreteria-contacto
- ferreteria-foro
Mantener los diez microservicios y sus responsabilidades, salvo autorización explícita para modificar la arquitectura.

No modificar el repositorio frontend desde este espacio de trabajo.
4. Tecnologías permitidas
Priorizar las tecnologías existentes y aquellas contempladas en las guías académicas:
- Java 21.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Spring Security.
- Jakarta Validation.
- Lombok.
- MySQL.
- Maven.
- JWT.
- BCrypt.
- Swagger y OpenAPI.
- JUnit.
- Mockito.
- MockMvc.
- H2 exclusivamente para pruebas, cuando corresponda.
- Comunicación REST entre microservicios.
- AWS EC2 y Nginx para despliegue.
No incorporar nuevas dependencias, frameworks, patrones avanzados o herramientas sin verificar su pertinencia académica y solicitar autorización.

No actualizar versiones de dependencias innecesariamente.
5. Arquitectura y organización del código
Respetar la estructura de paquetes existente y la separación de responsabilidades:
- model: entidades y modelos de datos.
- repository: acceso a datos.
- service: lógica de negocio.
- controller: exposición de endpoints REST.
- dto: solicitudes y respuestas.
- config: configuración de la aplicación.
- security: autenticación y autorización.
- exception: manejo de excepciones.
- client: comunicación HTTP entre microservicios, cuando se autorice.
Corregir paquetes, importaciones y nombres de clases inconsistentes.

Evitar duplicación innecesaria de código.

Mantener nombres claros y coherentes con el dominio de la ferretería.

El código debe ser comprensible y explicable por un estudiante de segundo año de Ingeniería en Informática.
6. Roles y permisos
Respetar los perfiles establecidos por el docente:
ADMIN
- Acceso completo a las funcionalidades administrativas.
- Gestión de productos, categorías, usuarios y demás recursos administrativos.
- Consulta y gestión de ventas.
VENDEDOR
- Consulta del listado de productos.
- Consulta del detalle de productos.
- Consulta del listado de pedidos o boletas.
- Consulta del detalle de pedidos o boletas.
- Sin permisos de creación, modificación, eliminación o anulación administrativa.
CLIENTE
- Acceso a las funcionalidades de la tienda.
- Consulta del catálogo.
- Gestión de su propio carrito.
- Realización de compras.
- Consulta de su propia información e historial de compras.
- Sin acceso a funcionalidades administrativas.
Los permisos deben comprobarse en el backend y no depender exclusivamente de las restricciones visuales del frontend.

Cualquier permiso adicional deberá justificarse según los requisitos oficiales.
7. Seguridad
- Utilizar BCrypt para almacenar contraseñas.
- Nunca almacenar contraseñas en texto plano.
- Nunca devolver contraseñas ni hashes en respuestas HTTP.
- Validar la firma, expiración y emisor de los JWT.
- Mantener coherencia en los claims y roles utilizados por los microservicios.
- Utilizar variables de entorno para secretos y credenciales.
- No incorporar secretos JWT predeterminados públicos.
- Configurar CORS mediante propiedades externas.
- Aplicar sesiones stateless cuando corresponda.
- Diferenciar correctamente respuestas HTTP 401 y 403.
- Impedir que un cliente acceda a información privada de otro usuario.
- Evitar que el usuario pueda modificar su propio rol mediante solicitudes no autorizadas.
- No exponer información sensible en logs, excepciones o documentación.
No introducir credenciales reales en archivos versionados.
8. Integridad de los datos y lógica de negocio
Las siguientes responsabilidades representan la arquitectura objetivo y deberán implementarse progresivamente, previa autorización:
- Auth será responsable de las credenciales y autenticación.
- Usuarios será responsable de los perfiles, manteniendo coherencia con Auth.
- Roles gestionará el catálogo de roles conforme a los requisitos.
- Categorías será responsable de la clasificación de productos.
- Productos será la fuente oficial de información y precios del catálogo.
- Inventario será la fuente oficial de existencias.
- Carrito gestionará productos y cantidades seleccionadas.
- Boletas gestionará ventas y detalles de compra.
Reglas fundamentales:
- No confiar en precios proporcionados por el cliente.
- Consultar los precios oficiales desde Productos.
- Validar la existencia de productos y categorías.
- Validar cantidades y disponibilidad de stock.
- Evitar existencias negativas.
- Evitar compras duplicadas y descuentos repetidos de inventario.
- Mantener coherencia entre compras, anulaciones y movimientos de stock.
- Conservar el precio histórico de los productos vendidos.
- Evitar errores de precisión en cálculos monetarios.
- No vaciar el carrito antes de confirmar una compra exitosa.
- No confiar en la identidad del usuario proporcionada por el cuerpo de una solicitud cuando pueda obtenerse del JWT.
No asumir que una transacción local de JPA garantiza atomicidad entre bases de datos de diferentes microservicios.
9. APIs REST y Swagger
- Respetar los contratos HTTP existentes mientras no se autorice su modificación.
- Utilizar métodos HTTP adecuados: GET, POST, PUT, PATCH y DELETE, según corresponda.
- Aplicar validaciones mediante Jakarta Validation.
- Utilizar DTO cuando corresponda.
- Mantener respuestas HTTP coherentes.
- Implementar manejo centralizado de excepciones.
- Documentar endpoints mediante Swagger y OpenAPI.
- Identificar endpoints públicos y protegidos.
- Documentar los permisos requeridos por cada operación.
- Documentar solicitudes, respuestas y códigos HTTP.
- No cambiar rutas o estructuras JSON sin informar el impacto sobre React.
10. Repositorio de referencia
Repositorio de referencia técnica:

https://github.com/NoemiBello/sonido-vivo-backend

Este repositorio pertenece a otro equipo académico.

Puede consultarse exclusivamente para estudiar buenas prácticas relacionadas con:
- Comunicación REST entre microservicios.
- Validación de productos y categorías.
- Gestión de inventario.
- Cálculo de precios y totales.
- Emisión y cancelación de compras.
- DTO y manejo de excepciones.
No copiar directamente su código, documentación, identidad visual ni funcionalidades específicas de una tienda de sonido.

No asumir que sus implementaciones están libres de errores.

Toda solución debe adaptarse al dominio de Ferretería Los Maestros y respetar los contenidos académicos.
11. Plan de trabajo
Trabajar progresivamente en las siguientes etapas:
0. Preparación del repositorio y documentación académica.
1. Reparación estructural y compilación de los diez microservicios.
2. Seguridad, autenticación, identidad y permisos.
3. Integración de Productos, Categorías e Inventario.
4. Integración comercial de Carrito y Boletas.
5. Revisión de Contacto y Foro.
6. Pruebas automatizadas, Swagger y documentación técnica.
7. Preparación de contratos para la integración con React.
8. Preparación y verificación del despliegue AWS.
9. Revisión final y preparación de la evaluación.
No avanzar automáticamente a una etapa posterior.

Cada etapa debe contar con autorización explícita del estudiante.
12. Pruebas y verificación
Antes de modificar archivos:
- Revisar git status.
- Identificar la rama actual.
- Revisar modificaciones locales existentes.
- Analizar los archivos relacionados con la tarea.
- Identificar posibles impactos sobre otros microservicios.
Después de modificar archivos:
- Compilar los microservicios afectados.
- Ejecutar sus pruebas automatizadas.
- Verificar los contratos afectados.
- Revisar git diff.
- Informar los resultados reales.
Utilizar Java 21 para las verificaciones finales.

Las pruebas deben cubrir progresivamente:
- CRUD.
- Validaciones.
- Autenticación JWT.
- Permisos por rol.
- Errores HTTP.
- Precios y cantidades.
- Movimientos de inventario.
- Compras y anulaciones.
- Comunicación entre microservicios.
Una prueba contextLoads() no constituye evidencia suficiente de funcionamiento completo.

No afirmar que una prueba fue aprobada si no se ejecutó.
13. Restricciones de Git y archivos
Sin autorización explícita:
- No realizar commits.
- No realizar push.
- No cambiar de rama.
- No realizar merge.
- No realizar rebase.
- No utilizar git reset --hard.
- No utilizar git clean.
- No descartar modificaciones locales.
- No eliminar archivos o funcionalidades existentes.
- No sobrescribir trabajo de otros integrantes.
- No modificar repositorios externos.
- No ejecutar operaciones destructivas sobre bases de datos.
Mantener la carpeta Documentacion/ excluida del control de versiones.

No publicar documentación académica privada, credenciales, secretos ni archivos sensibles.
14. Formato de comunicación
Responder siempre en español.

Explicar los cambios con claridad, utilizando terminología técnica comprensible para un estudiante.

Al finalizar cada etapa, entregar un informe que incluya:
1. Objetivo de la etapa.
2. Archivos creados.
3. Archivos modificados.
4. Archivos eliminados, si fueron autorizados.
5. Explicación de los cambios.
6. Justificación técnica y académica.
7. Comandos ejecutados.
8. Resultados de compilación.
9. Resultados de pruebas.
10. Errores, riesgos y limitaciones pendientes.
11. Impacto sobre otros microservicios y el frontend.
12. Recomendación para la siguiente etapa.
Diferenciar claramente entre funcionalidades implementadas, funcionalidades verificadas y funcionalidades pendientes.

Detenerse al finalizar la tarea autorizada y esperar nuevas instrucciones.
15. Principio fundamental
Priorizar un proyecto funcional, seguro, mantenible y coherente con la evaluación académica por encima de una solución innecesariamente compleja.

El estudiante debe poder comprender, explicar y defender individualmente cada implementación realizada.