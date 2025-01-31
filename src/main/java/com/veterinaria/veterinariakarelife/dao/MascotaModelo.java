
package com.veterinaria.veterinariakarelife.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.veterinaria.veterinariakarelife.interfaces.ModeloInterface;
import com.veterinaria.veterinariakarelife.models.Cliente;
import com.veterinaria.veterinariakarelife.models.Especie;
import com.veterinaria.veterinariakarelife.models.Estado;
import com.veterinaria.veterinariakarelife.models.Mascota;
import com.veterinaria.veterinariakarelife.models.Raza;

public class MascotaModelo implements ModeloInterface<Mascota> {
    
    private final Connection connection;

    public MascotaModelo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Mascota> ejecutarSP(String accion, Mascota mascota) throws SQLException {
        String sql = "{call usp_Mascotas(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accion);

            switch (accion) {
                case "Insert":
                    stmt.setObject(2, null); // mascota_id
                    stmt.setString(3, mascota.getNombre());
                    stmt.setInt(4, mascota.getEspecie().getId());
                    stmt.setInt(5, mascota.getRaza().getId());
                    stmt.setDate(6, java.sql.Date.valueOf(mascota.getFecha_nacimiento()));
                    stmt.setInt(7, mascota.getCliente().getId());
                    stmt.setInt(8, mascota.getEstado().getId() != 0 ? mascota.getEstado().getId() : null);
                    stmt.executeUpdate();
                    return null;
                case "Update":
                    stmt.setInt(2, mascota.getId());
                    stmt.setString(3, mascota.getNombre());
                    stmt.setInt(4, mascota.getEspecie().getId());
                    stmt.setInt(5, mascota.getRaza().getId());
                    stmt.setDate(6, java.sql.Date.valueOf(mascota.getFecha_nacimiento()));
                    stmt.setInt(7, mascota.getCliente().getId());
                    stmt.setObject(8, mascota.getEstado().getId() != 0 ? mascota.getEstado().getId() : null);
                    stmt.executeUpdate();
                    return null;
                case "Select":
                    stmt.setObject(2, null); // mascota_id
                    stmt.setObject(3, null); // nombre
                    stmt.setObject(4, null); // especie_id
                    stmt.setObject(5, null); // raza_id
                    stmt.setObject(6, null); // fecha_nacimiento
                    stmt.setObject(7, null); // cliente_id
                    stmt.setObject(8, null); // estado_id
                    ResultSet rs = stmt.executeQuery();
                    List<Mascota> mascotas = new ArrayList<>();
                    while (rs.next()) {
                        Cliente cliente = new Cliente(rs.getInt("cliente_id"), null, null, null, null, null, null);
                        Estado estado = new Estado(rs.getInt("estado_id"), null);
                        Especie especie = new Especie(rs.getInt("especie_id"), null, estado);
                        Raza raza = new Raza(rs.getInt("raza_id"), null, especie, estado);
                        Mascota m = new Mascota(
                                rs.getInt("mascota_id"),
                                rs.getString("nombre"),
                                especie,
                                raza,
                                rs.getDate("fecha_nacimiento").toLocalDate(),
                                cliente,
                                estado);
                        mascotas.add(m);
                    }
                    return mascotas;
                case "Status":
                    stmt.setInt(2, mascota.getId());
                    stmt.setInt(8, mascota.getEstado().getId());
                    stmt.executeUpdate();
                    return null;
                default:
                    break;
            }
        }
        return null;
    }

}
