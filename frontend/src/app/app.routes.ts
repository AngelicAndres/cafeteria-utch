import { Routes } from '@angular/router';
import { roleGuard } from './core/role.guard';
import { AdminProductosComponent } from './pages/admin-productos/admin-productos.component';
import { LoginComponent } from './pages/login/login.component';
import { MenuComponent } from './pages/menu/menu.component';
import { MisPedidosComponent } from './pages/mis-pedidos/mis-pedidos.component';
import { PanelPersonalComponent } from './pages/panel-personal/panel-personal.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Iniciar sesión' },
  { path: 'menu', component: MenuComponent, canActivate: [roleGuard('CLIENTE')], title: 'Menú' },
  { path: 'mis-pedidos', component: MisPedidosComponent, canActivate: [roleGuard('CLIENTE')], title: 'Mis pedidos' },
  { path: 'personal', component: PanelPersonalComponent, canActivate: [roleGuard('PERSONAL')], title: 'Panel del personal' },
  { path: 'admin', component: AdminProductosComponent, canActivate: [roleGuard('ADMIN')], title: 'Productos' },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
