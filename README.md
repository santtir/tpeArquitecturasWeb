Endpoints (a–h)

Base URL: http://localhost:8080

(a) Alta de estudiante
<img width="884" height="800" alt="queryAPostMan" src="https://github.com/user-attachments/assets/60463fa4-0540-418e-8020-b06d7528085e" />


POST /estudiante

{
  "nombres": "Lautaro",
  "apellido": "Reyes",
  "edad": 24,
  "genero": "Male",
  "nroDocumento": "40112222",
  "ciudadResidencia": "Tandil",
  "nroLibretaUniversitaria": "LU-2024"
}

(b) Matricular estudiante en carrera

POST /estudiante/{idEst}/matricular/{idCar}

Evita duplicados activos con existsByEstudianteIdAndCarreraIdAndGraduadoFalse.

(c) Listar estudiantes (con orden simple)
<img width="911" height="753" alt="Captura de pantalla 2025-10-20 140208" src="https://github.com/user-attachments/assets/370b9dc7-6843-46c3-a896-9cd66335c81f" />

GET /estudiante?sort=<campo>

Campos: nombres, apellido, edad, genero, nroDocumento, ciudadResidencia, nroLibretaUniversitaria

(d) Buscar estudiante por número de libreta
<img width="886" height="796" alt="Captura de pantalla 2025-10-20 145312" src="https://github.com/user-attachments/assets/a6414887-b8be-424f-86df-da60414cc3cd" />

GET /estudiante/libreta/{lu}

(e) Listar estudiantes por género
<img width="882" height="760" alt="Captura de pantalla 2025-10-20 152154" src="https://github.com/user-attachments/assets/e0d533e1-eae7-472b-9ba6-d8393e6f61b9" />

GET /estudiante/genero/{g}

(f) Carreras con inscriptos (desc)
<img width="848" height="734" alt="Captura de pantalla 2025-10-20 165644" src="https://github.com/user-attachments/assets/80321cb8-b40e-465d-a295-8919eeabb9b7" />


GET /reporte/carreras-inscriptos
Devuelve:

{ "idCarrera": 1, "carrera": "TUDAI", "inscriptos": 42 }

(g) Estudiantes de una carrera filtrados por ciudad
<img width="893" height="757" alt="gEstudiantePorCiudad" src="https://github.com/user-attachments/assets/c582f811-188c-4625-b7f5-81963c8d3a8c" />


GET /estudiante/carrera/{carrera}/ciudad/{ciudad}

(h) Reporte anual (inscriptos vs egresados)
<img width="899" height="751" alt="Captura de pantalla 2025-10-20 191032" src="https://github.com/user-attachments/assets/c045748d-572d-411b-91ef-a819e21a4d8b" />

GET /reporte/carreras-anual
Devuelve filas por (carrera, año):

{
  "idCarrera": 1,
  "carrera": "TUDAI",
  "anio": 2024,
  "inscriptos": 12,
  "egresados": 3
}
