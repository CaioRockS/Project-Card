import { AuthService } from './../auth/auth-service';
import { Component, inject } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-template',
  imports: [RouterOutlet, RouterModule],
  templateUrl: './template.html',
  styleUrl: './template.scss',
})
export class Template {
  AuthService = inject(AuthService);

  sair(){
    this.AuthService.logout(true);
  }
}
