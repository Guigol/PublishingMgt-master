import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { managerGuard } from './core/guards/manager.guard';
import { userGuard } from './core/guards/user.guard';
import { HomeComponent } from './pages/home/home.component';
import { authorGuard } from './core/guards/author.guard';

export const routes: Routes = [

  // HOME
   {
    path: '',
    component: HomeComponent
  },

  // LOGIN
  {
    path: 'login',
    loadChildren: () => import('./features/auth/auth.routes')
      .then(m => m.AUTH_ROUTES)
  },

  // AUTHOR DASHBOARD
   {
  path: 'author-overview',
  canActivate: [authGuard, authorGuard],
  loadComponent: () => import('./features/overview/author-overview.component')
    .then(m => m.AuthorOverviewComponent)
  },
  {
    path: 'author-dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard-author.component')
      .then(m => m.DashboardAuthorComponent)
  },

  // USER DASHBOARD
  {
  path: 'user-overview',
  canActivate: [authGuard, userGuard],
  loadComponent: () => import('./features/overview/user-overview.component')
    .then(m => m.UserOverviewComponent)
  },
  {
  path: 'user-dashboard',
  canActivate: [authGuard, userGuard],
  loadComponent: () =>
    import('./features/dashboard/user-dashboard.component')
    .then(m => m.UserDashboardComponent)
},

  // MANAGER DASHBOARD
   {
  path: 'mgr-overview',
  canActivate: [authGuard, managerGuard],
  loadComponent: () => import('./features/overview/mgr-overview.component')
    .then(m => m.MgrOverviewComponent)
  },
  {
  path: 'manager-dashboard',
  canActivate: [authGuard, managerGuard],
  loadComponent: () => import('./features/dashboard/manager-dashboard.component')
    .then(m => m.ManagerDashboardComponent),
  children: [
    {
      path: 'authors',
      loadComponent: () => import('./features/manager/author-read-manager.component')
        .then(m => m.AuthorReadMgrComponent)
    },
    {
      path: 'books',
      loadComponent: () => import('./features/admin/books-crud.component')
        .then(m => m.BooksCrudComponent)
    },
    {
      path: 'publishers',
      loadComponent: () => import('./features/admin/publisher-crud.component')
        .then(m => m.PublisherCrudComponent)
    },
    {
      path: 'publishing',
      loadComponent: () => import('./features/admin/publishing-crud.component')
        .then(m => m.PublishingCrudComponent)
    },
    {
      path: 'booksales',
      loadComponent: () => import('./features/admin/book-sales-crud.component')
        .then(m => m.BookSalesCrudComponent)
    },
    {
      path: 'authorparticipation',
      canActivate: [authGuard, managerGuard],
      loadComponent: () =>
      import('./features/manager/author-participation-manager.component')
      .then(m => m.AuthorParticipationManagerComponent)
    },
    {
      path: 'royalties',
      loadComponent: () => import('./features/royalties/admmgr-royalties.component')
        .then(m => m.ManagerRoyaltiesComponent)
    },

    // default redirection
    {
      path: '',
      redirectTo: 'authors',
      pathMatch: 'full'
    }
  ],
},

  // ADMIN DASHBOARD  with CRUD's children routes

  {
  path: 'admin-overview',
  canActivate: [authGuard, adminGuard],
  loadComponent: () => import('./features/overview/admin-overview.component')
    .then(m => m.AdminOverviewComponent)
  },
    {
    path: 'admin-dashboard',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent),
    children: [
      {
        path: 'users',
        canActivate: [authGuard, adminGuard],
        loadComponent: () => import('./features/admin/users-crud.component')
          .then(m => m.UsersCrudComponent)
       },
       {
        path: 'authors',
        canActivate: [authGuard, adminGuard],
         loadComponent: () => import('./features/admin/authors-crud.component')
         .then(m => m.AuthorsCrudComponent)
       },

       {
         path: 'books',
         canActivate: [authGuard, adminGuard],
         loadComponent: () => import('./features/admin/books-crud.component')
         .then(m => m.BooksCrudComponent)
       },
       
       {
         path: 'publishers',
         canActivate: [authGuard, adminGuard],
         loadComponent: () => import('./features/admin/publisher-crud.component')
        .then(m => m.PublisherCrudComponent)
       },
       {
        path: 'publishing',
        canActivate: [authGuard, adminGuard],
        loadComponent: () => import('./features/admin/publishing-crud.component')
        .then(m => m.PublishingCrudComponent)
      },
        {
         path: 'booksales',
         canActivate: [authGuard, adminGuard],
         loadComponent: () => import('./features/admin/book-sales-crud.component')
         .then(m => m.BookSalesCrudComponent)
      },
      {
         path: 'authorparticipation',
         canActivate: [authGuard, adminGuard],
         loadComponent: () => import('./features/admin/author-participation-crud.component')
         .then(m => m.AuthorParticipationCrudComponent)
      },
      {
          path: 'royalties',
          loadComponent: () => import('./features/royalties/admmgr-royalties.component')
            .then(m => m.ManagerRoyaltiesComponent)
        },

      // default redirection
            {
            path: '',
            redirectTo: 'users',
              pathMatch: 'full'
            }
          ]
        },

  // ROOT REDIRECTION
  {
  path: '',
  redirectTo: '/',
  pathMatch: 'full'
},
{
  path: '**',
  redirectTo: '/'
}
];