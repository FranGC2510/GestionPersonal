package org.dam.fcojavier.gestionpersonal;

import org.dam.fcojavier.gestionpersonal.DAOs.*;
import org.dam.fcojavier.gestionpersonal.enums.EstadoAusencia;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestDAOs {
    public static void main(String[] args) {
        //testEmpresa();
        System.out.println("\n------------------------------\n");
        //testEmpleado();
        System.out.println("\n------------------------------\n");
        //testSupervision();
        System.out.println("\n------------------------------\n");
        //testAusencia();
        System.out.println("\n------------------------------\n");
        //testSolicitaAusencia();
        System.out.println("\n------------------------------\n");
        //testTurno();
        System.out.println("\n------------------------------\n");
        testPerteneceTurno();

    }

    private static void testEmpresa() {
        System.out.println("PRUEBAS DE EMPRESA DAO");
        EmpresaDAO empresaDAO = new EmpresaDAO();

        try {
            // Test INSERT
            System.out.println("\nPrueba de INSERT:");
            Empresa nuevaEmpresa = new Empresa("Tech Solutions", "Calle Principal 123", "555-0123", "info@techsolutions.com", "password123");
            Empresa empresaInsertada = empresaDAO.insert(nuevaEmpresa);
            if (empresaInsertada != null) {
                System.out.println("Empresa insertada correctamente: " + empresaInsertada);
            }

            // Test FIND BY ID
            System.out.println("\nPrueba de FIND BY ID:");
            Empresa empresaEncontrada = empresaDAO.findById(empresaInsertada.getIdEmpresa());
            if (empresaEncontrada != null) {
                System.out.println("Empresa encontrada: " + empresaEncontrada);
            }

            // Test UPDATE
            System.out.println("\nPrueba de UPDATE:");
            empresaEncontrada.setDireccion("Nueva Dirección 456");
            Empresa empresaActualizada = empresaDAO.update(empresaEncontrada);
            if (empresaActualizada != null) {
                System.out.println("Empresa actualizada: " + empresaActualizada);
            }

            // Test FIND ALL
            System.out.println("\nPrueba de FIND ALL:");
            for (Empresa empresa : empresaDAO.findAll()) {
                System.out.println(empresa);
            }

            // Test DELETE
            System.out.println("\nPrueba de DELETE:");
            if (empresaDAO.delete(empresaInsertada)) {
                System.out.println("Empresa eliminada correctamente");
            }

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de EmpresaDAO: " + e.getMessage());
        }
    }

    private static void testEmpleado() {
        System.out.println("PRUEBAS DE EMPLEADO DAO");
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        EmpresaDAO empresaDAO = new EmpresaDAO();

        try {
            // Primero creamos una empresa para asociar al empleado
            Empresa empresa = new Empresa("Tech Solutions", "Calle Principal 123", "555-0123", "info@techsolutions.com", "password123");
            empresa = empresaDAO.insert(empresa);

            // Test INSERT
            System.out.println("\nPrueba de INSERT:");
            Empleado nuevoEmpleado = new Empleado(empresa, "Juan", "Pérez", "666-555-444", "juan@techsolutions.com", "pass123");
            nuevoEmpleado.setDepartamento("IT");
            nuevoEmpleado.setPuesto("Desarrollador");
            nuevoEmpleado.setRol(TipoEmpleado.EMPLEADO);

            Empleado empleadoInsertado = empleadoDAO.insert(nuevoEmpleado);
            if (empleadoInsertado != null) {
                System.out.println("Empleado insertado correctamente: " + empleadoInsertado);
            }

            // Test FIND BY ID
            System.out.println("\nPrueba de FIND BY ID:");
            Empleado empleadoEncontrado = empleadoDAO.findById(empleadoInsertado.getIdEmpleado());
            if (empleadoEncontrado != null) {
                System.out.println("Empleado encontrado: " + empleadoEncontrado);
            }

            // Test UPDATE
            System.out.println("\nPrueba de UPDATE:");
            empleadoEncontrado.setPuesto("Senior Developer");
            empleadoEncontrado.setRol(TipoEmpleado.SUPERVISOR);
            Empleado empleadoActualizado = empleadoDAO.update(empleadoEncontrado);
            if (empleadoActualizado != null) {
                System.out.println("Empleado actualizado: " + empleadoActualizado);
            }

            // Test FIND ALL
            System.out.println("\nPrueba de FIND ALL:");
            for (Empleado empleado : empleadoDAO.findAll()) {
                System.out.println(empleado);
            }

            // Test DELETE
            System.out.println("\nPrueba de DELETE:");
            if (empleadoDAO.delete(empleadoInsertado)) {
                System.out.println("Empleado eliminado correctamente");
            }

            // Limpiamos la empresa creada
            empresaDAO.delete(empresa);

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de EmpleadoDAO: " + e.getMessage());
        }
    }

    private static void testSupervision() {
        System.out.println("PRUEBAS DE SUPERVISION DAO");
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        EmpresaDAO empresaDAO = new EmpresaDAO();
        SupervisaEmpleadoDAO supervisionDAO = new SupervisaEmpleadoDAO();

        try {
            // Primero creamos una empresa para los empleados
            Empresa empresa = new Empresa("Tech Solutions", "Calle Principal 123",
                    "555-0123", "info@techsolutions.com", "password123");
            empresa = empresaDAO.insert(empresa);

            // Creamos un supervisor
            Empleado supervisor = new Empleado(empresa, "Ana", "García",
                    "666-777-888", "ana@techsolutions.com", "pass123");
            supervisor.setDepartamento("IT");
            supervisor.setPuesto("Team Lead");
            supervisor.setRol(TipoEmpleado.SUPERVISOR);
            supervisor = empleadoDAO.insert(supervisor);

            // Creamos un empleado
            Empleado empleado = new Empleado(empresa, "Juan", "Pérez",
                    "666-555-444", "juan@techsolutions.com", "pass123");
            empleado.setDepartamento("IT");
            empleado.setPuesto("Desarrollador");
            empleado.setRol(TipoEmpleado.EMPLEADO);
            empleado = empleadoDAO.insert(empleado);

            // Test ASIGNAR SUPERVISOR
            System.out.println("\nPrueba de ASIGNAR SUPERVISOR:");
            SupervisaEmpleado supervision = new SupervisaEmpleado(supervisor, empleado, LocalDate.now());
            SupervisaEmpleado supervisionCreada = supervisionDAO.asignarSupervisor(supervision);
            if (supervisionCreada != null) {
                System.out.println("Supervisión creada correctamente: " + supervisionCreada);
            }

            // Test OBTENER SUPERVISOR ACTUAL
            System.out.println("\nPrueba de OBTENER SUPERVISOR ACTUAL:");
            SupervisaEmpleado supervisionActual = supervisionDAO.obtenerSupervisorActual(empleado);
            if (supervisionActual != null) {
                System.out.println("Supervisor actual encontrado: " + supervisionActual);
            }
            /*
            // Test FINALIZAR SUPERVISION
            System.out.println("\nPrueba de FINALIZAR SUPERVISION:");
            supervisionActual.setFechaFin(LocalDate.now());
            SupervisaEmpleado supervisionFinalizada = supervisionDAO.finalizarSupervision(supervisionActual);
            if (supervisionFinalizada != null) {
                System.out.println("Supervisión finalizada correctamente: " + supervisionFinalizada);
            }
            */
            // Test OBTENER HISTORICO
            System.out.println("\nPrueba de OBTENER HISTORICO:");
            List<SupervisaEmpleado> historico = supervisionDAO.obtenerHistoricoSupervisoresByEmpleado(empleado);
            for (SupervisaEmpleado sup : historico) {
                System.out.println(sup);
            }

            // Test OBTENER EMPLEADOS SUPERVISADOS
            System.out.println("\nPrueba de OBTENER EMPLEADOS SUPERVISADOS:");
            List<SupervisaEmpleado> empleadosSupervisados = supervisionDAO.obtenerEmpleadosSupervisados(supervisor);
            System.out.println("Empleados supervisados por " + supervisor.getNombre() + " " + supervisor.getApellido() + ":");
            for (SupervisaEmpleado sup : empleadosSupervisados) {
                System.out.println("- " + sup.getEmpleado().getNombre() +
                        " " + sup.getEmpleado().getApellido() +
                        " (desde: " + sup.getFechaInicio() + ")");
            }


            // Limpieza de datos de prueba
            System.out.println("\nLimpiando datos de prueba...");
            empleadoDAO.delete(empleado);
            empleadoDAO.delete(supervisor);
            empresaDAO.delete(empresa);
            System.out.println("Datos de prueba eliminados correctamente");

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de SupervisionDAO: " + e.getMessage());
        }
    }

    private static void testAusencia() {
        System.out.println("PRUEBAS DE AUSENCIA DAO");
        AusenciaDAO ausenciaDAO = new AusenciaDAO();

        try {
            // Test INSERT
            System.out.println("\nPrueba de INSERT:");
            Ausencia nuevaAusencia = new Ausencia();
            nuevaAusencia.setMotivo("Baja por enfermedad");
            Ausencia ausenciaInsertada = ausenciaDAO.insert(nuevaAusencia);
            if (ausenciaInsertada != null) {
                System.out.println("Ausencia insertada correctamente: " + ausenciaInsertada);
            }

            // Test FIND BY ID
            System.out.println("\nPrueba de FIND BY ID:");
            Ausencia ausenciaEncontrada = ausenciaDAO.findById(ausenciaInsertada.getIdAusencia());
            if (ausenciaEncontrada != null) {
                System.out.println("Ausencia encontrada: " + ausenciaEncontrada);
            }

            // Test UPDATE
            System.out.println("\nPrueba de UPDATE:");
            ausenciaEncontrada.setMotivo("Baja por COVID");
            Ausencia ausenciaActualizada = ausenciaDAO.update(ausenciaEncontrada);
            if (ausenciaActualizada != null) {
                System.out.println("Ausencia actualizada: " + ausenciaActualizada);
            }

            // Test FIND ALL
            System.out.println("\nPrueba de FIND ALL:");
            for (Ausencia ausencia : ausenciaDAO.findAll()) {
                System.out.println(ausencia);
            }

            // Test DELETE
            System.out.println("\nPrueba de DELETE:");
            if (ausenciaDAO.delete(ausenciaInsertada)) {
                System.out.println("Ausencia eliminada correctamente");
            }

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de AusenciaDAO: " + e.getMessage());
        }
    }

    private static void testSolicitaAusencia() {
        System.out.println("PRUEBAS DE SOLICITA AUSENCIA DAO");
        SolicitaAusenciaDAO solicitaAusenciaDAO = new SolicitaAusenciaDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        EmpresaDAO empresaDAO = new EmpresaDAO();
        AusenciaDAO ausenciaDAO = new AusenciaDAO();

        try {
            // Creamos datos necesarios para las pruebas
            // 1. Empresa
            Empresa empresa = new Empresa("Tech Solutions", "Calle Principal 123",
                    "555-0123", "info@techsolutions.com", "password123");
            empresa = empresaDAO.insert(empresa);

            // 2. Empleado
            Empleado empleado = new Empleado(empresa, "Juan", "Pérez",
                    "666-555-444", "juan@techsolutions.com", "pass123");
            empleado.setDepartamento("IT");
            empleado.setPuesto("Desarrollador");
            empleado.setRol(TipoEmpleado.EMPLEADO);
            empleado = empleadoDAO.insert(empleado);

            // 3. Ausencia
            Ausencia ausencia = new Ausencia();
            ausencia.setMotivo("Vacaciones");
            ausencia = ausenciaDAO.insert(ausencia);

            // Test INSERT
            System.out.println("\nPrueba de INSERT:");
            SolicitaAusencia nuevaSolicitud = new SolicitaAusencia();
            nuevaSolicitud.setEmpleado(empleado);
            nuevaSolicitud.setAusencia(ausencia);
            nuevaSolicitud.setFechaSolicitud(LocalDate.now());
            nuevaSolicitud.setFechaInicio(LocalDate.now().plusDays(5));
            nuevaSolicitud.setFechaFin(LocalDate.now().plusDays(10));
            nuevaSolicitud.setEstado(EstadoAusencia.pendiente);

            SolicitaAusencia solicitudInsertada = solicitaAusenciaDAO.insert(nuevaSolicitud);
            if (solicitudInsertada != null) {
                System.out.println("Solicitud insertada correctamente: " + solicitudInsertada);
            }

            // Test FIND BY ID
            System.out.println("\nPrueba de FIND BY ID:");
            SolicitaAusencia solicitudEncontrada = solicitaAusenciaDAO.findById(
                    empleado.getIdEmpleado(),
                    ausencia.getIdAusencia(),
                    nuevaSolicitud.getFechaSolicitud()
            );
            if (solicitudEncontrada != null) {
                System.out.println("Solicitud encontrada: " + solicitudEncontrada);
            }

            // Test UPDATE ESTADO
            System.out.println("\nPrueba de UPDATE ESTADO:");
            solicitudEncontrada.setEstado(EstadoAusencia.aprobada);
            SolicitaAusencia solicitudActualizada = solicitaAusenciaDAO.updateEstado(solicitudEncontrada);
            if (solicitudActualizada != null) {
                System.out.println("Estado de solicitud actualizado: " + solicitudActualizada);
            }

            // Test FIND BY EMPLEADO
            System.out.println("\nPrueba de FIND BY EMPLEADO:");
            List<SolicitaAusencia> solicitudesEmpleado = solicitaAusenciaDAO.findByEmpleado(empleado);
            for (SolicitaAusencia solicitud : solicitudesEmpleado) {
                System.out.println(solicitud);
            }

            // Test FIND BY ESTADO
            System.out.println("\nPrueba de FIND BY ESTADO:");
            List<SolicitaAusencia> solicitudesAprobadas = solicitaAusenciaDAO.findByEstado(EstadoAusencia.aprobada);
            for (SolicitaAusencia solicitud : solicitudesAprobadas) {
                System.out.println(solicitud);
            }

            // Test GET AUSENCIAS ACTIVAS
            System.out.println("\nPrueba de GET AUSENCIAS ACTIVAS:");
            List<SolicitaAusencia> ausenciasActivas = solicitaAusenciaDAO.getAusenciasActivas();
            for (SolicitaAusencia solicitud : ausenciasActivas) {
                System.out.println(solicitud);
            }

            // Limpieza de datos de prueba
            System.out.println("\nLimpiando datos de prueba...");
            ausenciaDAO.delete(ausencia);
            empleadoDAO.delete(empleado);
            empresaDAO.delete(empresa);
            System.out.println("Datos de prueba eliminados correctamente");

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de SolicitaAusenciaDAO: " + e.getMessage());
        }
    }

    private static void testTurno() {
        System.out.println("PRUEBAS DE TURNO DAO");
        TurnoDAO turnoDAO = new TurnoDAO();

        try {
            // Test INSERT
            System.out.println("\nPrueba de INSERT:");
            Turno nuevoTurno = new Turno(
                    "Turno Mañana",
                    LocalTime.of(8, 0),
                    LocalTime.of(16, 0)
            );
            Turno turnoInsertado = turnoDAO.insert(nuevoTurno);
            if (turnoInsertado != null) {
                System.out.println("Turno insertado correctamente: " + turnoInsertado);
                System.out.println("Duración del turno: " + turnoInsertado.getDuracionHoras() + " horas");
            }

            // Test FIND BY ID
            System.out.println("\nPrueba de FIND BY ID:");
            Turno turnoEncontrado = turnoDAO.findById(turnoInsertado.getIdTurno());
            if (turnoEncontrado != null) {
                System.out.println("Turno encontrado: " + turnoEncontrado);
            }

            // Test UPDATE
            System.out.println("\nPrueba de UPDATE:");
            turnoEncontrado.setDescripcion("Turno Mañana Modificado");
            turnoEncontrado.setHoraInicio(LocalTime.of(7, 0));
            turnoEncontrado.setHoraFin(LocalTime.of(15, 0));
            Turno turnoActualizado = turnoDAO.update(turnoEncontrado);
            if (turnoActualizado != null) {
                System.out.println("Turno actualizado: " + turnoActualizado);
                System.out.println("Nueva duración del turno: " + turnoActualizado.getDuracionHoras() + " horas");
            }

            // Test FIND ALL
            System.out.println("\nPrueba de FIND ALL:");
            for (Turno turno : turnoDAO.findAll()) {
                System.out.println(turno);
            }

            // Test DELETE
            System.out.println("\nPrueba de DELETE:");
            if (turnoDAO.delete(turnoInsertado)) {
                System.out.println("Turno eliminado correctamente");
            }

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de TurnoDAO: " + e.getMessage());
        }
    }

    private static void testPerteneceTurno() {
        System.out.println("PRUEBAS DE PERTENECE TURNO DAO");
        PerteneceTurnoDAO perteneceTurnoDAO = new PerteneceTurnoDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        EmpresaDAO empresaDAO = new EmpresaDAO();
        TurnoDAO turnoDAO = new TurnoDAO();

        try {
            // Creamos datos necesarios para las pruebas
            // 1. Empresa
            Empresa empresa = new Empresa("Tech Solutions", "Calle Principal 123",
                    "555-0123", "info@techsolutions.com", "password123");
            empresa = empresaDAO.insert(empresa);

            // 2. Empleado
            Empleado empleado = new Empleado(empresa, "Juan", "Pérez",
                    "666-555-444", "juan@techsolutions.com", "pass123");
            empleado.setDepartamento("IT");
            empleado.setPuesto("Desarrollador");
            empleado.setRol(TipoEmpleado.EMPLEADO);
            empleado = empleadoDAO.insert(empleado);

            // 3. Turno
            Turno turno = new Turno("Turno Mañana",
                    LocalTime.of(8, 0),
                    LocalTime.of(16, 0));
            turno = turnoDAO.insert(turno);

            // Test INSERT
            System.out.println("\nPrueba de INSERT:");
            PerteneceTurno nuevaAsignacion = new PerteneceTurno(
                    empleado,
                    turno,
                    LocalDate.now()
            );
            PerteneceTurno asignacionInsertada = perteneceTurnoDAO.insert(nuevaAsignacion);
            if (asignacionInsertada != null) {
                System.out.println("Asignación insertada correctamente: " + asignacionInsertada);
            }

            // Test FIND BY EMPLEADO
            System.out.println("\nPrueba de FIND BY EMPLEADO:");
            List<PerteneceTurno> asignacionesEmpleado = perteneceTurnoDAO.findByEmpleado(empleado);
            for (PerteneceTurno asignacion : asignacionesEmpleado) {
                System.out.println(asignacion);
            }

            // Test FIND BY TURNO
            System.out.println("\nPrueba de FIND BY TURNO:");
            List<PerteneceTurno> asignacionesTurno = perteneceTurnoDAO.findByTurno(turno);
            for (PerteneceTurno asignacion : asignacionesTurno) {
                System.out.println(asignacion);
            }

            // Test FIND BY FECHA
            System.out.println("\nPrueba de FIND BY FECHA:");
            List<PerteneceTurno> asignacionesFecha = perteneceTurnoDAO.findByFecha(LocalDate.now());
            for (PerteneceTurno asignacion : asignacionesFecha) {
                System.out.println(asignacion);
            }

            // Test UPDATE
            System.out.println("\nPrueba de UPDATE:");
            // Creamos un nuevo turno para la actualización
            Turno turnoNuevo = new Turno("Turno Tarde",
                    LocalTime.of(16, 0),
                    LocalTime.of(0, 0));
            turnoNuevo = turnoDAO.insert(turnoNuevo);

            PerteneceTurno asignacionNueva = new PerteneceTurno(
                    empleado,
                    turnoNuevo,
                    LocalDate.now().plusDays(1)
            );

            PerteneceTurno asignacionActualizada = perteneceTurnoDAO.update(asignacionInsertada, asignacionNueva);
            if (asignacionActualizada != null) {
                System.out.println("Asignación actualizada correctamente: " + asignacionActualizada);
            }

            // Test DELETE
            System.out.println("\nPrueba de DELETE:");
            if (perteneceTurnoDAO.delete(asignacionActualizada)) {
                System.out.println("Asignación eliminada correctamente");
            }

            // Limpieza de datos de prueba
            System.out.println("\nLimpiando datos de prueba...");
            turnoDAO.delete(turno);
            turnoDAO.delete(turnoNuevo);
            empleadoDAO.delete(empleado);
            empresaDAO.delete(empresa);
            System.out.println("Datos de prueba eliminados correctamente");

        } catch (DAOException e) {
            System.err.println("Error en las pruebas de PerteneceTurnoDAO: " + e.getMessage());
        }
    }

}
