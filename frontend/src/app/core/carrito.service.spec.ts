import { CarritoService } from './carrito.service';
import { Producto } from './models';

describe('CarritoService', () => {
  const capuchino: Producto = { id: 2, nombre: 'Capuchino', precio: 35, disponible: true };
  const jugo: Producto = { id: 8, nombre: 'Jugo de naranja', precio: 28, disponible: false };

  it('acumula cantidades y calcula el total estimado', () => {
    const c = new CarritoService();
    c.agregar(capuchino);
    c.agregar(capuchino);
    expect(c.items()[0].cantidad).toBe(2);
    expect(c.total()).toBe(70);
  });

  it('no agrega productos no disponibles (RN3)', () => {
    const c = new CarritoService();
    c.agregar(jugo);
    expect(c.items().length).toBe(0);
  });

  it('no permite cantidades menores a 1 (RN2)', () => {
    const c = new CarritoService();
    c.agregar(capuchino);
    c.cambiarCantidad(capuchino.id, 0);
    expect(c.items()[0].cantidad).toBe(1);
  });
});
