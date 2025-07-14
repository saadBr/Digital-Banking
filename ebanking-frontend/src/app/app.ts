import { Component,OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AuthService} from './services/auth.service';
import {NgChartsModule} from 'ng2-charts';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet,FormsModule,ReactiveFormsModule,NgChartsModule],
  standalone: true,
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected title = 'ebanking-frontend';
  constructor(private authService:AuthService) {
  }
  ngOnInit(): void {
    this.authService.loadJwtTokenFromLocalStorage();
  }
}
