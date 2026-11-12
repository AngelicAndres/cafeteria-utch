package mx.utch.cafeteria;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integracion del flujo funcional minimo obligatorio, de extremo a extremo
 * sobre la API real (seguridad + servicios + JPA + H2 en memoria):
 * consultar menu -> seleccionar -> registrar pedido -> calcular total -> actualizar estado -> consultar.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FlujoPedidoIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private String login(String email, String password) throws Exception {
        MvcResult r = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return om.readTree(r.getResponse().getContentAsString()).get("token").asText();
    }

    private long idProducto(JsonNode menu, String nombre) {
        for (JsonNode p : menu) {
            if (p.get("nombre").asText().equals(nombre)) {
                return p.get("id").asLong();
            }
        }
        throw new IllegalStateException("No existe el producto " + nombre);
    }

    @Test
    void flujoCompletoDelPedido() throws Exception {
        String cliente = login("cliente@utch.mx", "cliente123");
        String personal = login("personal@utch.mx", "personal123");

        // 1. Consultar menu
        MvcResult menuRes = mvc.perform(get("/api/productos").header("Authorization", "Bearer " + cliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andReturn();
        JsonNode menu = om.readTree(menuRes.getResponse().getContentAsString());
        long capuchino = idProducto(menu, "Capuchino");
        long burrito = idProducto(menu, "Burrito de deshebrada");

        // 2-4. Seleccionar, registrar pedido y calcular total: 2 x 35 + 1 x 40 = 110
        String body = "{\"items\":[{\"productoId\":" + capuchino + ",\"cantidad\":2},"
                + "{\"productoId\":" + burrito + ",\"cantidad\":1}]}";
        MvcResult creado = mvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RECIBIDO"))
                .andExpect(jsonPath("$.total").value(110.0))
                .andExpect(jsonPath("$.detalles", hasSize(2)))
                .andReturn();
        long pedidoId = om.readTree(creado.getResponse().getContentAsString()).get("id").asLong();

        // 5. Actualizar estado (Personal) en orden
        for (String estado : new String[]{"EN_PREPARACION", "LISTO"}) {
            mvc.perform(patch("/api/pedidos/" + pedidoId + "/estado")
                            .header("Authorization", "Bearer " + personal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"estado\":\"" + estado + "\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value(estado));
        }

        // 6. Consultar (Cliente)
        mvc.perform(get("/api/pedidos/mis-pedidos").header("Authorization", "Bearer " + cliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pedidoId))
                .andExpect(jsonPath("$[0].estado").value("LISTO"));
    }

    @Test
    void rechazaProductoNoDisponible() throws Exception {
        String cliente = login("cliente@utch.mx", "cliente123");
        MvcResult menuRes = mvc.perform(get("/api/productos").header("Authorization", "Bearer " + cliente))
                .andReturn();
        long jugo = idProducto(om.readTree(menuRes.getResponse().getContentAsString()), "Jugo de naranja");

        mvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productoId\":" + jugo + ",\"cantidad\":1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("RN3")));
    }

    @Test
    void rechazaPedidoVacioYCantidadCero() throws Exception {
        String cliente = login("cliente@utch.mx", "cliente123");
        mvc.perform(post("/api/pedidos").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"items\":[]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/pedidos").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productoId\":1,\"cantidad\":0}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clienteNoPuedeCambiarEstadoNiCrearProductos() throws Exception {
        String cliente = login("cliente@utch.mx", "cliente123");
        mvc.perform(patch("/api/pedidos/1/estado").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"EN_PREPARACION\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/productos").header("Authorization", "Bearer " + cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Te\",\"precio\":20,\"disponible\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinTokenResponde401() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
    }
}
