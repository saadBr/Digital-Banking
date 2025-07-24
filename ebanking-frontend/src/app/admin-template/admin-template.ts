import { Component } from '@angular/core';
import { Navbar } from '../navbar/navbar';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-template',
  imports: [Navbar, RouterModule],
  standalone: true,
  templateUrl: './admin-template.html',
  styleUrl: './admin-template.css',
})
export class AdminTemplate {}
