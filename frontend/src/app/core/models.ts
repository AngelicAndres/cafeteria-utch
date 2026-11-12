// Modelos del frontend: reflejan los DTOs del backend (Diagrama de clases).

export type Rol = 'CLIENTE' | 'PERSONAL' | 'ADMIN';

export type EstadoPedido = 'RECIBIDO' | 'EN_PREPARACION' | 'LISTO' | 'ENTREGADO';

export const ESTADOS: EstadoPedido[] = ['RECIBIDO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO'];

export const ETIQUETA_ESTADO: Record<EstadoPedido, string> = {
  RECIBIDO: 'Recibido',
  EN_PREPARACION: 'En preparación',
  LISTO: 'Listo',
  ENTREGADO: 'Entregado',
};

export interface LoginResponse {
  token: string;
  tipo: string;
  nombre: string;
  email: string;
  rol: Rol;
}

export interface Producto {
  id: number;
  nombre: string;
  precio: number;
  disponible: boolean;
}

export interface ProductoRequest {
  nombre: string;
  precio: number;
  disponible: boolean;
}

export interface ItemPedido {
  productoId: number;
  cantidad: number;
}

export interface DetallePedido {
  productoId: number;
  producto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Pedido {
  id: number;
  cliente: string;
  fecha: string;
  estado: EstadoPedido;
  siguienteEstado: EstadoPedido | null;
  total: number;
  detalles: DetallePedido[];
}
