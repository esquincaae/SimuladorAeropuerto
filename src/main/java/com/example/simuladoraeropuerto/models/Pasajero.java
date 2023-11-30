package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pasajero {
    private Circulo representacion;
    private Circulo equipaje; // Atributo para el equipaje

    private AgentePasaporte agentePasaporteAsignado;
    private AgenteEquipaje agenteEquipajeAsignado; // Nuevo atributo para el agente de equipaje asignado

    public Pasajero() {
        Circle circleP = new Circle(10, Color.RED);
        this.representacion = new Circulo(circleP); // Círculo rojo como representación del pasajero
        Circle circleE = new Circle(5, Color.BROWN);
        this.equipaje = new Circulo(circleE); // Círculo más pequeño y café para el equipaje
    }

    public void ModificarRepresentacion(int x, int y, boolean e) {
        this.representacion.setCircle(x, y);
        this.representacion.setEstar(e);
    }

    public void setAgentePasaporteAsignado(AgentePasaporte agente) {
        this.agentePasaporteAsignado = agente;
    }

    public AgentePasaporte getAgentePasaporteAsignado() {
        return this.agentePasaporteAsignado;
    }

    public void setAgenteEquipajeAsignado(AgenteEquipaje agente) {
        this.agenteEquipajeAsignado = agente;
    }

    public AgenteEquipaje getAgenteEquipajeAsignado() {
        return this.agenteEquipajeAsignado;
    }

    public void ModificarEquipaje(int x, int y, boolean e) {
        this.equipaje.setCircle(x, y);
        this.equipaje.setEstar(e);
    }

    public Circulo getRepresentacion() {
        return representacion;
    }

    public Circulo getEquipaje() {
        return equipaje;
    }
}
